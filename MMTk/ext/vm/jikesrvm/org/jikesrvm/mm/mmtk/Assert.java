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

import org.mmtk.plan.Plan;
import org.mmtk.policy.Space;
import org.jikesrvm.VM;
import org.jikesrvm.scheduler.GlobaliserThread;
import org.jikesrvm.scheduler.RVMThread;
import org.vmmagic.pragma.*;
import org.vmmagic.unboxed.Address;

import static org.jikesrvm.runtime.SysCall.sysCall;

@Uninterruptible public class Assert extends org.mmtk.vm.Assert {
  @Override
  protected final boolean getVerifyAssertionsConstant() { return VM.VerifyAssertions;}

  /**
   * This method should be called whenever an error is encountered.
   *
   * @param str A string describing the error condition.
   */
  public final void error(String str) {
    Space.printUsagePages();
    Space.printUsageMB();
    fail(str);
  }

  @Override
  public final void fail(String message) {
    Space.printUsagePages();
    Space.printUsageMB();
    VM.sysFail(message);
  }

  @Uninterruptible
  public final void exit(int rc) {
    VM.sysExit(rc);
  }

  @Override
  @NoInstrument
  @Inline(value=Inline.When.AllArgumentsAreConstant)
  public final void _assert(boolean cond) {
    if (!org.mmtk.vm.VM.VERIFY_ASSERTIONS)
      VM.sysFail("All assertions must be guarded by VM.VERIFY_ASSERTIONS: please check the failing assertion");
    //CHECKSTYLE:OFF - Checkstyle assertion plugin would warn otherwise
    VM._assert(cond);
    //CHECKSTYLE:ON
  }

  @Override
  @NoInstrument
  @Inline(value=Inline.When.ArgumentsAreConstant, arguments={1})
  public final void _assert(boolean cond, String message) {
    if (!org.mmtk.vm.VM.VERIFY_ASSERTIONS)
      VM.sysFail("All assertions must be guarded by VM.VERIFY_ASSERTIONS: please check the failing assertion");
    if (!cond) VM.sysWriteln(message);
    //CHECKSTYLE:OFF - Checkstyle assertion plugin would warn otherwise
    VM._assert(cond);
    //CHECKSTYLE:ON
  }

  @Override
  public final void dumpStack() {
    RVMThread.dumpStack();
  }
  
  @Override
  @NoInstrument
  public final void failNI(String message) {
    RVMThread.dumpStackNI();
    VM.sysFailNI();
  }
  
  @Override
  @NoInline
  public final void dumpStackNI() {
    RVMThread.dumpStackNI();
  }
  
  @NoInstrument
  @NoInline
  public void logInt(int v) {
    sysCall.logInt(v);
  }
  
  @NoInstrument
  @NoInline
  public void logIntln(int v) {
    sysCall.logIntln(v);
  }
    
  @NoInstrument
  @NoInline
  public void logLong(long v) {
    sysCall.logLong(v);
  }
  
  @NoInstrument
  @NoInline
  public void logLongln(long v) {
    sysCall.logLongln(v);
  }
  
  @NoInstrument
  @NoInline
  public void logStr(String v) {
    //sysCall.logStr(java.lang.JikesRVMSupport.getBackingCharArray(v));
    char[] buf = java.lang.JikesRVMSupport.getBackingCharArrayNI(v);
    int len = java.lang.JikesRVMSupport.getStringLengthNI(v);
    for (int i = 0; i < len; i++) {
      sysCall.logChar(buf[i]);
    }
  }
  
  @NoInstrument
  @NoInline
  public void logStrln(String v) {
    //sysCall.logStrln(java.lang.JikesRVMSupport.getBackingCharArray(v));
    char[] buf = java.lang.JikesRVMSupport.getBackingCharArrayNI(v);
    int len = java.lang.JikesRVMSupport.getStringLengthNI(v);
    if (len == 0) {
      sysCall.logln();
    }
    else if (len == 1) {
      sysCall.logCharln(buf[0]);
    }
    else {
      for (int i = 0; i < (len-1); i++) {
        sysCall.logChar(buf[i]);
      }
      sysCall.logCharln(buf[(len-1)]);
    }
  }
  
  @NoInstrument
  @NoInline
  public void logHex(Address v) {
    sysCall.logHex(v.toInt());
  }
  
  @NoInstrument
  @NoInline
  public void logHexln(Address v) {
    sysCall.logHexln(v.toInt());
  }
  
  @NoInstrument
  public void assertGlobaliseControl() {
	if (!Plan.GLOBALISE_ON) return;
    if (GlobaliserThread.getThreadStatus(RVMThread.getCurrentThreadMonotonicNI()) != GlobaliserThread.ALLOCATING_THREAD_CONTROL && !RVMThread.getCurrentThread().terminating) {
      RVMThread.logInt(RVMThread.getCurrentThreadMonotonicNI());
      RVMThread.logStr(" ");
      RVMThread.logIntln(GlobaliserThread.getThreadStatus(RVMThread.getCurrentThreadMonotonicNI()));
      if (VM.VerifyAssertions) VM._assert(false);
    }
  }
  
  @NoInstrument
  public void assertGlobaliseActiveControl(int threadID) {
	if (!Plan.GLOBALISE_ON) return;
    if (VM.VerifyAssertions) VM._assert(GlobaliserThread.getThreadStatus(threadID) == GlobaliserThread.ALLOCATING_THREAD_CONTROL_AND_ACTIVE || GlobaliserThread.getThreadStatus(threadID) == GlobaliserThread.GLOBALISER_CONTROL_AND_ACTIVE);
  }
}
