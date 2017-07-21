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
package org.jikesrvm.objectmodel;

import static org.jikesrvm.SizeConstants.BYTES_IN_ADDRESS;
import static org.jikesrvm.SizeConstants.LOG_BYTES_IN_ADDRESS;
import static org.jikesrvm.objectmodel.MiscHeaderConstants.GC_TRACING_HEADER_BYTES;
import static org.jikesrvm.objectmodel.MiscHeaderConstants.NUM_BYTES_HEADER;

import org.jikesrvm.VM;
import org.jikesrvm.mm.mminterface.MemoryManagerConstants;
import org.jikesrvm.runtime.Entrypoints;
import org.jikesrvm.runtime.Magic;
import org.jikesrvm.scheduler.RVMThread;
import org.mmtk.plan.MutatorContext;
import org.vmmagic.pragma.Entrypoint;
import org.vmmagic.pragma.Inline;
import org.vmmagic.pragma.Interruptible;
import org.vmmagic.pragma.NoInline;
import org.vmmagic.pragma.NoInstrument;
import org.vmmagic.pragma.Uninterruptible;
import org.vmmagic.unboxed.Address;
import org.vmmagic.unboxed.ObjectReference;
import org.vmmagic.unboxed.Offset;
import org.vmmagic.unboxed.Word;

/**
 * Defines other header words not used for
 * core Java language support of memory allocation.
 * Typically these are extra header words used for various
 * kinds of instrumentation or profiling.
 *
 * @see ObjectModel
 */
@Uninterruptible
public final class MiscHeader {

  private static final Offset MISC_HEADER_START = JavaHeaderConstants.MISC_HEADER_OFFSET;

  /* offset from object ref to .oid field, in bytes */
  static final Offset OBJECT_OID_OFFSET = MISC_HEADER_START;
  /* offset from object ref to OBJECT_DEATH field, in bytes */
  static final Offset OBJECT_DEATH_OFFSET = OBJECT_OID_OFFSET.plus(BYTES_IN_ADDRESS);
  /* offset from object ref to .link field, in bytes */
  static final Offset OBJECT_LINK_OFFSET = OBJECT_DEATH_OFFSET.plus(BYTES_IN_ADDRESS);

  /////////////////////////
  // Support for YYY (an example of how to add a word to all objects)
  /////////////////////////
  // offset from object ref to yet-to-be-defined instrumentation word
  // static final int YYY_DATA_OFFSET_1 = (VM.YYY ? MISC_HEADER_START + GC_TRACING_HEADER_WORDS : 0);
  // static final int YYY_DATA_OFFSET_2 = (VM.YYY ? MISC_HEADER_START + GC_TRACING_HEADER_WORDS + 4 : 0);
  // static final int YYY_HEADER_BYTES = (VM.YYY ? 8 : 0);
  
  public static final Offset UNUSED = MISC_HEADER_START.plus(MiscHeaderConstants.GC_TRACING_HEADER_WORDS);
  
  //00000000 00000000 00GTTTTT TTTTTCCC
  public static final Offset STATUS2 = MISC_HEADER_START.plus(MiscHeaderConstants.GC_TRACING_HEADER_WORDS + 4);
  public static final int COLOUR_SHIFT = 0;
  public static final int COLOUR_MASK = 0x3;
  public static final int ALLOCATING_THREAD_SHIFT = 3;
  public static final int ALLOCATING_THREAD_MASK = 0x3FF8;
  public static final Offset OBJECT_ID = MISC_HEADER_START.plus(MiscHeaderConstants.GC_TRACING_HEADER_WORDS + 8);
    
  public static final int COLOURLESS = 0; //00
  public static final int WHITE = 1; //01
  public static final int GREY  = 2; //10
  public static final int BLACK = 3; //11
  
  public static int bootThreadAddress = 0x0;

  @NoInstrument
  public static int getAllocatingThread(ObjectReference o) {
    Address rvmthread = Address.fromIntZeroExtend(Magic.getIntAtOffset(o, STATUS2) & (~COLOUR_MASK));
    if (rvmthread.isZero()) {
      return 1;
    }
    return Magic.getIntAtOffset(rvmthread.toObjectReference(), Entrypoints.rvmmonotonicThreadID.getOffset());
  }
  
  @NoInstrument
  @Inline
  public static boolean isMine(ObjectReference o) {
    if (VM.VerifyAssertions) {
      VM._assert((ObjectReference.fromObject(Magic.getESIAsThread()).toAddress().toInt() & COLOUR_MASK) == 0);
    }
    return (Magic.getIntAtOffset(o, STATUS2) & (~COLOUR_MASK)) == ObjectReference.fromObject(Magic.getESIAsThread()).toAddress().toInt();
  }
  
  @NoInstrument
  @Inline
  public static int getOriginalAllocatingThread(ObjectReference o) {
    return MiscHeader.getAllocatingThread(o);
    /*if (threadID == 0 || threadID == 1024) {
      threadID = 1;
    }
    if (threadID > 1024) {
      return threadID - 1024;
    }
    else {
      return threadID;
    }*/
  }
    
  @NoInstrument
  @Inline
  public static int getColour(ObjectReference o) {
    return (Magic.getIntAtOffset(o, STATUS2) & COLOUR_MASK);
  }
  
  /**
   * Set an object's colour.
   * 
   * Contract: Colour can only progress WHITE -> GREY -> BLACK.
               There is no restriction on going from WHITE straight to BLACK.
   * Contract: Currently fails if you supply the same colour. This is inefficient and results in a useless CAS.
   * 
   * Aside: Also checks that the allocating thread ID is set and within the valid range.
   */
  @NoInstrument
  @NoInline
  public static void setColour(ObjectReference o, int colour) {
    if (VM.VerifyAssertions) {
      VM._assert(colour == WHITE || colour == GREY || colour == BLACK, "Setting an Invalid colour");
    }
    Magic.setIntAtOffset(o, STATUS2, (Magic.getIntAtOffset(o, STATUS2) & (~COLOUR_MASK)) | colour);
  }
    
  @NoInstrument
  @NoInline
  public static void setAllocID(ObjectReference o, int threadAllocN) {
    Magic.setIntAtOffset(o, OBJECT_ID, threadAllocN);
  }
    
  @NoInstrument
  @NoInline
  public static int getAllocID(ObjectReference o) {
    return Magic.getIntAtOffset(o, OBJECT_ID);
  }
  
  /**
   * Set the allocating thread and colour of an object. This is called on allocation.
   * 
   * Contract: This should be called as soon as the object has been allocated and before any mutator operates on this object.
   * Contract: This should be called ONCE and is mutually exclusive with setAllocatingThread.
   */
  @NoInstrument
  @NoInline
  public static void setAllocatingThreadAndColour(ObjectReference o, int threadID, int colour) {
    if (VM.VerifyAssertions) {
      VM._assert(threadID >= 0 && threadID <= 1023, "Thread ID must be within 0 -> 1023 inclusive.");
      VM._assert(colour == WHITE || colour == GREY || colour == BLACK, "Setting an Invalid colour");
    }
    Magic.setIntAtOffset(o, STATUS2, (ObjectReference.fromObject(Magic.getESIAsThread()).toAddress().toInt() & (~COLOUR_MASK)) | colour);
    if (VM.VerifyAssertions) {
      VM._assert((ObjectReference.fromObject(Magic.getESIAsThread()).toAddress().toInt() & COLOUR_MASK) == 0);
    }
  }
  
  @NoInstrument
  @NoInline
  public static void setBootThreadAndColour(ObjectReference o, int threadID, int colour) {
    if (VM.VerifyAssertions) {
      VM._assert(threadID >= 0 && threadID <= 1023, "Thread ID must be within 0 -> 1023 inclusive.");
      VM._assert(colour == WHITE || colour == GREY || colour == BLACK, "Setting an Invalid colour");
    }
    Magic.setIntAtOffset(o, STATUS2, (bootThreadAddress & (~COLOUR_MASK)) | colour);
    if (VM.VerifyAssertions) {
      VM._assert((bootThreadAddress & COLOUR_MASK) == 0);
    }
  }
  
  /**
   * Set the globalise bit of the object. This means that calls to getAllocatingThread by the allocating thread
   * will report that the object is NOT allocated by that thread. The thread by globalising the object has relinquished
   * ownership.
   */
  @NoInstrument
  @NoInline
  public static void setGlobaliseBit(ObjectReference o) {
    /*while (true) {
      int prevStatus = Magic.prepareInt(o, STATUS);
      if (Magic.attemptInt(o, STATUS, prevStatus, (prevStatus | (0x400 << ALLOCATING_THREAD_SHIFT)))) {
        break;
      }
    }*/
    //Magic.setIntAtOffset(o, STATUS2, (Magic.getIntAtOffset(o, STATUS2) & COLOUR_SHIFT));
  }
  
  @NoInstrument
  @NoInline
  public static void dumpAllHeaders(ObjectReference o) {
    RVMThread.logStr("dump for ");
    RVMThread.getCurrentThreadMonotonicNI();
    RVMThread.logHexln(o.toAddress());
     
    if (o.isNull()) {
      RVMThread.logStrln("[NULL]");
      return;
    }

    RVMThread.logStrln("[High]");
    int i;
     
    RVMThread.logStr("F0: ");
    for (i = 0; i < JavaHeaderConstants.ARRAY_LENGTH_BYTES; i=i+4) {
      Offset off = JavaHeaderConstants.ARRAY_LENGTH_OFFSET.minus(i);
      RVMThread.logHex(Magic.getWordAtOffset(o, off).toAddress());
      RVMThread.logStr(" ");
    }
    RVMThread.logStrln("");
     
    RVMThread.logStr("JH: ");
    for (i = 0; i < JavaHeaderConstants.JAVA_HEADER_BYTES; i=i+4) {
      Offset off = JavaHeaderConstants.JAVA_HEADER_OFFSET.plus(i);
      RVMThread.logHex(Magic.getWordAtOffset(o, off).toAddress());
      RVMThread.logStr(" ");
    }
    RVMThread.logStrln("");
     
    RVMThread.logStr("MH: ");
    for (i = 0; i < JavaHeaderConstants.MISC_HEADER_BYTES; i=i+4) {
      Offset off = JavaHeaderConstants.MISC_HEADER_OFFSET.plus(i);
      RVMThread.logHex(Magic.getWordAtOffset(o, off).toAddress());
      RVMThread.logStr(" ");
    }
    RVMThread.logStrln("");
     
    RVMThread.logStr("GC: ");
    for (i = 0; i < JavaHeaderConstants.GC_HEADER_BYTES; i=i+4) {
      Offset off = JavaHeaderConstants.GC_HEADER_OFFSET.plus(i);
      RVMThread.logHex(Magic.getWordAtOffset(o, off).toAddress());
      RVMThread.logStr(" ");
    }
    RVMThread.logStrln("");
     
    RVMThread.logStrln("[Low]");
     
    if (!o.isNull()) {
      RVMThread.logStrln("[F1]");
       
      for (i = 0; i < (ObjectModel.bytesUsed(o.toObject()) - JavaHeader.ARRAY_HEADER_SIZE); i=i+4) {
        Offset off = Offset.fromIntZeroExtend(i);
        Word v = Magic.getWordAtOffset(o, off);
        if (!v.isZero()) {
          RVMThread.logInt(i);
          RVMThread.logStr(":");
          RVMThread.logHex(v.toAddress());
          RVMThread.logStr(" ");
        }
      }
      RVMThread.logStrln("");
       
      RVMThread.logStrln("[F END]");
       
      if (o.isNull()) {
        RVMThread.logStrln("[NULL]");
      }
      else {
        RVMThread.logAtom(Magic.getObjectType(o).getDescriptorNI());
        RVMThread.logStrln("");
         
        RVMThread.logInt(MiscHeader.getAllocatingThread(o));
        RVMThread.logStr(".");
        RVMThread.logIntln(MiscHeader.getAllocID(o));
      }
    }
  }
  
  /**
   * How many available bits does the misc header want to use?
   */
  static final int REQUESTED_BITS = 0;

  /**
   * The next object ID to be used.
   */
  @Entrypoint
  private static Word oid;
  /**
   * The current "time" for the trace being generated.
   */
  private static Word time;
  /**
   * The address of the last object allocated into the header.
   */
  @Entrypoint
  private static Word prevAddress;

  static {
    oid = Word.fromIntSignExtend(4);
    time = Word.fromIntSignExtend(4);
    prevAddress = Word.zero();
  }

  /**
   * Perform any required initialization of the MISC portion of the header.
   * @param obj the object ref to the storage to be initialized
   * @param tib the TIB of the instance being created
   * @param size the number of bytes allocated by the GC system for this object.
   * @param isScalar are we initializing a scalar (true) or array (false) object?
   */
  @Uninterruptible
  public static void initializeHeader(Object obj, TIB tib, int size, boolean isScalar) {
    /* Only perform initialization when it is required */
    if (MemoryManagerConstants.GENERATE_GC_TRACE) {
      Address ref = Magic.objectAsAddress(obj);
      ref.store(oid, OBJECT_OID_OFFSET);
      ref.store(time, OBJECT_DEATH_OFFSET);
      oid = oid.plus(Word.fromIntSignExtend((size - GC_TRACING_HEADER_BYTES) >> LOG_BYTES_IN_ADDRESS));
    }
  }

  /**
   * Perform any required initialization of the MISC portion of the header.
   * @param bootImage the bootimage being written
   * @param ref the object ref to the storage to be initialized
   * @param tib the TIB of the instance being created
   * @param size the number of bytes allocated by the GC system for this object.
   * @param isScalar are we initializing a scalar (true) or array (false) object?
   */
  @Interruptible("Only called during boot iamge creation")
  public static void initializeHeader(BootImageInterface bootImage, Address ref, TIB tib, int size,
                                      boolean isScalar) {
    /* Only perform initialization when it is required */
    if (MemoryManagerConstants.GENERATE_GC_TRACE) {
      bootImage.setAddressWord(ref.plus(OBJECT_OID_OFFSET), oid, false, false);
      bootImage.setAddressWord(ref.plus(OBJECT_DEATH_OFFSET), time, false, false);
      bootImage.setAddressWord(ref.plus(OBJECT_LINK_OFFSET), prevAddress, false, false);
      prevAddress = ref.toWord();
      oid = oid.plus(Word.fromIntSignExtend((size - GC_TRACING_HEADER_BYTES) >> LOG_BYTES_IN_ADDRESS));
    }
  }

  public static void updateDeathTime(Object object) {
    if (VM.VerifyAssertions) VM._assert(MemoryManagerConstants.GENERATE_GC_TRACE);
    if (MemoryManagerConstants.GENERATE_GC_TRACE) {
      Magic.objectAsAddress(object).store(time, OBJECT_DEATH_OFFSET);
    }
  }

  public static void setDeathTime(Object object, Word time_) {
    if (VM.VerifyAssertions) VM._assert(MemoryManagerConstants.GENERATE_GC_TRACE);
    if (MemoryManagerConstants.GENERATE_GC_TRACE) {
      Magic.objectAsAddress(object).store(time_, OBJECT_DEATH_OFFSET);
    }
  }

  public static void setLink(Object object, ObjectReference link) {
    if (VM.VerifyAssertions) VM._assert(MemoryManagerConstants.GENERATE_GC_TRACE);
    if (MemoryManagerConstants.GENERATE_GC_TRACE) {
      Magic.objectAsAddress(object).store(link, OBJECT_LINK_OFFSET);
    }
  }

  public static void updateTime(Word time_) {
    if (VM.VerifyAssertions) VM._assert(MemoryManagerConstants.GENERATE_GC_TRACE);
    time = time_;
  }

  public static Word getOID(Object object) {
    if (VM.VerifyAssertions) VM._assert(MemoryManagerConstants.GENERATE_GC_TRACE);
    if (MemoryManagerConstants.GENERATE_GC_TRACE) {
      return Magic.objectAsAddress(object).plus(OBJECT_OID_OFFSET).loadWord();
    } else {
      return Word.zero();
    }
  }

  public static Word getDeathTime(Object object) {
    if (VM.VerifyAssertions) VM._assert(MemoryManagerConstants.GENERATE_GC_TRACE);
    if (MemoryManagerConstants.GENERATE_GC_TRACE) {
      return Magic.objectAsAddress(object).plus(OBJECT_DEATH_OFFSET).loadWord();
    } else {
      return Word.zero();
    }
  }

  public static ObjectReference getLink(Object ref) {
    if (VM.VerifyAssertions) VM._assert(MemoryManagerConstants.GENERATE_GC_TRACE);
    if (MemoryManagerConstants.GENERATE_GC_TRACE) {
      return ObjectReference.fromObject(Magic.getObjectAtOffset(ref, OBJECT_LINK_OFFSET));
    } else {
      return ObjectReference.nullReference();
    }
  }

  public static Address getBootImageLink() {
    if (VM.VerifyAssertions) VM._assert(MemoryManagerConstants.GENERATE_GC_TRACE);
    if (MemoryManagerConstants.GENERATE_GC_TRACE) {
      return prevAddress.toAddress();
    } else {
      return Address.zero();
    }
  }

  public static Word getOID() {
    if (VM.VerifyAssertions) VM._assert(MemoryManagerConstants.GENERATE_GC_TRACE);
    if (MemoryManagerConstants.GENERATE_GC_TRACE) {
      return oid;
    } else {
      return Word.zero();
    }
  }

  public static void setOID(Word oid_) {
    if (VM.VerifyAssertions) VM._assert(MemoryManagerConstants.GENERATE_GC_TRACE);
    if (MemoryManagerConstants.GENERATE_GC_TRACE) {
      oid = oid_;
    }
  }

  public static int getHeaderSize() {
    return NUM_BYTES_HEADER;
  }

  /**
   * For low level debugging of GC subsystem.
   * Dump the header word(s) of the given object reference.
   * @param ref the object reference whose header should be dumped
   */
  public static void dumpHeader(Object ref) {
    // by default nothing to do, unless the misc header is required
    if (MemoryManagerConstants.GENERATE_GC_TRACE) {
      VM.sysWrite(" OID=", getOID(ref));
      VM.sysWrite(" LINK=", getLink(ref));
      VM.sysWrite(" DEATH=", getDeathTime(ref));
    }
  }
}
