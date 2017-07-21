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
package org.mmtk.utility.deque;

import static org.mmtk.utility.Constants.*;

import org.mmtk.policy.RawPageSpace;
import org.mmtk.policy.Space;
import org.mmtk.utility.Log;
import org.mmtk.vm.Lock;
import org.mmtk.vm.VM;
import org.vmmagic.pragma.Entrypoint;
import org.vmmagic.pragma.Inline;
import org.vmmagic.pragma.NoInstrument;
import org.vmmagic.pragma.Uninterruptible;
import org.vmmagic.unboxed.Address;
import org.vmmagic.unboxed.Offset;

/**
 * This supports <i>unsynchronized</i> enqueuing and dequeuing of buffers
 * for shared use.  The data can be added to and removed from either end
 * of the deque.
 */
@Uninterruptible
public class SharedDeque extends Deque {
  private static final boolean DISABLE_WAITING = true;
  private static final Offset NEXT_OFFSET = Offset.zero();
  private static final Offset PREV_OFFSET = Offset.fromIntSignExtend(BYTES_IN_ADDRESS);

  private static final boolean TRACE = false;
  private static final boolean TRACE_DETAIL = false;
  private static final boolean TRACE_BLOCKERS = false;

  /****************************************************************************
   *
   * Public instance methods
   */

  /**
   * @param name the queue's human-readable name
   * @param rps the space to get pages from
   * @param arity the arity (number of words per entry) of this queue
   */
  public SharedDeque(String name, RawPageSpace rps, int arity) {
    this.rps = rps;
    this.arity = arity;
    this.name = name;
    lock = VM.newLock("SharedDeque");
    clearCompletionFlag();
    head = HEAD_INITIAL_VALUE;
    tail = TAIL_INITIAL_VALUE;
  }

  /** @return the arity (words per entry) of this queue */
  @Inline
  final int getArity() { return arity; }
  @Inline
  @NoInstrument
  final int getArityNI() { return arity; }

  /**
   * Enqueue a block on the head or tail of the shared queue
   *
   * @param buf the block to enqueue
   * @param arity the arity of this queue
   * @param toTail whether to enqueue to the tail of the shared queue
   */
  final void enqueue(Address buf, int arity, boolean toTail) {
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(arity == this.arity);
    lock();
    if (toTail) {
      // Add to the tail of the queue
      setNext(buf, Address.zero());
      if (tail.EQ(TAIL_INITIAL_VALUE))
        head = buf;
      else
        setNext(tail, buf);
      setPrev(buf, tail);
      tail = buf;
    } else {
      // Add to the head of the queue
      setPrev(buf, Address.zero());
      if (head.EQ(HEAD_INITIAL_VALUE))
        tail = buf;
      else
        setPrev(head, buf);
      setNext(buf, head);
      head = buf;
    }
    bufsenqueued++;
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(checkDequeLength(bufsenqueued));
    unlock();
  }
  
  @NoInstrument
  final void enqueueNI(Address buf, int arity, boolean toTail) {
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(arity == this.arity);
    lockNI();
    //lockNI();
    if (toTail) {
      // Add to the tail of the queue
      setNextNI(buf, Address.zero());
      if (tail.EQ(TAIL_INITIAL_VALUE))
        head = buf;
      else
        setNextNI(tail, buf);
      setPrevNI(buf, tail);
      tail = buf;
    } else {
      // Add to the head of the queue
      setPrevNI(buf, Address.zero());
      if (head.EQ(HEAD_INITIAL_VALUE))
        tail = buf;
      else
        setPrevNI(head, buf);
      setNextNI(buf, head);
      head = buf;
    }
    bufsenqueued++;
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(checkDequeLengthNI(bufsenqueued));
    //unlockNI();
    unlockNI();
  }

  public final void clearDeque(int arity) {
    Address buf = dequeue(arity);
    while (!buf.isZero()) {
      free(bufferStart(buf));
      buf = dequeue(arity);
    }
    setCompletionFlag();
  }

  @Inline
  final Address dequeue(int arity) {
    return dequeue(arity, false);
  }
  
  @Inline
  @NoInstrument
  final Address dequeueNI(int arity) {
    return dequeueNI(arity, false);
  }

  final Address dequeue(int arity, boolean fromTail) {
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(arity == this.arity);
    return dequeue(false, fromTail);
  }
  
  @NoInstrument
  final Address dequeueNI(int arity, boolean fromTail) {
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(arity == this.arity);
    return dequeueNI(false, fromTail);
  }

  @Inline
  final Address dequeueAndWait(int arity) {
    return dequeueAndWait(arity, false);
  }
  
  @Inline
  @NoInstrument
  final Address dequeueAndWaitNI(int arity) {
    return dequeueAndWaitNI(arity, false);
  }

  final Address dequeueAndWait(int arity, boolean fromTail) {
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(arity == this.arity);
    Address buf = dequeue(false, fromTail);
    if (buf.isZero() && (!complete())) {
      buf = dequeue(true, fromTail);  // Wait inside dequeue
    }
    return buf;
  }
  
  @NoInstrument
  final Address dequeueAndWaitNI(int arity, boolean fromTail) {
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(arity == this.arity);
    Address buf = dequeueNI(false, fromTail);
    if (buf.isZero() && (!(completionFlag == 1))) {
      buf = dequeueNI(true, fromTail);  // Wait inside dequeue
    }
    return buf;
  }

  /**
   * Prepare for parallel processing. All active GC threads will
   * participate, and pop operations will block until all work
   * is complete.
   */
  public final void prepare() {
    if (DISABLE_WAITING) {
      prepareNonBlocking();
    } else {
      /* This should be the normal mode of operation once performance is fixed */
      prepare(VM.activePlan.collector().parallelWorkerCount());
    }
  }

  /**
   * Prepare for processing where pop operations on the deques
   * will never block.
   */
  public final void prepareNonBlocking() {
    prepare(1);
  }

  /**
   * Prepare for parallel processing where a specific number
   * of threads take part.
   *
   * @param consumers # threads taking part.
   */
  private void prepare(int consumers) {
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(numConsumersWaiting == 0);
    setNumConsumers(consumers);
    clearCompletionFlag();
  }

  public final void reset() {
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(numConsumersWaiting == 0);
    clearCompletionFlag();
    setNumConsumersWaiting(0);
    //assertExhausted();
  }

  public final void assertExhausted() {
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(head.isZero() && tail.isZero());
  }

  @Inline
  final Address alloc() {
    Address rtn = rps.acquire(PAGES_PER_BUFFER);
    if (rtn.isZero()) {
      Space.printUsageMB();
      VM.assertions.fail("Failed to allocate space for queue.  Is metadata virtual memory exhausted?");
    }
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(rtn.EQ(bufferStart(rtn)));
    return rtn;
  }
  @NoInstrument
  @Inline
  final Address allocNI() {
    Address rtn = rps.acquire(PAGES_PER_BUFFER);
    if (rtn.isZero()) {
      Space.printUsageMB();
      VM.assertions.fail("Failed to allocate space for queue.  Is metadata virtual memory exhausted?");
    }
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(rtn.EQ(bufferStartNI(rtn)));
    return rtn;
  }

  @Inline
  final void free(Address buf) {
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(buf.EQ(bufferStart(buf)) && !buf.isZero());
    rps.release(buf);
  }

  @Inline
  public final int enqueuedPages() {
    return bufsenqueued * PAGES_PER_BUFFER;
  }

  /****************************************************************************
   *
   * Private instance methods and fields
   */

  /** The name of this shared deque - for diagnostics */
  private final String name;

  /** Raw page space from which to allocate */
  private RawPageSpace rps;

  /** Number of words per entry */
  private final int arity;

  /** Completion flag - set when all consumers have arrived at the barrier */
  @Entrypoint
  private volatile int completionFlag;

  /** # active threads - processing is complete when # waiting == this */
  @Entrypoint
  private volatile int numConsumers;

  /** # threads waiting */
  @Entrypoint
  private volatile int numConsumersWaiting;

  /** Head of the shared deque */
  @Entrypoint
  protected volatile Address head;

  /** Tail of the shared deque */
  @Entrypoint
  protected volatile Address tail;
  @Entrypoint
  private volatile int bufsenqueued;
  private Lock lock;

  private static final long WARN_PERIOD = (long)(2*1E9);
  private static final long TIMEOUT_PERIOD = 10 * WARN_PERIOD;

  /**
   * Dequeue a block from the shared pool.  If 'waiting' is true, and the
   * queue is empty, wait for either a new block to show up or all the
   * other consumers to join us.
   *
   * @param waiting whether to wait to dequeue a block if none is present
   * @param fromTail whether to dequeue from the tail
   * @return the Address of the block
   */
  private Address dequeue(boolean waiting, boolean fromTail) {
    lock();
    Address rtn = ((fromTail) ? tail : head);
    if (rtn.isZero()) {
      if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(tail.isZero() && head.isZero());
      // no buffers available
      if (waiting) {
        int ordinal = TRACE ? 0 : VM.activePlan.collector().getId();
        setNumConsumersWaiting(numConsumersWaiting + 1);
        while (rtn.isZero()) {
          if (numConsumersWaiting == numConsumers)
            setCompletionFlag();
          if (TRACE) {
            Log.write("-- ("); Log.write(ordinal);
            Log.write(") joining wait queue of SharedDeque(");
            Log.write(name); Log.write(") ");
            Log.write(numConsumersWaiting); Log.write("/");
            Log.write(numConsumers);
            Log.write(" consumers waiting");
            if (complete()) Log.write(" WAIT COMPLETE");
            Log.writeln();
            if (TRACE_BLOCKERS)
              VM.assertions.dumpStack();
          }
          unlock();
          // Spin and wait
          spinWait(fromTail);

          if (complete()) {
            if (TRACE) {
              Log.write("-- ("); Log.write(ordinal); Log.writeln(") EXITING");
            }
            lock();
            setNumConsumersWaiting(numConsumersWaiting - 1);
            unlock();
            return Address.zero();
          }
          lock();
          // Re-get the list head/tail while holding the lock
          rtn = ((fromTail) ? tail : head);
        }
        setNumConsumersWaiting(numConsumersWaiting - 1);
        if (TRACE) {
          Log.write("-- ("); Log.write(ordinal); Log.write(") resuming work ");
          Log.write(" n="); Log.writeln(numConsumersWaiting);
        }
      } else {
        unlock();
        return Address.zero();
      }
    }
    if (fromTail) {
      // dequeue the tail buffer
      setTail(getPrev(tail));
      if (head.EQ(rtn)) {
        setHead(Address.zero());
        if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(tail.isZero());
      } else {
        setNext(tail, Address.zero());
      }
    } else {
      // dequeue the head buffer
      setHead(getNext(head));
      if (tail.EQ(rtn)) {
        setTail(Address.zero());
        if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(head.isZero());
      } else {
        setPrev(head, Address.zero());
      }
    }
    bufsenqueued--;
    unlock();
    return rtn;
  }
  
  @NoInstrument
  private Address dequeueNI(boolean waiting, boolean fromTail) {
    lockNI();
    Address rtn = ((fromTail) ? tail : head);
    if (rtn.isZero()) {
      if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(tail.isZero() && head.isZero());
      // no buffers available
      if (waiting) {
        int ordinal = TRACE ? 0 : VM.activePlan.collector().getId();
        setNumConsumersWaitingNI(numConsumersWaiting + 1);
        while (rtn.isZero()) {
          if (numConsumersWaiting == numConsumers)
          	 completionFlag = 1;
          if (TRACE) {
            Log.write("-- ("); Log.write(ordinal);
            Log.write(") joining wait queue of SharedDeque(");
            Log.write(name); Log.write(") ");
            Log.write(numConsumersWaiting); Log.write("/");
            Log.write(numConsumers);
            Log.write(" consumers waiting");
            if (completionFlag == 1) Log.write(" WAIT COMPLETE");
            Log.writeln();
            if (TRACE_BLOCKERS)
              VM.assertions.dumpStack();
          }
          unlockNI();
          // Spin and wait
          spinWaitNI(fromTail);

          if (completionFlag == 1) {
            if (TRACE) {
              Log.write("-- ("); Log.write(ordinal); Log.writeln(") EXITING");
            }
            lockNI();
            setNumConsumersWaitingNI(numConsumersWaiting - 1);
            unlockNI();
            return Address.zero();
          }
          lockNI();
          // Re-get the list head/tail while holding the lock
          rtn = ((fromTail) ? tail : head);
        }
        setNumConsumersWaitingNI(numConsumersWaiting - 1);
        if (TRACE) {
          Log.write("-- ("); Log.write(ordinal); Log.write(") resuming work ");
          Log.write(" n="); Log.writeln(numConsumersWaiting);
        }
      } else {
        unlockNI();
        return Address.zero();
      }
    }
    if (fromTail) {
      // dequeue the tail buffer
      setTailNI(getPrevNI(tail));
      if (head.EQ(rtn)) {
        setHeadNI(Address.zero());
        if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(tail.isZero());
      } else {
        setNextNI(tail, Address.zero());
      }
    } else {
      // dequeue the head buffer
      setHeadNI(getNextNI(head));
      if (tail.EQ(rtn)) {
        setTailNI(Address.zero());
        if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(head.isZero());
      } else {
        setPrevNI(head, Address.zero());
      }
    }
    bufsenqueued--;
    unlockNI();
    return rtn;
  }

  /**
   * Spinwait for GC work to arrive
   *
   * @param fromTail Check the head or the tail ?
   */
  private void spinWait(boolean fromTail) {
    long startNano = 0;
    long lastElapsedNano = 0;
    while (true) {
      long startCycles = VM.statistics.cycles();
      long endCycles = startCycles + ((long) 1e9); // a few hundred milliseconds more or less.
      long nowCycles;
      do {
        VM.memory.isync();
        Address rtn = ((fromTail) ? tail : head);
        if (!rtn.isZero() || complete()) return;
        nowCycles = VM.statistics.cycles();
      } while (startCycles < nowCycles && nowCycles < endCycles); /* check against both ends to guard against CPU migration */

      /*
       * According to the cycle counter, we've been spinning for a while.
       * Time to check nanoTime and see if we should print a warning and/or fail.
       * We lock the deque while doing this to avoid interleaved messages from multiple threads.
       */
      lock();
      if (startNano == 0) {
        startNano = VM.statistics.nanoTime();
      } else {
        long nowNano = VM.statistics.nanoTime();
        long elapsedNano = nowNano - startNano;
        if (elapsedNano - lastElapsedNano > WARN_PERIOD) {
          Log.write("GC Warning: SharedDeque("); Log.write(name);
          Log.write(") wait has reached "); Log.write(VM.statistics.nanosToSecs(elapsedNano));
          Log.write(", "); Log.write(numConsumersWaiting); Log.write("/");
          Log.write(numConsumers); Log.writeln(" threads waiting");
          lastElapsedNano = elapsedNano;
        }
        if (elapsedNano > TIMEOUT_PERIOD) {
          unlock();   // To allow other GC threads to die in turn
          VM.assertions.fail("GC Error: SharedDeque Timeout");
        }
      }
      unlock();
    }
  }
  
  @NoInstrument
  private void spinWaitNI(boolean fromTail) {
    long startNano = 0;
    long lastElapsedNano = 0;
    while (true) {
      long startCycles = VM.statistics.cycles();
      long endCycles = startCycles + ((long) 1e9); // a few hundred milliseconds more or less.
      long nowCycles;
      do {
        VM.memory.isync();
        Address rtn = ((fromTail) ? tail : head);
        if (!rtn.isZero() || completionFlag == 1) return;
        nowCycles = VM.statistics.cycles();
      } while (startCycles < nowCycles && nowCycles < endCycles); /* check against both ends to guard against CPU migration */

      /*
       * According to the cycle counter, we've been spinning for a while.
       * Time to check nanoTime and see if we should print a warning and/or fail.
       * We lock the deque while doing this to avoid interleaved messages from multiple threads.
       */
      lockNI();
      if (startNano == 0) {
        startNano = VM.statistics.nanoTime();
      } else {
        long nowNano = VM.statistics.nanoTime();
        long elapsedNano = nowNano - startNano;
        if (elapsedNano - lastElapsedNano > WARN_PERIOD) {
          Log.write("GC Warning: SharedDeque("); Log.write(name);
          Log.write(") wait has reached "); Log.write(VM.statistics.nanosToSecs(elapsedNano));
          Log.write(", "); Log.write(numConsumersWaiting); Log.write("/");
          Log.write(numConsumers); Log.writeln(" threads waiting");
          lastElapsedNano = elapsedNano;
        }
        if (elapsedNano > TIMEOUT_PERIOD) {
          unlockNI();   // To allow other GC threads to die in turn
          VM.assertions.fail("GC Error: SharedDeque Timeout");
        }
      }
      unlockNI();
    }
  }

  /**
   * Set the "next" pointer in a buffer forming the linked buffer chain.
   *
   * @param buf The buffer whose next field is to be set.
   * @param next The reference to which next should point.
   */
  private static void setNext(Address buf, Address next) {
    buf.store(next, NEXT_OFFSET);
  }
  @NoInstrument
  private static void setNextNI(Address buf, Address next) {
    buf.store(next, NEXT_OFFSET);
  }

  /**
   * Get the "next" pointer in a buffer forming the linked buffer chain.
   *
   * @param buf The buffer whose next field is to be returned.
   * @return The next field for this buffer.
   */
  protected final Address getNext(Address buf) {
    return buf.loadAddress(NEXT_OFFSET);
  }
  @NoInstrument
  protected final Address getNextNI(Address buf) {
    return buf.loadAddress(NEXT_OFFSET);
  }

  /**
   * Set the "prev" pointer in a buffer forming the linked buffer chain.
   *
   * @param buf The buffer whose next field is to be set.
   * @param prev The reference to which prev should point.
   */
  private void setPrev(Address buf, Address prev) {
    buf.store(prev, PREV_OFFSET);
  }
  @NoInstrument
  private void setPrevNI(Address buf, Address prev) {
    buf.store(prev, PREV_OFFSET);
  }

  /**
   * Get the "next" pointer in a buffer forming the linked buffer chain.
   *
   * @param buf The buffer whose next field is to be returned.
   * @return The next field for this buffer.
   */
  protected final Address getPrev(Address buf) {
    return buf.loadAddress(PREV_OFFSET);
  }
  @NoInstrument
  protected final Address getPrevNI(Address buf) {
    return buf.loadAddress(PREV_OFFSET);
  }

  /**
   * Check the number of buffers in the work queue (for debugging
   * purposes).
   *
   * @param length The number of buffers believed to be in the queue.
   * @return True if the length of the queue matches length.
   */
  private boolean checkDequeLength(int length) {
    Address top = head;
    int l = 0;
    while (!top.isZero() && l <= length) {
      top = getNext(top);
      l++;
    }
    return l == length;
  }
  @NoInstrument
  private boolean checkDequeLengthNI(int length) {
    Address top = head;
    int l = 0;
    //VM.assertions.logStr("bufsencoded ");
    //VM.assertions.logIntln(length);
    while (!top.isZero() && l <= length) {
      //VM.assertions.logStr("top ");
      //VM.assertions.logHexln(top);
      //VM.assertions.logStr("l ");
      //VM.assertions.logIntln(l);
      top = getNextNI(top);
      l++;
    }
    //VM.assertions.logStr("bufsencoded ");
    //VM.assertions.logIntln(length);
    //VM.assertions.logStr("l ");
    //VM.assertions.logIntln(l);
    return l == length;
  }

  /**
   * Lock this shared queue.  We use one simple low-level lock to
   * synchronize access to the shared queue of buffers.
   */
  private void lock() {
    lock.acquire();
  }
  
  @NoInstrument
  private void lockNI() {
    lock.acquireNI();
  }

  /**
   * Release the lock.  We use one simple low-level lock to synchronize
   * access to the shared queue of buffers.
   */
  private void unlock() {
    lock.release();
  }
  @NoInstrument
  private void unlockNI() {
    lock.releaseNI();
  }

  /**
   * @return whether the current round of processing is complete
   */
  private boolean complete() {
    return completionFlag == 1;
  }

  /**
   * Set the completion flag.
   */
  @Inline
  private void setCompletionFlag() {
    if (TRACE_DETAIL) {
      Log.writeln("# setCompletionFlag: ");
    }
    completionFlag = 1;
  }

  /**
   * Clear the completion flag.
   */
  @Inline
  private void clearCompletionFlag() {
    if (TRACE_DETAIL) {
      Log.writeln("# clearCompletionFlag: ");
    }
    completionFlag = 0;
  }

  @Inline
  private void setNumConsumers(int newNumConsumers) {
    if (TRACE_DETAIL) {
      Log.write("# Num consumers "); Log.writeln(newNumConsumers);
    }
    numConsumers = newNumConsumers;
  }

  @Inline
  private void setNumConsumersWaiting(int newNCW) {
    if (TRACE_DETAIL) {
      Log.write("# Num consumers waiting "); Log.writeln(newNCW);
    }
    numConsumersWaiting = newNCW;
  }
  
  @Inline
  @NoInstrument
  private void setNumConsumersWaitingNI(int newNCW) {
    if (TRACE_DETAIL) {
      Log.write("# Num consumers waiting "); Log.writeln(newNCW);
    }
    numConsumersWaiting = newNCW;
  }

  @Inline
  private void setHead(Address newHead) {
    head = newHead;
    VM.memory.sync();
  }

  @Inline
  private void setTail(Address newTail) {
    tail = newTail;
    VM.memory.sync();
  }
  
  @Inline
  @NoInstrument
  private void setHeadNI(Address newHead) {
    head = newHead;
    VM.memory.sync();
  }

  @Inline
  @NoInstrument
  private void setTailNI(Address newTail) {
    tail = newTail;
    VM.memory.sync();
  }
}
