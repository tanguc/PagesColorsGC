package org.jikesrvm.runtime;

import javax.annotation.Generated;

@org.vmmagic.pragma.Uninterruptible
@Generated(
value = "org.jikesrvm.tools.annotation_processing.SysCallProcessor",
comments = "Auto-generated from org.jikesrvm.runtime.SysCall")
public final class SysCallImpl extends org.jikesrvm.runtime.SysCall {

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysConsoleWriteChar(char v) {
    sysConsoleWriteChar(BootRecord.the_boot_record.sysConsoleWriteCharIP, v);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysConsoleWriteChar(org.vmmagic.unboxed.Address nativeIP, char v);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysConsoleWriteInteger(int value, int hexToo) {
    sysConsoleWriteInteger(BootRecord.the_boot_record.sysConsoleWriteIntegerIP, value, hexToo);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysConsoleWriteInteger(org.vmmagic.unboxed.Address nativeIP, int value, int hexToo);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysConsoleWriteLong(long value, int hexToo) {
    sysConsoleWriteLong(BootRecord.the_boot_record.sysConsoleWriteLongIP, value, hexToo);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysConsoleWriteLong(org.vmmagic.unboxed.Address nativeIP, long value, int hexToo);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysConsoleWriteDouble(double value, int postDecimalDigits) {
    sysConsoleWriteDouble(BootRecord.the_boot_record.sysConsoleWriteDoubleIP, value, postDecimalDigits);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysConsoleWriteDouble(org.vmmagic.unboxed.Address nativeIP, double value, int postDecimalDigits);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysExit(int value) {
    sysExit(BootRecord.the_boot_record.sysExitIP, value);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysExit(org.vmmagic.unboxed.Address nativeIP, int value);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public int sysArg(int argno, byte[] buf, int buflen) {
    return sysArg(BootRecord.the_boot_record.sysArgIP, argno, buf, buflen);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native int sysArg(org.vmmagic.unboxed.Address nativeIP, int argno, byte[] buf, int buflen);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public int sysGetenv(byte[] varName, byte[] buf, int limit) {
    return sysGetenv(BootRecord.the_boot_record.sysGetenvIP, varName, buf, limit);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native int sysGetenv(org.vmmagic.unboxed.Address nativeIP, byte[] varName, byte[] buf, int limit);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysCopy(org.vmmagic.unboxed.Address dst, org.vmmagic.unboxed.Address src, org.vmmagic.unboxed.Extent cnt) {
    sysCopy(BootRecord.the_boot_record.sysCopyIP, dst, src, cnt);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysCopy(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address dst, org.vmmagic.unboxed.Address src, org.vmmagic.unboxed.Extent cnt);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysMemmove(org.vmmagic.unboxed.Address dst, org.vmmagic.unboxed.Address src, org.vmmagic.unboxed.Extent cnt) {
    sysMemmove(BootRecord.the_boot_record.sysMemmoveIP, dst, src, cnt);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysMemmove(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address dst, org.vmmagic.unboxed.Address src, org.vmmagic.unboxed.Extent cnt);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public org.vmmagic.unboxed.Address sysMalloc(int length) {
    return sysMalloc(BootRecord.the_boot_record.sysMallocIP, length);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native org.vmmagic.unboxed.Address sysMalloc(org.vmmagic.unboxed.Address nativeIP, int length);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public org.vmmagic.unboxed.Address sysCalloc(int length) {
    return sysCalloc(BootRecord.the_boot_record.sysCallocIP, length);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native org.vmmagic.unboxed.Address sysCalloc(org.vmmagic.unboxed.Address nativeIP, int length);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysFree(org.vmmagic.unboxed.Address location) {
    sysFree(BootRecord.the_boot_record.sysFreeIP, location);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysFree(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address location);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysZeroNT(org.vmmagic.unboxed.Address dst, org.vmmagic.unboxed.Extent cnt) {
    sysZeroNT(BootRecord.the_boot_record.sysZeroNTIP, dst, cnt);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysZeroNT(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address dst, org.vmmagic.unboxed.Extent cnt);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysZero(org.vmmagic.unboxed.Address dst, org.vmmagic.unboxed.Extent cnt) {
    sysZero(BootRecord.the_boot_record.sysZeroIP, dst, cnt);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysZero(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address dst, org.vmmagic.unboxed.Extent cnt);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysZeroPages(org.vmmagic.unboxed.Address dst, int cnt) {
    sysZeroPages(BootRecord.the_boot_record.sysZeroPagesIP, dst, cnt);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysZeroPages(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address dst, int cnt);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysSyncCache(org.vmmagic.unboxed.Address address, int size) {
    sysSyncCache(BootRecord.the_boot_record.sysSyncCacheIP, address, size);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysSyncCache(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address address, int size);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public int sysPerfEventInit(int events) {
    return sysPerfEventInit(BootRecord.the_boot_record.sysPerfEventInitIP, events);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native int sysPerfEventInit(org.vmmagic.unboxed.Address nativeIP, int events);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public int sysPerfEventCreate(int id, byte[] name) {
    return sysPerfEventCreate(BootRecord.the_boot_record.sysPerfEventCreateIP, id, name);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native int sysPerfEventCreate(org.vmmagic.unboxed.Address nativeIP, int id, byte[] name);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysPerfEventEnable() {
    sysPerfEventEnable(BootRecord.the_boot_record.sysPerfEventEnableIP);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysPerfEventEnable(org.vmmagic.unboxed.Address nativeIP);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysPerfEventDisable() {
    sysPerfEventDisable(BootRecord.the_boot_record.sysPerfEventDisableIP);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysPerfEventDisable(org.vmmagic.unboxed.Address nativeIP);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public int sysPerfEventRead(int id, long[] values) {
    return sysPerfEventRead(BootRecord.the_boot_record.sysPerfEventReadIP, id, values);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native int sysPerfEventRead(org.vmmagic.unboxed.Address nativeIP, int id, long[] values);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public int sysStat(byte[] name, int kind) {
    return sysStat(BootRecord.the_boot_record.sysStatIP, name, kind);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native int sysStat(org.vmmagic.unboxed.Address nativeIP, byte[] name, int kind);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public int sysReadByte(int fd) {
    return sysReadByte(BootRecord.the_boot_record.sysReadByteIP, fd);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native int sysReadByte(org.vmmagic.unboxed.Address nativeIP, int fd);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public int sysWriteByte(int fd, int data) {
    return sysWriteByte(BootRecord.the_boot_record.sysWriteByteIP, fd, data);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native int sysWriteByte(org.vmmagic.unboxed.Address nativeIP, int fd, int data);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public int sysReadBytes(int fd, org.vmmagic.unboxed.Address buf, int cnt) {
    return sysReadBytes(BootRecord.the_boot_record.sysReadBytesIP, fd, buf, cnt);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native int sysReadBytes(org.vmmagic.unboxed.Address nativeIP, int fd, org.vmmagic.unboxed.Address buf, int cnt);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public int sysWriteBytes(int fd, org.vmmagic.unboxed.Address buf, int cnt) {
    return sysWriteBytes(BootRecord.the_boot_record.sysWriteBytesIP, fd, buf, cnt);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native int sysWriteBytes(org.vmmagic.unboxed.Address nativeIP, int fd, org.vmmagic.unboxed.Address buf, int cnt);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public int sysBytesAvailable(int fd) {
    return sysBytesAvailable(BootRecord.the_boot_record.sysBytesAvailableIP, fd);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native int sysBytesAvailable(org.vmmagic.unboxed.Address nativeIP, int fd);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public int sysSyncFile(int fd) {
    return sysSyncFile(BootRecord.the_boot_record.sysSyncFileIP, fd);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native int sysSyncFile(org.vmmagic.unboxed.Address nativeIP, int fd);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public int sysSetFdCloseOnExec(int fd) {
    return sysSetFdCloseOnExec(BootRecord.the_boot_record.sysSetFdCloseOnExecIP, fd);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native int sysSetFdCloseOnExec(org.vmmagic.unboxed.Address nativeIP, int fd);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public int sysAccess(byte[] name, int kind) {
    return sysAccess(BootRecord.the_boot_record.sysAccessIP, name, kind);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native int sysAccess(org.vmmagic.unboxed.Address nativeIP, byte[] name, int kind);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public org.vmmagic.unboxed.Address sysMMap(org.vmmagic.unboxed.Address start, org.vmmagic.unboxed.Extent length, int protection, int flags, int fd, org.vmmagic.unboxed.Offset offset) {
    return sysMMap(BootRecord.the_boot_record.sysMMapIP, start, length, protection, flags, fd, offset);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native org.vmmagic.unboxed.Address sysMMap(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address start, org.vmmagic.unboxed.Extent length, int protection, int flags, int fd, org.vmmagic.unboxed.Offset offset);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public org.vmmagic.unboxed.Address sysMMapErrno(org.vmmagic.unboxed.Address start, org.vmmagic.unboxed.Extent length, int protection, int flags, int fd, org.vmmagic.unboxed.Offset offset) {
    return sysMMapErrno(BootRecord.the_boot_record.sysMMapErrnoIP, start, length, protection, flags, fd, offset);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native org.vmmagic.unboxed.Address sysMMapErrno(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address start, org.vmmagic.unboxed.Extent length, int protection, int flags, int fd, org.vmmagic.unboxed.Offset offset);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public int sysMProtect(org.vmmagic.unboxed.Address start, org.vmmagic.unboxed.Extent length, int prot) {
    return sysMProtect(BootRecord.the_boot_record.sysMProtectIP, start, length, prot);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native int sysMProtect(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address start, org.vmmagic.unboxed.Extent length, int prot);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public int sysGetPageSize() {
    return sysGetPageSize(BootRecord.the_boot_record.sysGetPageSizeIP);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native int sysGetPageSize(org.vmmagic.unboxed.Address nativeIP);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public int sysNumProcessors() {
    return sysNumProcessors(BootRecord.the_boot_record.sysNumProcessorsIP);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native int sysNumProcessors(org.vmmagic.unboxed.Address nativeIP);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public org.vmmagic.unboxed.Word sysThreadCreate(org.vmmagic.unboxed.Address tr, org.vmmagic.unboxed.Address ip, org.vmmagic.unboxed.Address fp) {
    return sysThreadCreate(BootRecord.the_boot_record.sysThreadCreateIP, tr, ip, fp);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native org.vmmagic.unboxed.Word sysThreadCreate(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address tr, org.vmmagic.unboxed.Address ip, org.vmmagic.unboxed.Address fp);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public int sysThreadBindSupported() {
    return sysThreadBindSupported(BootRecord.the_boot_record.sysThreadBindSupportedIP);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native int sysThreadBindSupported(org.vmmagic.unboxed.Address nativeIP);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysThreadBind(int cpuId) {
    sysThreadBind(BootRecord.the_boot_record.sysThreadBindIP, cpuId);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysThreadBind(org.vmmagic.unboxed.Address nativeIP, int cpuId);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysThreadYield() {
    sysThreadYield(BootRecord.the_boot_record.sysThreadYieldIP);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysThreadYield(org.vmmagic.unboxed.Address nativeIP);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public org.vmmagic.unboxed.Word sysGetThreadId() {
    return sysGetThreadId(BootRecord.the_boot_record.sysGetThreadIdIP);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native org.vmmagic.unboxed.Word sysGetThreadId(org.vmmagic.unboxed.Address nativeIP);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public org.vmmagic.unboxed.Word sysGetThreadPriorityHandle() {
    return sysGetThreadPriorityHandle(BootRecord.the_boot_record.sysGetThreadPriorityHandleIP);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native org.vmmagic.unboxed.Word sysGetThreadPriorityHandle(org.vmmagic.unboxed.Address nativeIP);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public int sysGetThreadPriority(org.vmmagic.unboxed.Word thread, org.vmmagic.unboxed.Word handle) {
    return sysGetThreadPriority(BootRecord.the_boot_record.sysGetThreadPriorityIP, thread, handle);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native int sysGetThreadPriority(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Word thread, org.vmmagic.unboxed.Word handle);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public int sysSetThreadPriority(org.vmmagic.unboxed.Word thread, org.vmmagic.unboxed.Word handle, int priority) {
    return sysSetThreadPriority(BootRecord.the_boot_record.sysSetThreadPriorityIP, thread, handle, priority);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native int sysSetThreadPriority(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Word thread, org.vmmagic.unboxed.Word handle, int priority);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysSetupHardwareTrapHandler() {
    sysSetupHardwareTrapHandler(BootRecord.the_boot_record.sysSetupHardwareTrapHandlerIP);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysSetupHardwareTrapHandler(org.vmmagic.unboxed.Address nativeIP);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public int sysStashVMThread(org.jikesrvm.scheduler.RVMThread vmThread) {
    return sysStashVMThread(BootRecord.the_boot_record.sysStashVMThreadIP, vmThread);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native int sysStashVMThread(org.vmmagic.unboxed.Address nativeIP, org.jikesrvm.scheduler.RVMThread vmThread);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysThreadTerminate() {
    sysThreadTerminate(BootRecord.the_boot_record.sysThreadTerminateIP);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysThreadTerminate(org.vmmagic.unboxed.Address nativeIP);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public org.vmmagic.unboxed.Word sysMonitorCreate() {
    return sysMonitorCreate(BootRecord.the_boot_record.sysMonitorCreateIP);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native org.vmmagic.unboxed.Word sysMonitorCreate(org.vmmagic.unboxed.Address nativeIP);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysMonitorDestroy(org.vmmagic.unboxed.Word monitor) {
    sysMonitorDestroy(BootRecord.the_boot_record.sysMonitorDestroyIP, monitor);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysMonitorDestroy(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Word monitor);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysMonitorEnter(org.vmmagic.unboxed.Word monitor) {
    sysMonitorEnter(BootRecord.the_boot_record.sysMonitorEnterIP, monitor);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysMonitorEnter(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Word monitor);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysMonitorEnterDbg(org.vmmagic.unboxed.Word monitor) {
    sysMonitorEnterDbg(BootRecord.the_boot_record.sysMonitorEnterDbgIP, monitor);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysMonitorEnterDbg(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Word monitor);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysMonitorExit(org.vmmagic.unboxed.Word monitor) {
    sysMonitorExit(BootRecord.the_boot_record.sysMonitorExitIP, monitor);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysMonitorExit(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Word monitor);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysMonitorExitDbg(org.vmmagic.unboxed.Word monitor) {
    sysMonitorExitDbg(BootRecord.the_boot_record.sysMonitorExitDbgIP, monitor);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysMonitorExitDbg(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Word monitor);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysMonitorTimedWaitAbsolute(org.vmmagic.unboxed.Word monitor, long whenWakeupNanos) {
    sysMonitorTimedWaitAbsolute(BootRecord.the_boot_record.sysMonitorTimedWaitAbsoluteIP, monitor, whenWakeupNanos);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysMonitorTimedWaitAbsolute(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Word monitor, long whenWakeupNanos);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysMonitorWait(org.vmmagic.unboxed.Word monitor) {
    sysMonitorWait(BootRecord.the_boot_record.sysMonitorWaitIP, monitor);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysMonitorWait(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Word monitor);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysMonitorBroadcast(org.vmmagic.unboxed.Word monitor) {
    sysMonitorBroadcast(BootRecord.the_boot_record.sysMonitorBroadcastIP, monitor);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysMonitorBroadcast(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Word monitor);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysMonitorWaitDbg(org.vmmagic.unboxed.Word monitor) {
    sysMonitorWaitDbg(BootRecord.the_boot_record.sysMonitorWaitDbgIP, monitor);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysMonitorWaitDbg(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Word monitor);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysMonitorBroadcastDbg(org.vmmagic.unboxed.Word monitor) {
    sysMonitorBroadcastDbg(BootRecord.the_boot_record.sysMonitorBroadcastDbgIP, monitor);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysMonitorBroadcastDbg(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Word monitor);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public long sysLongDivide(long x, long y) {
    return sysLongDivide(BootRecord.the_boot_record.sysLongDivideIP, x, y);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native long sysLongDivide(org.vmmagic.unboxed.Address nativeIP, long x, long y);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public long sysLongRemainder(long x, long y) {
    return sysLongRemainder(BootRecord.the_boot_record.sysLongRemainderIP, x, y);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native long sysLongRemainder(org.vmmagic.unboxed.Address nativeIP, long x, long y);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public float sysLongToFloat(long x) {
    return sysLongToFloat(BootRecord.the_boot_record.sysLongToFloatIP, x);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native float sysLongToFloat(org.vmmagic.unboxed.Address nativeIP, long x);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public double sysLongToDouble(long x) {
    return sysLongToDouble(BootRecord.the_boot_record.sysLongToDoubleIP, x);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native double sysLongToDouble(org.vmmagic.unboxed.Address nativeIP, long x);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public int sysFloatToInt(float x) {
    return sysFloatToInt(BootRecord.the_boot_record.sysFloatToIntIP, x);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native int sysFloatToInt(org.vmmagic.unboxed.Address nativeIP, float x);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public int sysDoubleToInt(double x) {
    return sysDoubleToInt(BootRecord.the_boot_record.sysDoubleToIntIP, x);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native int sysDoubleToInt(org.vmmagic.unboxed.Address nativeIP, double x);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public long sysFloatToLong(float x) {
    return sysFloatToLong(BootRecord.the_boot_record.sysFloatToLongIP, x);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native long sysFloatToLong(org.vmmagic.unboxed.Address nativeIP, float x);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public long sysDoubleToLong(double x) {
    return sysDoubleToLong(BootRecord.the_boot_record.sysDoubleToLongIP, x);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native long sysDoubleToLong(org.vmmagic.unboxed.Address nativeIP, double x);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public double sysDoubleRemainder(double x, double y) {
    return sysDoubleRemainder(BootRecord.the_boot_record.sysDoubleRemainderIP, x, y);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native double sysDoubleRemainder(org.vmmagic.unboxed.Address nativeIP, double x, double y);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public float sysPrimitiveParseFloat(byte[] buf) {
    return sysPrimitiveParseFloat(BootRecord.the_boot_record.sysPrimitiveParseFloatIP, buf);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native float sysPrimitiveParseFloat(org.vmmagic.unboxed.Address nativeIP, byte[] buf);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public int sysPrimitiveParseInt(byte[] buf) {
    return sysPrimitiveParseInt(BootRecord.the_boot_record.sysPrimitiveParseIntIP, buf);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native int sysPrimitiveParseInt(org.vmmagic.unboxed.Address nativeIP, byte[] buf);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public long sysParseMemorySize(byte[] sizeName, byte[] sizeFlag, byte[] defaultFactor, int roundTo, byte[] argToken, byte[] subArg) {
    return sysParseMemorySize(BootRecord.the_boot_record.sysParseMemorySizeIP, sizeName, sizeFlag, defaultFactor, roundTo, argToken, subArg);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native long sysParseMemorySize(org.vmmagic.unboxed.Address nativeIP, byte[] sizeName, byte[] sizeFlag, byte[] defaultFactor, int roundTo, byte[] argToken, byte[] subArg);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public long sysCurrentTimeMillis() {
    return sysCurrentTimeMillis(BootRecord.the_boot_record.sysCurrentTimeMillisIP);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native long sysCurrentTimeMillis(org.vmmagic.unboxed.Address nativeIP);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public long sysNanoTime() {
    return sysNanoTime(BootRecord.the_boot_record.sysNanoTimeIP);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native long sysNanoTime(org.vmmagic.unboxed.Address nativeIP);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysNanoSleep(long howLongNanos) {
    sysNanoSleep(BootRecord.the_boot_record.sysNanoSleepIP, howLongNanos);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysNanoSleep(org.vmmagic.unboxed.Address nativeIP, long howLongNanos);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public org.vmmagic.unboxed.Address sysDlopen(byte[] libname) {
    return sysDlopen(BootRecord.the_boot_record.sysDlopenIP, libname);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native org.vmmagic.unboxed.Address sysDlopen(org.vmmagic.unboxed.Address nativeIP, byte[] libname);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public org.vmmagic.unboxed.Address sysDlsym(org.vmmagic.unboxed.Address libHandler, byte[] symbolName) {
    return sysDlsym(BootRecord.the_boot_record.sysDlsymIP, libHandler, symbolName);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native org.vmmagic.unboxed.Address sysDlsym(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address libHandler, byte[] symbolName);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysCreateThreadSpecificDataKeys() {
    sysCreateThreadSpecificDataKeys(BootRecord.the_boot_record.sysCreateThreadSpecificDataKeysIP);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysCreateThreadSpecificDataKeys(org.vmmagic.unboxed.Address nativeIP);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysEnableAlignmentChecking() {
    sysEnableAlignmentChecking(BootRecord.the_boot_record.sysEnableAlignmentCheckingIP);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysEnableAlignmentChecking(org.vmmagic.unboxed.Address nativeIP);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysDisableAlignmentChecking() {
    sysDisableAlignmentChecking(BootRecord.the_boot_record.sysDisableAlignmentCheckingIP);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysDisableAlignmentChecking(org.vmmagic.unboxed.Address nativeIP);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysReportAlignmentChecking() {
    sysReportAlignmentChecking(BootRecord.the_boot_record.sysReportAlignmentCheckingIP);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysReportAlignmentChecking(org.vmmagic.unboxed.Address nativeIP);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public org.vmmagic.unboxed.Address gcspyDriverAddStream(org.vmmagic.unboxed.Address driver, int id) {
    return gcspyDriverAddStream(BootRecord.the_boot_record.gcspyDriverAddStreamIP, driver, id);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native org.vmmagic.unboxed.Address gcspyDriverAddStream(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address driver, int id);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void gcspyDriverEndOutput(org.vmmagic.unboxed.Address driver) {
    gcspyDriverEndOutput(BootRecord.the_boot_record.gcspyDriverEndOutputIP, driver);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void gcspyDriverEndOutput(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address driver);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void gcspyDriverInit(org.vmmagic.unboxed.Address driver, int id, org.vmmagic.unboxed.Address serverName, org.vmmagic.unboxed.Address driverName, org.vmmagic.unboxed.Address title, org.vmmagic.unboxed.Address blockInfo, int tileNum, org.vmmagic.unboxed.Address unused, int mainSpace) {
    gcspyDriverInit(BootRecord.the_boot_record.gcspyDriverInitIP, driver, id, serverName, driverName, title, blockInfo, tileNum, unused, mainSpace);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void gcspyDriverInit(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address driver, int id, org.vmmagic.unboxed.Address serverName, org.vmmagic.unboxed.Address driverName, org.vmmagic.unboxed.Address title, org.vmmagic.unboxed.Address blockInfo, int tileNum, org.vmmagic.unboxed.Address unused, int mainSpace);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void gcspyDriverInitOutput(org.vmmagic.unboxed.Address driver) {
    gcspyDriverInitOutput(BootRecord.the_boot_record.gcspyDriverInitOutputIP, driver);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void gcspyDriverInitOutput(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address driver);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void gcspyDriverResize(org.vmmagic.unboxed.Address driver, int size) {
    gcspyDriverResize(BootRecord.the_boot_record.gcspyDriverResizeIP, driver, size);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void gcspyDriverResize(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address driver, int size);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void gcspyDriverSetTileNameRange(org.vmmagic.unboxed.Address driver, int i, org.vmmagic.unboxed.Address start, org.vmmagic.unboxed.Address end) {
    gcspyDriverSetTileNameRange(BootRecord.the_boot_record.gcspyDriverSetTileNameRangeIP, driver, i, start, end);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void gcspyDriverSetTileNameRange(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address driver, int i, org.vmmagic.unboxed.Address start, org.vmmagic.unboxed.Address end);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void gcspyDriverSetTileName(org.vmmagic.unboxed.Address driver, int i, org.vmmagic.unboxed.Address start, long value) {
    gcspyDriverSetTileName(BootRecord.the_boot_record.gcspyDriverSetTileNameIP, driver, i, start, value);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void gcspyDriverSetTileName(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address driver, int i, org.vmmagic.unboxed.Address start, long value);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void gcspyDriverSpaceInfo(org.vmmagic.unboxed.Address driver, org.vmmagic.unboxed.Address info) {
    gcspyDriverSpaceInfo(BootRecord.the_boot_record.gcspyDriverSpaceInfoIP, driver, info);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void gcspyDriverSpaceInfo(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address driver, org.vmmagic.unboxed.Address info);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void gcspyDriverStartComm(org.vmmagic.unboxed.Address driver) {
    gcspyDriverStartComm(BootRecord.the_boot_record.gcspyDriverStartCommIP, driver);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void gcspyDriverStartComm(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address driver);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void gcspyDriverStream(org.vmmagic.unboxed.Address driver, int id, int len) {
    gcspyDriverStream(BootRecord.the_boot_record.gcspyDriverStreamIP, driver, id, len);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void gcspyDriverStream(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address driver, int id, int len);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void gcspyDriverStreamByteValue(org.vmmagic.unboxed.Address driver, byte value) {
    gcspyDriverStreamByteValue(BootRecord.the_boot_record.gcspyDriverStreamByteValueIP, driver, value);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void gcspyDriverStreamByteValue(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address driver, byte value);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void gcspyDriverStreamShortValue(org.vmmagic.unboxed.Address driver, short value) {
    gcspyDriverStreamShortValue(BootRecord.the_boot_record.gcspyDriverStreamShortValueIP, driver, value);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void gcspyDriverStreamShortValue(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address driver, short value);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void gcspyDriverStreamIntValue(org.vmmagic.unboxed.Address driver, int value) {
    gcspyDriverStreamIntValue(BootRecord.the_boot_record.gcspyDriverStreamIntValueIP, driver, value);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void gcspyDriverStreamIntValue(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address driver, int value);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void gcspyDriverSummary(org.vmmagic.unboxed.Address driver, int id, int len) {
    gcspyDriverSummary(BootRecord.the_boot_record.gcspyDriverSummaryIP, driver, id, len);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void gcspyDriverSummary(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address driver, int id, int len);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void gcspyDriverSummaryValue(org.vmmagic.unboxed.Address driver, int value) {
    gcspyDriverSummaryValue(BootRecord.the_boot_record.gcspyDriverSummaryValueIP, driver, value);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void gcspyDriverSummaryValue(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address driver, int value);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void gcspyIntWriteControl(org.vmmagic.unboxed.Address driver, int id, int tileNum) {
    gcspyIntWriteControl(BootRecord.the_boot_record.gcspyIntWriteControlIP, driver, id, tileNum);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void gcspyIntWriteControl(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address driver, int id, int tileNum);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public org.vmmagic.unboxed.Address gcspyMainServerAddDriver(org.vmmagic.unboxed.Address addr) {
    return gcspyMainServerAddDriver(BootRecord.the_boot_record.gcspyMainServerAddDriverIP, addr);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native org.vmmagic.unboxed.Address gcspyMainServerAddDriver(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address addr);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void gcspyMainServerAddEvent(org.vmmagic.unboxed.Address server, int event, org.vmmagic.unboxed.Address name) {
    gcspyMainServerAddEvent(BootRecord.the_boot_record.gcspyMainServerAddEventIP, server, event, name);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void gcspyMainServerAddEvent(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address server, int event, org.vmmagic.unboxed.Address name);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public org.vmmagic.unboxed.Address gcspyMainServerInit(int port, int len, org.vmmagic.unboxed.Address name, int verbose) {
    return gcspyMainServerInit(BootRecord.the_boot_record.gcspyMainServerInitIP, port, len, name, verbose);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native org.vmmagic.unboxed.Address gcspyMainServerInit(org.vmmagic.unboxed.Address nativeIP, int port, int len, org.vmmagic.unboxed.Address name, int verbose);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public int gcspyMainServerIsConnected(org.vmmagic.unboxed.Address server, int event) {
    return gcspyMainServerIsConnected(BootRecord.the_boot_record.gcspyMainServerIsConnectedIP, server, event);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native int gcspyMainServerIsConnected(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address server, int event);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public org.vmmagic.unboxed.Address gcspyMainServerOuterLoop() {
    return gcspyMainServerOuterLoop(BootRecord.the_boot_record.gcspyMainServerOuterLoopIP);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native org.vmmagic.unboxed.Address gcspyMainServerOuterLoop(org.vmmagic.unboxed.Address nativeIP);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void gcspyMainServerSafepoint(org.vmmagic.unboxed.Address server, int event) {
    gcspyMainServerSafepoint(BootRecord.the_boot_record.gcspyMainServerSafepointIP, server, event);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void gcspyMainServerSafepoint(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address server, int event);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void gcspyMainServerSetGeneralInfo(org.vmmagic.unboxed.Address server, org.vmmagic.unboxed.Address info) {
    gcspyMainServerSetGeneralInfo(BootRecord.the_boot_record.gcspyMainServerSetGeneralInfoIP, server, info);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void gcspyMainServerSetGeneralInfo(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address server, org.vmmagic.unboxed.Address info);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void gcspyMainServerStartCompensationTimer(org.vmmagic.unboxed.Address server) {
    gcspyMainServerStartCompensationTimer(BootRecord.the_boot_record.gcspyMainServerStartCompensationTimerIP, server);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void gcspyMainServerStartCompensationTimer(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address server);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void gcspyMainServerStopCompensationTimer(org.vmmagic.unboxed.Address server) {
    gcspyMainServerStopCompensationTimer(BootRecord.the_boot_record.gcspyMainServerStopCompensationTimerIP, server);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void gcspyMainServerStopCompensationTimer(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address server);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void gcspyStartserver(org.vmmagic.unboxed.Address server, int wait, org.vmmagic.unboxed.Address serverOuterLoop) {
    gcspyStartserver(BootRecord.the_boot_record.gcspyStartserverIP, server, wait, serverOuterLoop);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void gcspyStartserver(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address server, int wait, org.vmmagic.unboxed.Address serverOuterLoop);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void gcspyStreamInit(org.vmmagic.unboxed.Address stream, int id, int dataType, org.vmmagic.unboxed.Address name, int minValue, int maxValue, int zeroValue, int defaultValue, org.vmmagic.unboxed.Address pre, org.vmmagic.unboxed.Address post, int presentation, int paintStyle, int maxStreamIndex, int red, int green, int blue) {
    gcspyStreamInit(BootRecord.the_boot_record.gcspyStreamInitIP, stream, id, dataType, name, minValue, maxValue, zeroValue, defaultValue, pre, post, presentation, paintStyle, maxStreamIndex, red, green, blue);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void gcspyStreamInit(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address stream, int id, int dataType, org.vmmagic.unboxed.Address name, int minValue, int maxValue, int zeroValue, int defaultValue, org.vmmagic.unboxed.Address pre, org.vmmagic.unboxed.Address post, int presentation, int paintStyle, int maxStreamIndex, int red, int green, int blue);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void gcspyFormatSize(org.vmmagic.unboxed.Address buffer, int size) {
    gcspyFormatSize(BootRecord.the_boot_record.gcspyFormatSizeIP, buffer, size);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void gcspyFormatSize(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address buffer, int size);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public int gcspySprintf(org.vmmagic.unboxed.Address str, org.vmmagic.unboxed.Address format, org.vmmagic.unboxed.Address value) {
    return gcspySprintf(BootRecord.the_boot_record.gcspySprintfIP, str, format, value);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native int gcspySprintf(org.vmmagic.unboxed.Address nativeIP, org.vmmagic.unboxed.Address str, org.vmmagic.unboxed.Address format, org.vmmagic.unboxed.Address value);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void logInt(int v) {
    logInt(BootRecord.the_boot_record.logIntIP, v);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void logInt(org.vmmagic.unboxed.Address nativeIP, int v);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void logLong(long v) {
    logLong(BootRecord.the_boot_record.logLongIP, v);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void logLong(org.vmmagic.unboxed.Address nativeIP, long v);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void logLongln(long v) {
    logLongln(BootRecord.the_boot_record.logLonglnIP, v);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void logLongln(org.vmmagic.unboxed.Address nativeIP, long v);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void logChar(char v) {
    logChar(BootRecord.the_boot_record.logCharIP, v);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void logChar(org.vmmagic.unboxed.Address nativeIP, char v);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void logHex(int v) {
    logHex(BootRecord.the_boot_record.logHexIP, v);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void logHex(org.vmmagic.unboxed.Address nativeIP, int v);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void logIntln(int v) {
    logIntln(BootRecord.the_boot_record.logIntlnIP, v);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void logIntln(org.vmmagic.unboxed.Address nativeIP, int v);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void logCharln(char v) {
    logCharln(BootRecord.the_boot_record.logCharlnIP, v);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void logCharln(org.vmmagic.unboxed.Address nativeIP, char v);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void logln() {
    logln(BootRecord.the_boot_record.loglnIP);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void logln(org.vmmagic.unboxed.Address nativeIP);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void logHexln(int v) {
    logHexln(BootRecord.the_boot_record.logHexlnIP, v);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void logHexln(org.vmmagic.unboxed.Address nativeIP, int v);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void logGC(int tid, int gc, long startTime, int pagesFreed, long nanosElapsed) {
    logGC(BootRecord.the_boot_record.logGCIP, tid, gc, startTime, pagesFreed, nanosElapsed);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void logGC(org.vmmagic.unboxed.Address nativeIP, int tid, int gc, long startTime, int pagesFreed, long nanosElapsed);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void logThread(int tid, int monotonicID, int category, int nameLength) {
    logThread(BootRecord.the_boot_record.logThreadIP, tid, monotonicID, category, nameLength);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void logThread(org.vmmagic.unboxed.Address nativeIP, int tid, int monotonicID, int category, int nameLength);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void logThreadCounter(int tid, int monotonicID, int counterId, int counterValue) {
    logThreadCounter(BootRecord.the_boot_record.logThreadCounterIP, tid, monotonicID, counterId, counterValue);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void logThreadCounter(org.vmmagic.unboxed.Address nativeIP, int tid, int monotonicID, int counterId, int counterValue);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void logExtraChar(int tid, char v) {
    logExtraChar(BootRecord.the_boot_record.logExtraCharIP, tid, v);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void logExtraChar(org.vmmagic.unboxed.Address nativeIP, int tid, char v);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void logSpace(int tid, int spaceID, int lengthOfName) {
    logSpace(BootRecord.the_boot_record.logSpaceIP, tid, spaceID, lengthOfName);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void logSpace(org.vmmagic.unboxed.Address nativeIP, int tid, int spaceID, int lengthOfName);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void logType(int tid, int typeID, boolean isArray, boolean isPrimitive, boolean hasFinalizer, int nPrimFields, int nRefFields, int nStaticPrimFields, int nStaticRefFields, int lengthofname) {
    logType(BootRecord.the_boot_record.logTypeIP, tid, typeID, isArray, isPrimitive, hasFinalizer, nPrimFields, nRefFields, nStaticPrimFields, nStaticRefFields, lengthofname);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void logType(org.vmmagic.unboxed.Address nativeIP, int tid, int typeID, boolean isArray, boolean isPrimitive, boolean hasFinalizer, int nPrimFields, int nRefFields, int nStaticPrimFields, int nStaticRefFields, int lengthofname);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void logTIGC(int tid, int threadID, int tigc, long startTime, int pagesFreed, long nanosElapsed, int liveWhite, long remsetGreyEntries, long remsetBlackEntries, long reclaimedCellsSize) {
    logTIGC(BootRecord.the_boot_record.logTIGCIP, tid, threadID, tigc, startTime, pagesFreed, nanosElapsed, liveWhite, remsetGreyEntries, remsetBlackEntries, reclaimedCellsSize);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void logTIGC(org.vmmagic.unboxed.Address nativeIP, int tid, int threadID, int tigc, long startTime, int pagesFreed, long nanosElapsed, int liveWhite, long remsetGreyEntries, long remsetBlackEntries, long reclaimedCellsSize);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void logThreadCounterOverflow(int tid, int monotonicID, int counterId, int counterOverflowValue) {
    logThreadCounterOverflow(BootRecord.the_boot_record.logThreadCounterOverflowIP, tid, monotonicID, counterId, counterOverflowValue);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void logThreadCounterOverflow(org.vmmagic.unboxed.Address nativeIP, int tid, int monotonicID, int counterId, int counterOverflowValue);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public void sysSetBind(int cpuId) {
    sysSetBind(BootRecord.the_boot_record.sysSetBindIP, cpuId);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native void sysSetBind(org.vmmagic.unboxed.Address nativeIP, int cpuId);

  @java.lang.Override
  @org.vmmagic.pragma.NoInstrument
  public int sysGetBind() {
    return sysGetBind(BootRecord.the_boot_record.sysGetBindIP);
  }

  @org.vmmagic.pragma.SysCallNative
  private static native int sysGetBind(org.vmmagic.unboxed.Address nativeIP);

}