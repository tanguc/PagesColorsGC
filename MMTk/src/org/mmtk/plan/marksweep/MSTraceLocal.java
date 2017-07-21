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

import org.mmtk.plan.Plan;
import org.mmtk.plan.TraceLocal;
import org.mmtk.plan.Trace;
import org.mmtk.policy.Space;
import org.mmtk.utility.HeaderByte;
import org.mmtk.utility.deque.ObjectReferenceDeque;
import org.mmtk.utility.deque.SynchronisedWriteBuffer;
import org.mmtk.utility.statistics.Stats;
import org.mmtk.vm.VM;
import org.vmmagic.pragma.*;
import org.vmmagic.unboxed.*;

/**
 * This class implements the thread-local functionality for a transitive
 * closure over a mark-sweep space.
 */
@Uninterruptible
public final class MSTraceLocal extends TraceLocal {
  /****************************************************************************
   * Instance fields
   */

  /**
   *
   */
  private final ObjectReferenceDeque modBuffer;

  public MSTraceLocal(Trace trace, ObjectReferenceDeque modBuffer) {
    super(MS.SCAN_MARK, trace);
    this.modBuffer = modBuffer;
  }
  
  public MSTraceLocal(Trace trace, ObjectReferenceDeque modBuffer, SynchronisedWriteBuffer[] collectorRemsetBuffers) {
    this(trace, modBuffer);
    this.collectorRemsetBuffers = collectorRemsetBuffers;
    if (VM.VERIFY_ASSERTIONS) {
      VM.assertions._assert(collectorRemsetBuffers != null);
    }
  }
  
  @Inline
  @Override
  public final void processRootEdge(Address slot, boolean untraced) {
    ObjectReference object;
    boolean addToRemset = false;
    if (untraced) object = slot.loadObjectReference();
    else     object = VM.activePlan.global().loadObjectReference(slot);
    if (object != null && Plan.REMSET_ON) {
      Space tgtSpace = Space.getSpaceForObjectNI(object);
      if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(tgtSpace != null);
      if (tgtSpace.isThreadLocalSpace) {
        if (VM.objectModel.getColour(object) >= Plan.GREY) { //This means only stack roots that point to grey/black objects are included.
          addToRemset = true;
        }
      }
    }
    ObjectReference newObject = traceObjectR(object, addToRemset);
    if (overwriteReferenceDuringTrace()) {
      if (untraced) slot.store(newObject);
      else     VM.activePlan.global().storeObjectReference(slot, newObject);
    }
  }
  
  @Override
  @Inline
  public final void processEdge(ObjectReference source, Address slot) {
    boolean addToRemset = false;
    ObjectReference object = VM.activePlan.global().loadObjectReference(slot);
    if (object != null) {
      if (VM.objectModel.getColour(source) == Plan.BLACK || VM.objectModel.getColour(object) >= Plan.GREY) {
        if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(!Plan.GLOBALISE_ON || VM.objectModel.getColour(object) >= Plan.GREY || (VM.objectModel.getAllocatingThread(object) == 1 && VM.objectModel.getAllocID(object) == 0));
        addToRemset = true;
      }
      else {
        Space tgtSpace = Space.getSpaceForObjectNI(object);
        if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(tgtSpace != null);
        
        if (tgtSpace.isThreadLocalSpace) {
          Space srcSpace = Space.getSpaceForObjectNI(source);
          if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(srcSpace != null);
          
          if (srcSpace.threadLocalIndex != tgtSpace.threadLocalIndex) {
            if (!(!Plan.GLOBALISE_ON || VM.objectModel.getColour(object) >= Plan.GREY || (VM.objectModel.getAllocatingThread(object) == 1 && VM.objectModel.getAllocID(object) == 0))) {
               VM.objectModel.dumpAllHeaders(source);
               VM.objectModel.dumpAllHeaders(object);
            }
            if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(!Plan.GLOBALISE_ON || VM.objectModel.getColour(object) >= Plan.GREY || (VM.objectModel.getAllocatingThread(object) == 1 && VM.objectModel.getAllocID(object) == 0));
             addToRemset = true;
          }
        }
      }
    }
    ObjectReference newObject = traceObjectR(object, addToRemset);
    if (overwriteReferenceDuringTrace()) {
      VM.activePlan.global().storeObjectReference(slot, newObject);
    }
  }


  /****************************************************************************
   * Externally visible Object processing and tracing
   */
  

  /**
   * {@inheritDoc}
   */
  @Override
  @NoInstrument
  public boolean isLive(ObjectReference object) {
    if (object.isNull()) return false;
    if (VM.activePlan.fullyBooted()) {
      if (Space.isInSpace(MS.MARK_SWEEPS[VM.objectModel.getOriginalAllocatingThread(object)], object)) {
        return MS.msSpaces[VM.objectModel.getOriginalAllocatingThread(object)].isLive(object);
      }
    }
    else {
      if (Space.isInSpace(MS.MARK_SWEEPS[1], object)) {
        return MS.msSpaces[1].isLive(object);
      }
    }
    return super.isLive(object);
  }
  
  @Inline
  @NoInstrument
  public ObjectReference traceObjectR(ObjectReference object, boolean addToRemset) {
    if (object.isNull()) return object;
    if (Stats.gcCount() == 1) {
      if (VM.objectModel.getColour(object) <= Plan.WHITE) {
        VM.objectModel.setBootThreadAndColour(object, 1, Plan.GREY);
        //MutatorContext owner = VM.activePlan.getThreadById(VM.objectModel.getOriginalAllocatingThread(object));
        //if (owner != null) {
          //owner.addIntoRemset(object);
        //}
        if (Plan.REMSET_ON) {
          int ownerId = VM.objectModel.getOriginalAllocatingThread(object);
          if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(collectorRemsetBuffers != null);
          if (collectorRemsetBuffers[ownerId] != null) {
            collectorRemsetBuffers[ownerId].insertNI(object.toAddress());
          }
          else {
            VM.assertions.logStrln("[Warning]: tracing object with no thread context");
          }
        }
      }
    }
    for (int i = 1; i < MS.TL_SPACES; i++) {
      if (MS.msSpaces[i] == null) {
        continue;
      }
      else if (Space.isInSpace(MS.MARK_SWEEPS[i], object)) {
        return MS.msSpaces[i].traceObject(this, object, (Stats.gcCount() == 1 ? false : addToRemset));
      }
    }
    return super.traceObject(object);
  }

  /**
   * Process any remembered set entries.  This means enumerating the
   * mod buffer and for each entry, marking the object as unlogged
   * (we don't enqueue for scanning since we're doing a full heap GC).
   */
  @Override
  protected void processRememberedSets() {
    if (modBuffer != null) {
      logMessage(5, "clearing modBuffer");
      while (!modBuffer.isEmpty()) {
        ObjectReference src = modBuffer.pop();
        HeaderByte.markAsUnlogged(src);
      }
    }
  }
}
