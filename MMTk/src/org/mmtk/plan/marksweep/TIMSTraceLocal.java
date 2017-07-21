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
import org.mmtk.utility.deque.AddressDeque;
import org.mmtk.utility.deque.ObjectReferenceDeque;
import org.mmtk.vm.VM;
import org.vmmagic.pragma.*;
import org.vmmagic.unboxed.*;

/**
 * This class implements the thread-local functionality for a transitive
 * closure over a mark-sweep space.
 */
@Uninterruptible
public final class TIMSTraceLocal extends TraceLocal {
  /****************************************************************************
   * Instance fields
   */
  private final ObjectReferenceDeque modBuffer;
  private AddressDeque remsetAccess;
  private int mutatorSlot;
  
  public int blackRemsetEntries = 0;
  public int greyRemsetEntries = 0;
  
  /* In TransitiveClosure
  public int blackObjects = 0;
  public int greyObjects = 0;
  public int whiteObjects = 0;
   */

  /**
   * Constructor
   */
  public TIMSTraceLocal(Trace trace, ObjectReferenceDeque modBuffer) {
    super(trace);
    this.modBuffer = modBuffer;
  }
  
  public void prepare(AddressDeque remset, int mutatorSlot) {
  	this.remsetAccess = remset;
  	this.mutatorSlot = mutatorSlot;
  	
  	//blackObjects = 0;
    //greyObjects = 0;
    whiteObjects = 0;
  }


  /****************************************************************************
   * Externally visible Object processing and tracing
   */

  /**
   * Is the specified object live?
   *
   * @param object The object.
   * @return <code>true</code> if the object is live.
   */
  @Override
  @NoInstrument
  public boolean isLive(ObjectReference object) {
    if (object.isNull()) return false;
    if (Space.isInSpace(MS.MARK_SWEEPS[mutatorSlot], object)) {
    	return MS.msSpaces[mutatorSlot].isLive(object);
    }
    return true;
  }

  /**
   * This method is the core method during the trace of the object graph.
   * The role of this method is to:
   *
   * 1. Ensure the traced object is not collected.
   * 2. If this is the first visit to the object enqueue it to be scanned.
   * 3. Return the forwarded reference to the object.
   *
   * In this instance, we refer objects in the mark-sweep space to the
   * msSpace for tracing, and defer to the superclass for all others.
   *
   * @param object The object to be traced.
   * @return The new reference to the same object instance.
   */
  @Inline
  @Override
  @NoInstrument
  public ObjectReference traceObject(ObjectReference object) {
    if (object.isNull()) return object;
    if (Space.isInSpace(MS.MARK_SWEEPS[mutatorSlot], object)) {
    	return MS.msSpaces[mutatorSlot].traceObject(this, object);
    }
    return object;
  }

  /**
   * Process any remembered set entries.  This means enumerating the
   * mod buffer and for each entry, marking the object as unlogged.
   */
  @NoInstrument
  protected void processRememberedSets() {
    if (modBuffer != null) {
      logMessage(5, "clearing modBuffer");
      while (!modBuffer.isEmpty()) {
        ObjectReference src = modBuffer.pop();
        HeaderByte.markAsUnlogged(src);
      }
    }
    
    greyRemsetEntries = 0;
    blackRemsetEntries = 0;
    
    if (remsetAccess != null) {
	    remsetAccess.insertNI(Address.fromIntZeroExtend(5));
	    while (!remsetAccess.isEmpty()) {
	    	Address loc = remsetAccess.pop();
	      if (loc.EQ(Address.fromIntZeroExtend(5))) {
	      	break;
	      }
	      if (VM.DEBUG) VM.debugging.remsetEntry(loc);
	      ObjectReference src = loc.toObjectReference();
	      if (VM.objectModel.getColour(src) == Plan.BLACK) blackRemsetEntries++;
	      else if (VM.objectModel.getColour(src) == Plan.GREY) greyRemsetEntries++;
	      if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(VM.objectModel.getColour(src) >= Plan.GREY);
	      ObjectReference newObject = traceObject(src, false);
	      processNode(newObject);
	      remsetAccess.insertNI(newObject.toAddress());
	    }
    }
  }
  
  @Inline
  public void completeTrace() {
    logMessage(4, "Processing GC in parallel");
    if (!rootLocations.isEmpty()) {
      processRoots();
    }
    processRememberedSets();
    logMessage(5, "processing gray objects");
    do {
      while (!values.isEmpty()) {
        ObjectReference v = values.pop();
        scanObject(v);
      }
      //No extra processRemset here
    } while (!values.isEmpty());
  }
}
