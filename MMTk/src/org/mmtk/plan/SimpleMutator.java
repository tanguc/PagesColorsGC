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

import org.mmtk.utility.Constants;
import org.mmtk.utility.Log;
import org.mmtk.vm.VM;
import org.vmmagic.pragma.*;
import org.vmmagic.unboxed.Address;
import org.vmmagic.unboxed.ObjectReference;
import org.vmmagic.unboxed.Offset;
import org.vmmagic.unboxed.Word;

/**
 * This class (and its sub-classes) implement <i>per-mutator thread</i>
 * behavior and state.<p>
 *
 * MMTk assumes that the VM instantiates instances of MutatorContext
 * in thread local storage (TLS) for each application thread. Accesses
 * to this state are therefore assumed to be low-cost during mutator
 * time.
 *
 * @see MutatorContext
 */
@Uninterruptible
public abstract class SimpleMutator extends MutatorContext {

  /****************************************************************************
   *
   * Collection.
   */

  /**
   * Perform a per-mutator collection phase.   This is executed by
   * one collector thread on behalf of a mutator thread.
   */
  @Override
  @Inline
  public void collectionPhase(short phaseId, boolean primary) {
    if (phaseId == Simple.TI_INITIATE) {
      return;
    }
    
    if (phaseId == Simple.TI_PREPARE_STACKS) {
      VM.collection.prepareMutator(this);
      return;
    }
    
    if (phaseId == Simple.TI_COMPLETE) {
      return;
    }
    
    if (phaseId == Simple.PREPARE_STACKS) {
      if (!Plan.stacksPrepared()) {
        VM.collection.prepareMutator(this);
      }
      flushRememberedSets();
      return;
    }

    if (phaseId == Simple.PREPARE) {
      if (Plan.REMSET_ON) {
        remset.flushLocal();
        remsetPool.prepareNonBlocking();
        while (!remsetAccess.isEmpty()) {
          remsetAccess.pop();
        }
        remsetPool.clearDeque(1);
        remsetPool.reset();
      }
      
      los.prepare(true);
      lgcode.prepare(true);
      smcode.prepare();
      nonmove.prepare();
      VM.memory.collectorPrepareVMSpace();
      return;
    }

    if (phaseId == Simple.RELEASE) {
      los.release(true);
      lgcode.release(true);
      smcode.release();
      nonmove.release();
      VM.memory.collectorReleaseVMSpace();
      return;
    }

    Log.write("Per-mutator phase \""); Phase.getPhase(phaseId).logPhase();
    Log.writeln("\" not handled.");
    VM.assertions.fail("Per-mutator phase not handled!");
  }
  
  @Override
  @NoInstrument
  public void objectReferenceWrite(ObjectReference src, Address slot, ObjectReference value, Word metaDataA, Word metaDataB, int mode) {
    fastPathReferenceWrite(src, value);
    VM.barriers.objectReferenceWrite(src, value, metaDataA, metaDataB, mode);
  }
  
  @Override
  @NoInstrument
  public ObjectReference objectReferenceRead(ObjectReference src, Address slot, Word metaDataA, Word metaDataB, int mode) {
    fastPath(src);
    return VM.barriers.objectReferenceRead(src, metaDataA, metaDataB, mode);
  }
  
  @NoInstrument
  public boolean objectReferenceBulkCopy(ObjectReference src, Offset srcOffset, ObjectReference dst, Offset dstOffset, int bytes) {
    if (!(dst.isNull() || !VM.activePlan.fullyBooted() || !VM.activePlan.isMutator()) && VM.objectModel.getColour(dst) == Plan.GREY) { VM.objectModel.requestGlobalise(dst); }
    if (!(src.isNull() || !VM.activePlan.fullyBooted() || !VM.activePlan.isMutator()) && VM.objectModel.getColour(src) == Plan.GREY) { VM.objectModel.requestGlobalise(src); }
    Address end = VM.objectModel.objectToAddress(src).plus(srcOffset).plus(bytes);
    Address cursor = VM.objectModel.objectToAddress(src).plus(srcOffset);
    while (cursor.LT(end)) {
      fastPathReferenceWrite(dst, cursor.loadObjectReference());
      cursor = cursor.plus(Constants.BYTES_IN_ADDRESS);
    }
    return false;
  }
  
  @Override
  @NoInstrument
  public void objectReferenceNonHeapWrite(Address slot, ObjectReference tgt, Word metaDataA, Word metaDataB) {
    fastPathStaticWrite(tgt);
    VM.barriers.objectReferenceNonHeapWrite(slot, tgt, metaDataA, metaDataB);
  }
  
  @Override
  @NoInstrument
  public ObjectReference objectReferenceNonHeapRead(Address slot, Word metaDataA, Word metaDataB) {
    return VM.barriers.objectReferenceNonHeapRead(slot, metaDataA, metaDataB);
  }
  
  @Override
  public boolean objectReferenceTryCompareAndSwap(ObjectReference src, Address slot, ObjectReference old, ObjectReference tgt, Word metaDataA, Word metaDataB, int mode) {
    fastPathReferenceWrite(src, tgt);
    return VM.barriers.objectReferenceTryCompareAndSwap(src, old, tgt, metaDataA, metaDataB, mode);
  }
}
