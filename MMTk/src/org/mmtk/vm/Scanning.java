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
package org.mmtk.vm;

import org.mmtk.plan.TraceLocal;
import org.mmtk.plan.TransitiveClosure;

import org.vmmagic.pragma.Uninterruptible;
import org.vmmagic.unboxed.*;

@Uninterruptible public abstract class Scanning {
  /**
   * Delegated scanning of a object, processing each pointer field
   * encountered.
   *
   * @param trace the trace to use for scanning
   * @param object The object to be scanned.
   */
  public abstract void scanObject(TransitiveClosure trace, ObjectReference object);

  /**
   * Invoke a specialized scan method. Note that these methods must have been allocated
   * explicitly through Plan and PlanConstraints.
   *
   * @param id The specialized method id
   * @param trace The trace the method has been specialized for
   * @param object The object to be scanned
   */
  public abstract void specializedScanObject(int id, TransitiveClosure trace, ObjectReference object);

  /**
   * Prepares for using the <code>computeAllRoots</code> method.  The
   * thread counter allows multiple GC threads to co-operatively
   * iterate through the thread data structure (if load balancing
   * parallel GC threads were not important, the thread counter could
   * simply be replaced by a for loop).
   */
  public abstract void resetThreadCounter();

  /**
   * Called the first time during a collection that thread's stacks
   * have been scanned. This can be used (for example) to clean up
   * obsolete compiled methods that are no longer being executed.
   *
   * @param partialScan whether the scan was partial or full-heap
   */
  public abstract void notifyInitialThreadScanComplete(boolean partialScan);

  /**
   * Computes static roots.  This method establishes all such roots for
   * collection and places them in the root locations queue.  This method
   * should not have side effects (such as copying or forwarding of
   * objects).  There are a number of important preconditions:
   *
   * <ul>
   * <li> All objects used in the course of GC (such as the GC thread
   * objects) need to be "pre-copied" prior to calling this method.
   * <li> The <code>threadCounter</code> must be reset so that load
   * balancing parallel GC can share the work of scanning threads.
   * </ul>
   *
   * @param trace The trace to use for computing roots.
   */
  public abstract void computeStaticRoots(TraceLocal trace);

  /**
   * Computes global roots.  This method establishes all such roots for
   * collection and places them in the root locations queue.  This method
   * should not have side effects (such as copying or forwarding of
   * objects).  There are a number of important preconditions:
   *
   * <ul>
   * <li> All objects used in the course of GC (such as the GC thread
   * objects) need to be "pre-copied" prior to calling this method.
   * <li> The <code>threadCounter</code> must be reset so that load
   * balancing parallel GC can share the work of scanning threads.
   * </ul>
   *
   * @param trace The trace to use for computing roots.
   */
  public abstract void computeGlobalRoots(TraceLocal trace);

  /**
   * Computes roots pointed to by threads, their associated registers
   * and stacks.<p>
   *
   * This method places these roots in the root values,
   * root locations and interior root locations queues.  This method
   * should not have side effects (such as copying or forwarding of
   * objects).  There are a number of important preconditions:
   *
   * <ul>
   * <li> All objects used in the course of GC (such as the GC thread
   * objects) need to be "pre-copied" prior to calling this method.
   * <li> The <code>threadCounter</code> must be reset so that load
   * balancing parallel GC can share the work of scanning threads.
   * </ul>
   *
   * @param trace The trace to use for computing roots.
   */
  public abstract void computeThreadRoots(TraceLocal trace);
  public abstract void computeThreadRoots(TraceLocal trace, int mutatorSlot);

  /**
   * Computes new roots pointed to by threads, their associated registers
   * and stacks.   This method is only required to return roots that are
   * new since the last stack scan (if possible, the implementation will
   * optimize the scanning to only scan new portions of the stacks).<p>
   *
   * This method places these roots in the root values,
   * root locations and interior root locations queues.  This method
   * should not have side effects (such as copying or forwarding of
   * objects).  There are a number of important preconditions:
   *
   * <ul>
   * <li> All objects used in the course of GC (such as the GC thread
   * objects) need to be "pre-copied" prior to calling this method.
   * <li> The <code>threadCounter</code> must be reset so that load
   * balancing parallel GC can share the work of scanning threads.
   * </ul>
   *
   * @param trace The trace to use for computing roots.
   */
  public abstract void computeNewThreadRoots(TraceLocal trace);

  /**
   * Compute all roots out of the VM's boot image (if any).  This method is a no-op
   * in the case where the VM does not maintain an MMTk-visible Java space.   However,
   * when the VM does maintain a space (such as a boot image) which is visible to MMTk,
   * that space could either be scanned by MMTk as part of its transitive closure over
   * the whole heap, or as a (considerable) performance optimization, MMTk could avoid
   * scanning the space if it is aware of all pointers out of that space.  This method
   * is used to establish the root set out of the scannable space in the case where
   * such a space exists.
   *
   * @param trace The trace object to use to report root locations.
   */
  public abstract void computeBootImageRoots(TraceLocal trace);

  /**
   * @return true if the runtime supports a return barrier
   */
  public abstract boolean supportsReturnBarrier();
}
