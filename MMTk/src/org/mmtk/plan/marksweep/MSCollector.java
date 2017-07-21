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
import org.mmtk.utility.Conversions;
import org.mmtk.utility.Log;
import org.mmtk.utility.deque.AddressDeque;
import org.mmtk.utility.deque.SynchronisedWriteBuffer;
import org.mmtk.utility.options.Options;
import org.mmtk.vm.VM;
import org.vmmagic.pragma.*;

/**
 * This class implements <i>per-collector thread</i> behavior
 * and state for the <i>MS</i> plan, which implements a full-heap
 * mark-sweep collector.<p>
 *
 * Specifically, this class defines <i>MS</i> collection behavior
 * (through <code>trace</code> and the <code>collectionPhase</code>
 * method).<p>
 *
 * @see MS for an overview of the mark-sweep algorithm.
 *
 * @see MS
 * @see MSMutator
 * @see StopTheWorldCollector
 * @see CollectorContext
 */
@Uninterruptible
public class MSCollector extends StopTheWorldCollector {

  /****************************************************************************
   * Instance fields
   */

  /**
   *
   */
  private SynchronisedWriteBuffer[] collectorRemsetBuffers = new SynchronisedWriteBuffer[1024];

  protected MSTraceLocal fullTrace = new MSTraceLocal(global().msTrace, null, collectorRemsetBuffers);
  protected TraceLocal currentTrace = fullTrace;
  
  public final Trace tiTrace = new Trace(MS.metaDataSpace);
  protected TIMSTraceLocal tiTraceLocal = new TIMSTraceLocal(tiTrace, null);
  /****************************************************************************
   * Collection
   */

  /**
   * {@inheritDoc}
   */
  @Inline
  @Override
  public void collectionPhase(short phaseId, boolean primary) {
    if (phaseId == MS.TI_PREPARE) {
      super.collectionPhase(phaseId, primary);
      MS.msSpaces[this.mutatorSlot].prepare(true);
      AddressDeque remsetAccess = VM.activePlan.getThreadById(this.mutatorSlot).remsetAccess;
      tiTrace.prepare();
      tiTraceLocal.prepare(remsetAccess, this.mutatorSlot);
      return;
    }
    
    if (phaseId == MS.TI_STACK_ROOTS) {
      VM.scanning.computeThreadRoots(tiTraceLocal, this.mutatorSlot);
      return;
    }
    
    if (phaseId == MS.TI_CLOSURE) {
      tiTraceLocal.completeTrace();
      return;
    }
       
    if (phaseId == MS.TI_RELEASE) {
      tiTraceLocal.release();
      tiTrace.release();
      MS.msSpaces[this.mutatorSlot].release();
      return;
    }
    
    if (phaseId == Simple.TI_COMPLETE) {
      int kbAfterTIGC = Conversions.pagesToKBytes(MS.msSpaces[this.mutatorSlot].reservedPages());
      if (!Plan.PERFORMANCE_RUN) {
        Log.write("[TIGC "); Log.write(this.mutatorSlot); Log.write("."); Log.write(tigcCount);
        Log.write(" ");
        Log.write(VM.statistics.nanoTime() - Plan.startTime);
        Log.write(" ns ");
        Log.write(kbBeforeTIGC);
        Log.write(" -> ");
        Log.write(kbAfterTIGC);
        Log.write("KB (");
        Log.write(kbBeforeTIGC - kbAfterTIGC);
        Log.write("KB) Time ");
        Log.write(VM.statistics.nanosToMillis(VM.statistics.nanoTime() - tigcStart));
        Log.write(" ms, ");
        Log.write(tiTraceLocal.blackRemsetEntries);
        Log.write(":");
        Log.write(tiTraceLocal.greyRemsetEntries);
        Log.write(", ");
        //Log.write(tiTraceLocal.blackObjects);
        //Log.write(":");
        //Log.write(tiTraceLocal.greyObjects);
        //Log.write(":");
        Log.write(tiTraceLocal.whiteObjects);
        Log.write(", ");
        Log.write(MS.msSpaces[mutatorSlot].reclaimedCellsSize);
        //Log.write(":");
        //Log.write(MS.msSpaces[mutatorSlot].reclaimedBlocksSize);
        Log.writeln("]");
      }
      else if (Options.verbose.getValue() >= 2) {
        Log.write("[TIGC "); Log.write(this.mutatorSlot); Log.write("."); Log.write(tigcCount);
        Log.write(" ");
        Log.write(kbBeforeTIGC - kbAfterTIGC);
        Log.write("KB Time ");
        Log.write(VM.statistics.nanosToMillis(VM.statistics.nanoTime() - tigcStart));
        Log.writeln(" ms]");
      }
      if (VM.VERIFY_ASSERTIONS && !Plan.PERFORMANCE_RUN) {
        VM.assertions._assert(MS.msSpaces[mutatorSlot].reclaimedBlocksSize == ((kbBeforeTIGC - kbAfterTIGC) * 1024));
      }
      VM.statistics.logTIGC(this.mutatorSlot, tigcCount, tigcStart - Plan.startTime, kbBeforeTIGC - kbAfterTIGC, VM.statistics.nanoTime() - tigcStart, tiTraceLocal.whiteObjects, tiTraceLocal.greyRemsetEntries, tiTraceLocal.blackRemsetEntries, MS.msSpaces[mutatorSlot].reclaimedCellsSize);
      MS.msSpaces[this.mutatorSlot].kbFreedLastTIGC = kbBeforeTIGC - kbAfterTIGC;
      return;
    }
    
    if (phaseId == MS.PREPARE) {
      super.collectionPhase(phaseId, primary);
      for (int i = 0; i < 1024; i++) {
        MutatorContext mutator = VM.activePlan.getThreadById(i);
        if (mutator == null) {
          collectorRemsetBuffers[i] = null;
        }
        else {
          collectorRemsetBuffers[i] = new SynchronisedWriteBuffer(mutator.remsetPool);
        }
      }
      fullTrace.prepare();
      return;
    }

    if (phaseId == MS.CLOSURE) {
      fullTrace.completeTrace();
      return;
    }

    if (phaseId == MS.RELEASE) {
      fullTrace.release();
      super.collectionPhase(phaseId, primary);
      for (int i = 0; i < 1024; i++) {
        if (collectorRemsetBuffers[i] != null) {
          collectorRemsetBuffers[i].flushLocal();
        }
      }
      return;
    }

    super.collectionPhase(phaseId, primary);
  }


  /****************************************************************************
   * Miscellaneous
   */

  /** @return The active global plan as an <code>MS</code> instance. */
  @Inline
  private static MS global() {
    return (MS) VM.activePlan.global();
  }

  @Override
  public final TraceLocal getCurrentTrace() {
    return currentTrace;
  }
   
  @Override
  @NoInstrument
  public void collect() {
    if (!this.isThreadIndependent) {
      super.collect();
      return;
    }
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(Phase.tiPhases != null && Phase.tiPhases[this.collectorSlot] != null);
    Phase.tiPhases[this.collectorSlot].beginNewPhaseStack(Phase.scheduleComplex(global().threadIndependentCollection));
  }
}
