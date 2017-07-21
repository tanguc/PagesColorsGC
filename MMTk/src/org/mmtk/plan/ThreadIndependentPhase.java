package org.mmtk.plan;

import org.mmtk.utility.Log;
import org.mmtk.utility.options.Options;
import org.mmtk.vm.VM;
import org.vmmagic.pragma.Inline;
import org.vmmagic.pragma.Uninterruptible;

@Uninterruptible
public class ThreadIndependentPhase {

  /***********************************************************************
   *
   * Phase stack
   */

  /** The maximum stack depth for the phase stack. */
  private static final int MAX_PHASE_STACK_DEPTH = Phase.MAX_PHASES;

  /** Stores the current sub phase for a complex phase. Each entry corresponds to a phase stack entry */
  private int[] complexPhaseCursor = new int[MAX_PHASE_STACK_DEPTH];

  /** The phase stack. Stores the current nesting of phases */
  private int[] phaseStack = new int[MAX_PHASE_STACK_DEPTH];

  /** The current stack pointer */
  private int phaseStackPointer = -1;

  /**
   * The current even (0 mod 2) scheduled phase.
   * As we only sync at the end of a phase we need this to ensure that
   * the primary thread setting the phase does not race with the other
   * threads reading it.
   */
  private int evenScheduledPhase;

  /**
   * The current odd (1 mod 2) scheduled phase.
   * As we only sync at the end of a phase we need this to ensure that
   * the primary thread setting the phase does not race with the other
   * threads reading it.
   */
  private int oddScheduledPhase;

  /**
   * Do we need to add a sync point to reset the mutator count. This
   * is necessary for consecutive mutator phases and unneccessary
   * otherwise. Again we separate in even and odd to ensure that there
   * is no race between the primary thread setting and the helper
   * threads reading.
   */
  private boolean evenMutatorResetRendezvous;

  /**
   * Do we need to add a sync point to reset the mutator count. This
   * is necessary for consecutive mutator phases and unneccessary
   * otherwise. Again we separate in even and odd to ensure that there
   * is no race between the primary thread setting and the helper
   * threads reading.
   */
  private boolean oddMutatorResetRendezvous;

  /**
   * The complex phase whose timer should be started after the next
   * rendezvous. We can not start the timer at the point we determine
   * the next complex phase as we determine the next phase at the
   * end of the previous phase before the sync point.
   */
  private short startComplexTimer;

  /**
   * The complex phase whose timer should be stopped after the next
   * rendezvous. We can not start the timer at the point we determine
   * the next complex phase as we determine the next phase at the
   * end of the previous phase before the sync point.
   */
  private short stopComplexTimer;

	public ThreadIndependentPhase() {
		
	}
	
	public void beginNewPhaseStack(int scheduledPhase) {
    int order = ((ParallelCollector)VM.activePlan.collector()).rendezvous();

    if (order == 0) {
      pushScheduledPhase(scheduledPhase);
    }
    processPhaseStack(false);
  }

  /**
   * Continue the execution of a phase stack. Used for incremental
   * and concurrent collection.
   */
  public void continuePhaseStack() {
    processPhaseStack(true);
  }
  
  /** Get the ordering component of an encoded phase */
  protected short getSchedule(int scheduledPhase) {
    short ordering = (short)((scheduledPhase >> 16) & 0x0000FFFF);
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(ordering > 0);
    return ordering;
  }
  
  /**
   * Retrieve a phase by the unique phase identifier.
   *
   * @param id The phase identifier.
   * @return The Phase instance.
   */
  public Phase getPhase(short id) {
    return Phase.phases[id];
  }

  /** Get the phase id component of an encoded phase */
  protected short getPhaseId(int scheduledPhase) {
    short phaseId = (short)(scheduledPhase & 0x0000FFFF);
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(phaseId > 0);
    return phaseId;
  }

  /**
   * Process the phase stack. This method is called by multiple threads.
   */
  private void processPhaseStack(boolean resume) {
    /* Global and Collector instances used in phases */
    Plan plan = VM.activePlan.global();
    ParallelCollector collector = (ParallelCollector)VM.activePlan.collector();

    int order = collector.rendezvous();
    final boolean primary = order == 0;

    boolean log = Options.verbose.getValue() >= 6;
    boolean logDetails = Options.verbose.getValue() >= 7;

    if (primary && resume) {
      if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(!Phase.isPhaseStackEmpty());
      if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(!Plan.gcInProgress());
      Plan.setGCStatus(Plan.GC_PROPER);
    }

    /* In order to reduce the need for synchronization, we keep an odd or even
     * counter for the number of phases processed. As each phase has a single
     * rendezvous it is only possible to be out by one so the odd or even counter
     * protects us. */
    boolean isEvenPhase = true;

    if (primary) {
      /* First phase will be even, so we say we are odd here so that the next phase set is even*/
      setNextPhase(false, getNextPhase(), false);
    }

    /* Make sure everyone sees the first phase */
    collector.rendezvous();

    /* The main phase execution loop */
    int scheduledPhase;
    while((scheduledPhase = getCurrentPhase(isEvenPhase)) > 0) {
      short schedule = getSchedule(scheduledPhase);
      short phaseId = getPhaseId(scheduledPhase);
      Phase p = getPhase(phaseId);
      //Timer timer = new Timer("", false, true);

      /* Start the timer(s) */
      if (primary) { //!collector.isThreadIndependent && 
        /*if (resume) {
          resumeComplexTimers();
        }*/
        //if (p.timer != null) p.timer.start();
        ///timer.start();
        /*if (startComplexTimer > 0) {
          Phase.getPhase(startComplexTimer).timer.start();
          startComplexTimer = 0;
        }*/
      }

      if (log) {
        Log.write("Execute ");
        p.logPhase();
      }

      /* Execute a single simple scheduled phase */
      switch (schedule) {
        /* Global phase */
        case Phase.SCHEDULE_GLOBAL: {
          if (logDetails) Log.writeln(" as Global...");
          if (primary) {
            if (VM.DEBUG) VM.debugging.globalPhase(phaseId,true);
            plan.collectionPhase(phaseId);
            if (VM.DEBUG) VM.debugging.globalPhase(phaseId,false);
          }
          break;
        }

        /* Collector phase */
        case Phase.SCHEDULE_COLLECTOR: {
          if (logDetails)  {
          	Log.write(" as Collector... "); 
      	    Log.writeln(VM.activePlan.getThreadID());
      	  }
          if (VM.DEBUG) VM.debugging.collectorPhase(phaseId,order,true);
          collector.collectionPhase(phaseId, primary);
          if (VM.DEBUG) VM.debugging.collectorPhase(phaseId,order,false);
          break;
        }

        /* Mutator phase */
        case Phase.SCHEDULE_MUTATOR: {
          MutatorContext mutator;
          mutator = VM.activePlan.getThreadById(collector.mutatorSlot);
	        if (logDetails) {
	        	Log.write(" as Mutator... ");
	        	Log.writeln(mutator.getMonotonicId());
	        }
	        if (VM.DEBUG) VM.debugging.mutatorPhase(phaseId,mutator.getId(),true);
	        mutator.collectionPhase(phaseId, primary);
	        if (VM.DEBUG) VM.debugging.mutatorPhase(phaseId,mutator.getId(),false);
        	break;
       }

        /* Concurrent phase */
        case Phase.SCHEDULE_CONCURRENT: {
          VM.assertions.fail("Concurrent not supported");
        }

        default: {
          /* getNextPhase has done the wrong thing */
          VM.assertions.fail("Invalid schedule in Phase.processPhaseStack");
          break;
        }
      }

      if (primary) {
        /* Set the next phase by processing the stack */
        int next = getNextPhase();
        boolean needsResetRendezvous = (next > 0) && (schedule == Phase.SCHEDULE_MUTATOR && getSchedule(next) == Phase.SCHEDULE_MUTATOR);
        setNextPhase(isEvenPhase, next, needsResetRendezvous);
      }

      /* Sync point after execution of a phase */
      collector.rendezvous();

      /* At this point, in the case of consecutive phases with mutator
       * scheduling, we have to double-synchronize to ensure all
       * collector threads see the reset mutator counter. */
      if (needsMutatorResetRendezvous(isEvenPhase)) {
        collector.rendezvous();
      }

      /* Stop the timer(s) */
      if (primary) { //!collector.isThreadIndependent && 
        /*if (p.timer != null) p.timer.stop();
        if (stopComplexTimer > 0) {
          Phase.getPhase(stopComplexTimer).timer.stop();
          stopComplexTimer = 0;
        }*/
      	///timer.stop();
      }

      /* Flip the even / odd phase sense */
      isEvenPhase = !isEvenPhase;
      resume = false;
    }
  }

  /**
   * Get the next phase.
   */
  private int getCurrentPhase(boolean isEvenPhase) {
    return isEvenPhase ? evenScheduledPhase : oddScheduledPhase;
  }

  /**
   * Do we need a mutator reset rendezvous in this phase?
   */
  private boolean needsMutatorResetRendezvous(boolean isEvenPhase) {
    return isEvenPhase ? evenMutatorResetRendezvous : oddMutatorResetRendezvous;
  }
  /**
   * Set the next phase. If we are in an even phase the next phase is odd.
   */
  private void setNextPhase(boolean isEvenPhase, int scheduledPhase, boolean needsResetRendezvous) {
    if (isEvenPhase) {
      oddScheduledPhase = scheduledPhase;
      evenMutatorResetRendezvous = needsResetRendezvous;
    } else {
      evenScheduledPhase = scheduledPhase;
      oddMutatorResetRendezvous = needsResetRendezvous;
    }
  }

  /**
   * Pull the next scheduled phase off the stack. This may involve
   * processing several complex phases and skipping placeholders, etc.
   *
   * @return The next phase to run, or -1 if no phases are left.
   */
  private int getNextPhase() {
    while (phaseStackPointer >= 0) {
      int scheduledPhase = peekScheduledPhase();
      short schedule = getSchedule(scheduledPhase);
      short phaseId = getPhaseId(scheduledPhase);

      switch(schedule) {
        case Phase.SCHEDULE_PLACEHOLDER: {
          /* Placeholders are ignored and we continue looking */
          popScheduledPhase();
          continue;
        }

        case Phase.SCHEDULE_GLOBAL:
        case Phase.SCHEDULE_COLLECTOR:
        case Phase.SCHEDULE_MUTATOR: {
          /* Simple phases are just popped off the stack and executed */
          popScheduledPhase();
          return scheduledPhase;
        }

        case Phase.SCHEDULE_CONCURRENT: {
        	VM.assertions.fail("ERM");
          return -1;
        }

        case Phase.SCHEDULE_COMPLEX: {
          /* A complex phase may either be a newly pushed complex phase,
           * or a complex phase we are in the process of executing in
           * which case we move to the next subphase. */
          ComplexPhase p = (ComplexPhase)getPhase(phaseId);
          int cursor = incrementComplexPhaseCursor();
          if (cursor == 0 && p.timer != null) {
            /* Tell the primary thread to start the timer after the next sync. */
            startComplexTimer = phaseId;
          }
          if (cursor < p.count()) {
            /* There are more entries, we push the next one and continue */
            pushScheduledPhase(p.get(cursor));
            continue;
          }

          /* We have finished this complex phase */
          popScheduledPhase();
          if (p.timer != null) {
            /* Tell the primary thread to stop the timer after the next sync. */
            stopComplexTimer = phaseId;
          }
          continue;
        }

        default: {
          VM.assertions.fail("Invalid phase type encountered");
        }
      }
    }
    return -1;
  }

  /**
   * Pause all of the timers for the complex phases sitting in the stack.
   */
  private void pauseComplexTimers() {
    for(int i=phaseStackPointer; i >=0; i--) {
      Phase p = getPhase(getPhaseId(phaseStack[i]));
      if (p.timer != null) p.timer.stop();
    }
  }

  /**
   * Resume all of the timers for the complex phases sitting in the stack.
   */
  private void resumeComplexTimers() {
    for(int i=phaseStackPointer; i >=0; i--) {
      Phase p = getPhase(getPhaseId(phaseStack[i]));
      if (p.timer != null) p.timer.start();
    }
  }

  /**
   * Return true if phase stack is empty, false otherwise.
   *
   * @return true if phase stack is empty, false otherwise.
   */
  @Inline
  public boolean isPhaseStackEmpty() {
    return phaseStackPointer < 0;
  }

  /**
   * Clears the scheduled phase stack.
   */
  @Inline
  public void resetPhaseStack() {
    phaseStackPointer = -1;
  }

  /**
   * Push a scheduled phase onto the top of the work stack.
   *
   * @param scheduledPhase The scheduled phase.
   */
  @Inline
  public void pushScheduledPhase(int scheduledPhase) {
    phaseStack[++phaseStackPointer] = scheduledPhase;
    complexPhaseCursor[phaseStackPointer] = 0;
  }

  /**
   * Increment the cursor associated with the current phase
   * stack entry. This is used to remember the current sub phase
   * when executing a complex phase.
   *
   * @return The old value of the cursor.
   */
  @Inline
  private int incrementComplexPhaseCursor() {
    return complexPhaseCursor[phaseStackPointer]++;
  }

  /**
   * Pop off the scheduled phase at the top of the work stack.
   */
  @Inline
  private int popScheduledPhase() {
    return phaseStack[phaseStackPointer--];
  }

  /**
   * Peek the scheduled phase at the top of the work stack.
   */
  @Inline
  private int peekScheduledPhase() {
    return phaseStack[phaseStackPointer];
  }
}
