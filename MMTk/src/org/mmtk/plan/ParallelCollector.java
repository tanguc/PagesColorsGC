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

import org.mmtk.utility.Log;
import org.mmtk.utility.options.Options;
import org.mmtk.vm.Monitor;
import org.mmtk.vm.VM;
import org.vmmagic.pragma.*;
import org.vmmagic.unboxed.ObjectReference;

/**
 * TODO: Documentation.
 */
@Uninterruptible
public abstract class ParallelCollector extends CollectorContext {

  /****************************************************************************
   * Instance fields
   */

  /** The group that this collector context is running in (may be null) */
  protected ParallelCollectorGroup group;

  /** Last group trigger index (see CollectorContextGroup) */
  protected int lastTriggerCount;

  /** The index of this thread in the collector context group. */
  int workerOrdinal;

  /****************************************************************************
   * Collection.
   */
  private boolean requestFlag = false;
  public Monitor lock;
  public int mutatorSlot = -1;
  public boolean isThreadIndependent = false;
  public volatile int triggerCount;
  protected volatile int contextsParked;
  public int collectorSlot = -1;
  
  /**
   * Request a collection.
   */
  @NoInstrument
  public void requestTIGC() {
	if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(lock != null);
    lock.lockNI();
    requestFlag = true;
    lock.broadcastNI();
    lock.unlockNI();
  }
  
  @NoInstrument
  public void setMutator(int mutatorSlot) {
    this.mutatorSlot = mutatorSlot;
  }
    
  public void clearMutator() {
    this.mutatorSlot = -1;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Unpreemptible
  @NoInstrument
  public void run() {
    if (isThreadIndependent) {
      while(true) {
    	if (Options.verbose.getValue() >= 5) { Log.write("[TIGC: Waiting for request "); Log.write(this.mutatorSlot); Log.writeln("...]"); }
        if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(lock != null);
        lock.lockNI();
        while (!requestFlag) {
          lock.awaitNI();
        }
        lock.unlockNI();
      
        int m = this.mutatorSlot;
      
        if (Options.verbose.getValue() >= 5) { Log.write("[TIGC: Request received "); Log.write(m); Log.writeln("]"); }
        VM.collection.waitUntilMutatorBlockedForTIGC(m);
        
        if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(lock != null);
        //lock.lock();
        requestFlag = false;
        //lock.unlock();
             
        if (Options.verbose.getValue() >= 5) { Log.write("[TIGC: Triggering worker thread "); Log.write(m); Log.writeln("]"); }
             
        if (VM.activePlan.getBinding(VM.activePlan.getThreadID()) == -1) {
          VM.activePlan.setBinding(VM.activePlan.getBinding(this.mutatorSlot));
        }
        
        if (Options.verbose.getValue() >= 5) { Log.write("[TIGC: Collecting: "); Log.write(this.isThreadIndependent); Log.writeln("...]"); }
        collect();

        if (Options.verbose.getValue() >= 5) { Log.write("[TIGC: Complete. Resuming mutator "); Log.write(m); Log.writeln("...]"); }
        VM.collection.resumeMutatorFromTIGC(m);
      }
    }
    else {
      while(true) {
        park();
        collect();
      }
    }
  }

  /** Perform a single garbage collection */
  public void collect() {
    VM.assertions.fail("Collector has not implemented collectionPhase");
  }

  /**
   * Perform a (local, i.e.per-collector) collection phase.
   *
   * @param phaseId The unique phase identifier
   * @param primary Should this thread be used to execute any single-threaded
   * local operations?
   */
  public void collectionPhase(short phaseId, boolean primary) {
    VM.assertions.fail("Collector has not implemented collectionPhase");
  }

  /**
   * @return The current trace instance.
   */
  public TraceLocal getCurrentTrace() {
    VM.assertions.fail("Collector has not implemented getCurrentTrace");
    return null;
  }

  /**
   * Park this thread into the group, waiting for a request.
   */
  public void park() {
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(this.group != null);
    group.park(this);
  }

  @Override
  public int parallelWorkerCount() {
    if (group == null) return 1;
    return group.activeWorkerCount();
  }

  @Override
  public int parallelWorkerOrdinal() {
    return workerOrdinal;
  }

  @Override
  public int rendezvous() {
    if (group == null) return 0;
    return group.rendezvous();
  }
  
  public void triggerCycle() {}
  public void waitForCycle() {}
}
