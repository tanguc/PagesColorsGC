/*
 *  This file is part of the Jikes RVM project (http://jikesrvm.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License. You
 *  may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  See the COPYRIGHT.txt file distributed with this work for information
 *  regarding copyright ownership.
 */
package org.mmtk.plan.marksweep;

import org.mmtk.plan.*;
import org.mmtk.policy.MarkSweepSpace;
import org.mmtk.policy.Space;
import org.mmtk.utility.heap.VMRequest;

import org.vmmagic.pragma.*;
import org.vmmagic.unboxed.*;

/**
 * This class implements the global state of a simple mark-sweep collector.<p>
 *
 * All plans make a clear distinction between <i>global</i> and
 * <i>thread-local</i> activities, and divides global and local state
 * into separate class hierarchies.  Global activities must be
 * synchronized, whereas no synchronization is required for
 * thread-local activities.  There is a single instance of Plan (or the
 * appropriate sub-class), and a 1:1 mapping of PlanLocal to "kernel
 * threads" (aka CPUs).  Thus instance
 * methods of PlanLocal allow fast, unsychronized access to functions such as
 * allocation and collection.<p>
 *
 * The global instance defines and manages static resources
 * (such as memory and virtual memory resources).  This mapping of threads to
 * instances is crucial to understanding the correctness and
 * performance properties of MMTk plans.
 */
@Uninterruptible
public class MS extends StopTheWorld {

  /****************************************************************************
   * Class variables
   */

  /**
   *
   */
	public static final MarkSweepSpace msSpace = null; //new MarkSweepSpace("ms", VMRequest.create());
	  public static final MarkSweepSpace msSpace1 = new MarkSweepSpace("ms1", VMRequest.discontiguous());
	  public static final int TL_SPACES = 1024;
	  public static MarkSweepSpace[] msSpaces = new MarkSweepSpace[TL_SPACES];
	  public static final int[] MARK_SWEEPS = new int[TL_SPACES];
	  
	  public static final int MARK_SWEEP = 0;

	  public static final int SCAN_MARK = 0;
	  
	  static {
	    msSpaces[1] = msSpace1;
	    MARK_SWEEPS[1] = msSpace1.getDescriptor();
	    msSpaces[1].isThreadLocalSpace = false;
	    msSpaces[1].threadLocalIndex = 1;
	    
	    for (int i = 2; i < TL_SPACES; i++) {
	      msSpaces[i] = new MarkSweepSpace("ms" + i, VMRequest.discontiguous());
	      MARK_SWEEPS[i] = msSpaces[i].getDescriptor();
	      msSpaces[i].isThreadLocalSpace = true;
	      msSpaces[i].threadLocalIndex = i;
	    }
	  }
	  
	  @NoInstrument
	  public static void registerNumberVMThreads(int mainThreadID) {
	    for (int i = 2; i < mainThreadID; i++) {
	      msSpaces[i].isThreadLocalSpace = false;
	      msSpaces[i].threadLocalIndex = -1;
	    }
	  }

  /****************************************************************************
   * Instance variables
   */

  /**
   *
   */
  public final Trace msTrace = new Trace(metaDataSpace);


  /*****************************************************************************
   * Collection
   */

  /**
   * {@inheritDoc}
   */
  @Inline
  @Override
  public void collectionPhase(short phaseId) {

    if (phaseId == PREPARE) {
      super.collectionPhase(phaseId);
      msTrace.prepare();
      for (int i = 1; i < TL_SPACES; i++) {
        if (msSpaces[i] == null) {
          continue;
        }
        msSpaces[i].prepare(true);
      }
      return;
    }

    if (phaseId == CLOSURE) {
      msTrace.prepare();
      return;
    }
    if (phaseId == RELEASE) {
      msTrace.release();
      for (int i = 1; i < TL_SPACES; i++) {
        if (msSpaces[i] == null) {
          continue;
        }
        msSpaces[i].kbFreedLastTIGC = Integer.MAX_VALUE;
        msSpaces[i].release();
      }
      super.collectionPhase(phaseId);
      return;
    }

    super.collectionPhase(phaseId);
  }

  /*****************************************************************************
   * Accounting
   */

  /**
   * {@inheritDoc}
   * The superclass accounts for its spaces, we just
   * augment this with the mark-sweep space's contribution.
   */
  @Override
  @NoInstrument
  public int getPagesUsed() {
    int reserved = 0;
    for (int i = 0; i < TL_SPACES; i++) {
      if (msSpaces[i] == null) {
        continue;
      }
      reserved = reserved + msSpaces[i].reservedPages();
    }
    return reserved + super.getPagesUsed();
  }

  /*****************************************************************************
   * Miscellaneous
   */

  /**
   * {@inheritDoc}
   */
  @Override
  @NoInstrument
  public boolean willNeverMove(ObjectReference object) {
    for (int i = 0; i < TL_SPACES; i++) {
      if (msSpaces[i] == null) {
        continue;
      }
      if (Space.isInSpace(MARK_SWEEPS[i], object)) {
        return true;
      }
    }
    return super.willNeverMove(object);
  }

  @Interruptible
  @Override
  protected void registerSpecializedMethods() {
    TransitiveClosure.registerSpecializedScan(SCAN_MARK, MSTraceLocal.class);
    super.registerSpecializedMethods();
  }
}
