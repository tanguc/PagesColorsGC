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
package org.jikesrvm.mm.mmtk;

import org.mmtk.vm.VM;
import org.vmmagic.pragma.NoInstrument;
import org.vmmagic.pragma.Uninterruptible;

/**
 * Provides MMTk access to a heavy lock with condition variable.
 * Functionally similar to Java monitors, but safe in the darker corners of runtime code.
 */
@Uninterruptible
public final class Monitor extends org.mmtk.vm.Monitor {

  private final org.jikesrvm.scheduler.Monitor theLock;

  public Monitor(String name) {
    this.theLock = new org.jikesrvm.scheduler.Monitor();
  }

  @Override
  public void lock() {
    theLock.lockNoHandshake();
  }
  
  @NoInstrument
  public void lockNI() {
	if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(theLock != null);
    theLock.lockNoHandshakeNI();
  }

  @Override
  public void unlock() {
    theLock.unlock();
  }
  
  @NoInstrument
  public void unlockNI() {
	if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(theLock != null);
    theLock.unlockNI();
  }

  @Override
  public void await() {
    theLock.waitNoHandshake();
  }
  
  @NoInstrument
  public void awaitNI() {
	if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(theLock != null);
    theLock.waitNoHandshakeNI();
  }

  @Override
  @NoInstrument
  public void broadcast() {
    theLock.broadcast();
  }
  
  @NoInstrument
  public void broadcastNI() {
	if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(theLock != null);
    theLock.broadcastNI();
  }
}
