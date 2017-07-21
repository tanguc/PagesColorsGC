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
package org.mmtk.plan;

import org.mmtk.plan.marksweep.MS;
import org.mmtk.policy.Space;
import org.mmtk.utility.Conversions;
import org.mmtk.utility.Log;
import org.mmtk.utility.options.Options;
import org.mmtk.utility.sanitychecker.SanityCheckerLocal;
import org.mmtk.utility.statistics.Stats;
import org.mmtk.vm.VM;
import org.vmmagic.pragma.*;

/**
 * This class (and its sub-classes) implement <i>per-collector thread</i>
 * behavior and state.<p>
 *
 * MMTk assumes that the VM instantiates instances of CollectorContext
 * in thread local storage (TLS) for each thread participating in
 * collection.  Accesses to this state are therefore assumed to be
 * low-cost during mutator time.
 *
 * @see CollectorContext
 */
@Uninterruptible
public abstract class SimpleCollector extends ParallelCollector {

  /****************************************************************************
   * Instance fields
   */

  /** Used for sanity checking. */
  protected final SanityCheckerLocal sanityLocal = new SanityCheckerLocal();

  /****************************************************************************
   *
   * Collection
   */
  protected int kbBeforeTIGC = 0;
  int kbBeforeGC = 0;

  /**
   * {@inheritDoc}
   */
  @Override
  @Inline
  public void collectionPhase(short phaseId, boolean primary) {
    if (phaseId == Simple.TI_INITIATE) {
      tigcCount++;
      kbBeforeTIGC = Conversions.pagesToKBytes(MS.msSpaces[this.mutatorSlot].reservedPages()); 
      tigcStart = VM.statistics.nanoTime();
      return;
    }
    
    if (phaseId == Simple.TI_PREPARE) {
      return;
    }
    
    if (phaseId == Simple.INITIATE) {
      kbBeforeGC = Conversions.pagesToKBytes(Space.getPagesReserved());
      gcStart = VM.statistics.nanoTime();
      return;
    }
    
    if (phaseId == Simple.PREPARE) {
      // Nothing to do
      return;
    }

    if (phaseId == Simple.STACK_ROOTS) {
      VM.scanning.computeThreadRoots(getCurrentTrace());
      return;
    }

    if (phaseId == Simple.ROOTS) {
      VM.scanning.computeGlobalRoots(getCurrentTrace());
      VM.scanning.computeStaticRoots(getCurrentTrace());
      if (Plan.SCAN_BOOT_IMAGE) {
        VM.scanning.computeBootImageRoots(getCurrentTrace());
      }
      return;
    }

    if (phaseId == Simple.SOFT_REFS) {
      if (primary) {
        if (Options.noReferenceTypes.getValue())
          VM.softReferences.clear();
        else
          VM.softReferences.scan(getCurrentTrace(),global().isCurrentGCNursery());
      }
      return;
    }

    if (phaseId == Simple.WEAK_REFS) {
      if (primary) {
        if (Options.noReferenceTypes.getValue())
          VM.weakReferences.clear();
        else
          VM.weakReferences.scan(getCurrentTrace(),global().isCurrentGCNursery());
      }
      return;
    }

    if (phaseId == Simple.FINALIZABLE) {
      if (primary) {
        if (Options.noFinalizer.getValue())
          VM.finalizableProcessor.clear();
        else
          VM.finalizableProcessor.scan(getCurrentTrace(),global().isCurrentGCNursery());
      }
      return;
    }

    if (phaseId == Simple.PHANTOM_REFS) {
      if (primary) {
        if (Options.noReferenceTypes.getValue())
          VM.phantomReferences.clear();
        else
          VM.phantomReferences.scan(getCurrentTrace(),global().isCurrentGCNursery());
      }
      return;
    }

    if (phaseId == Simple.FORWARD_REFS) {
      if (primary && !Options.noReferenceTypes.getValue() &&
          VM.activePlan.constraints().needsForwardAfterLiveness()) {
        VM.softReferences.forward(getCurrentTrace(),global().isCurrentGCNursery());
        VM.weakReferences.forward(getCurrentTrace(),global().isCurrentGCNursery());
        VM.phantomReferences.forward(getCurrentTrace(),global().isCurrentGCNursery());
      }
      return;
    }

    if (phaseId == Simple.FORWARD_FINALIZABLE) {
      if (primary && !Options.noFinalizer.getValue() &&
          VM.activePlan.constraints().needsForwardAfterLiveness()) {
        VM.finalizableProcessor.forward(getCurrentTrace(),global().isCurrentGCNursery());
      }
      return;
    }

    if (phaseId == Simple.COMPLETE) {
      if (!Plan.PERFORMANCE_RUN) {
        int kbAfterGC = Conversions.pagesToKBytes(Space.getPagesReserved());
        VM.statistics.logGC(Stats.gcCount(), (Plan.startTime > 0 ? gcStart - Plan.startTime : 0), kbBeforeGC - kbAfterGC, VM.statistics.nanoTime() - gcStart);
      }
      return;
    }

    if (phaseId == Simple.RELEASE) {
      // Nothing to do
      return;
    }

    if (Options.sanityCheck.getValue() && sanityLocal.collectionPhase(phaseId, primary)) {
      return;
    }

    Log.write("Per-collector phase "); Log.write(Phase.getName(phaseId));
    Log.writeln(" not handled.");
    VM.assertions.fail("Per-collector phase not handled!");
  }

  /****************************************************************************
   *
   * Miscellaneous.
   */

  /** @return The active global plan as a <code>Simple</code> instance. */
  @Inline
  private static Simple global() {
    return (Simple) VM.activePlan.global();
  }
}
