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

import static org.jikesrvm.objectmodel.JavaHeaderConstants.GC_HEADER_OFFSET;

import org.jikesrvm.classloader.Atom;
import org.jikesrvm.classloader.RVMArray;
import org.jikesrvm.classloader.RVMClass;
import org.jikesrvm.classloader.RVMType;
import org.jikesrvm.mm.mminterface.DebugUtil;
import org.jikesrvm.mm.mminterface.MemoryManager;
import org.jikesrvm.objectmodel.JavaHeader;
import org.jikesrvm.objectmodel.JavaHeaderConstants;
import org.jikesrvm.objectmodel.MiscHeader;
import org.jikesrvm.objectmodel.TIB;
import org.jikesrvm.runtime.Magic;
import org.jikesrvm.scheduler.GlobaliserThread;
import org.mmtk.plan.CollectorContext;
import org.mmtk.plan.Plan;
import org.mmtk.utility.alloc.Allocator;
import org.mmtk.utility.heap.Map;
import org.mmtk.vm.VM;
import org.vmmagic.pragma.Inline;
import org.vmmagic.pragma.NoInstrument;
import org.vmmagic.pragma.Uninterruptible;
import org.vmmagic.unboxed.Address;
import org.vmmagic.unboxed.ObjectReference;
import org.vmmagic.unboxed.Offset;
import org.vmmagic.unboxed.Word;

@Uninterruptible public final class ObjectModel extends org.mmtk.vm.ObjectModel {

  @Override
  protected Offset getArrayBaseOffset() { return JavaHeaderConstants.ARRAY_BASE_OFFSET; }

  @Override
  @Inline
  public ObjectReference copy(ObjectReference from, int allocator) {
    TIB tib = org.jikesrvm.objectmodel.ObjectModel.getTIB(from);
    RVMType type = Magic.objectAsType(tib.getType());

    if (type.isClassType())
      return copyScalar(from, tib, type.asClass(), allocator);
    else
      return copyArray(from, tib, type.asArray(), allocator);
  }

  @Inline
  private ObjectReference copyScalar(ObjectReference from, TIB tib, RVMClass type, int allocator) {
    int bytes = org.jikesrvm.objectmodel.ObjectModel.bytesRequiredWhenCopied(from.toObject(), type);
    int align = org.jikesrvm.objectmodel.ObjectModel.getAlignment(type, from.toObject());
    int offset = org.jikesrvm.objectmodel.ObjectModel.getOffsetForAlignment(type, from);
    CollectorContext context = VM.activePlan.collector();
    allocator = context.copyCheckAllocator(from, bytes, align, allocator);
    Address region = MemoryManager.allocateSpace(context, bytes, align, offset,
                                                allocator, from);
    Object toObj = org.jikesrvm.objectmodel.ObjectModel.moveObject(region, from.toObject(), bytes, type);
    ObjectReference to = ObjectReference.fromObject(toObj);
    context.postCopy(to, ObjectReference.fromObject(tib), bytes, allocator);
    return to;
  }

  @Inline
  private ObjectReference copyArray(ObjectReference from, TIB tib, RVMArray type, int allocator) {
    int elements = Magic.getArrayLength(from.toObject());
    int bytes = org.jikesrvm.objectmodel.ObjectModel.bytesRequiredWhenCopied(from.toObject(), type, elements);
    int align = org.jikesrvm.objectmodel.ObjectModel.getAlignment(type, from.toObject());
    int offset = org.jikesrvm.objectmodel.ObjectModel.getOffsetForAlignment(type, from);
    CollectorContext context = VM.activePlan.collector();
    allocator = context.copyCheckAllocator(from, bytes, align, allocator);
    Address region = MemoryManager.allocateSpace(context, bytes, align, offset,
                                                allocator, from);
    Object toObj = org.jikesrvm.objectmodel.ObjectModel.moveObject(region, from.toObject(), bytes, type);
    ObjectReference to = ObjectReference.fromObject(toObj);
    context.postCopy(to, ObjectReference.fromObject(tib), bytes, allocator);
    if (type == RVMType.CodeArrayType) {
      // sync all moved code arrays to get icache and dcache in sync
      // immediately.
      int dataSize = bytes - org.jikesrvm.objectmodel.ObjectModel.computeHeaderSize(Magic.getObjectType(toObj));
      org.jikesrvm.runtime.Memory.sync(to.toAddress(), dataSize);
    }
    return to;
  }

  /**
   * Return the size of a given object, in bytes
   *
   * @param object The object whose size is being queried
   * @return The size (in bytes) of the given object.
   */
  static int getObjectSize(ObjectReference object) {
    TIB tib = org.jikesrvm.objectmodel.ObjectModel.getTIB(object);
    RVMType type = Magic.objectAsType(tib.getType());

    if (type.isClassType())
      return org.jikesrvm.objectmodel.ObjectModel.bytesRequiredWhenCopied(object.toObject(), type.asClass());
    else
      return org.jikesrvm.objectmodel.ObjectModel.bytesRequiredWhenCopied(object.toObject(), type.asArray(), Magic.getArrayLength(object.toObject()));
  }

  /**
   * @param region The start (or an address less than) the region that was reserved for this object.
   */
  @Override
  @Inline
  public Address copyTo(ObjectReference from, ObjectReference to, Address region) {
    TIB tib = org.jikesrvm.objectmodel.ObjectModel.getTIB(from);
    RVMType type = tib.getType();
    int bytes;

    boolean copy = (from != to);

    if (copy) {
      if (type.isClassType()) {
        RVMClass classType = type.asClass();
        bytes = org.jikesrvm.objectmodel.ObjectModel.bytesRequiredWhenCopied(from.toObject(), classType);
        org.jikesrvm.objectmodel.ObjectModel.moveObject(from.toObject(), to.toObject(), bytes, classType);
      } else {
      RVMArray arrayType = type.asArray();
        int elements = Magic.getArrayLength(from.toObject());
        bytes = org.jikesrvm.objectmodel.ObjectModel.bytesRequiredWhenCopied(from.toObject(), arrayType, elements);
        org.jikesrvm.objectmodel.ObjectModel.moveObject(from.toObject(), to.toObject(), bytes, arrayType);
      }
    } else {
      bytes = getCurrentSize(to);
    }

    Address start = org.jikesrvm.objectmodel.ObjectModel.objectStartRef(to);
    Allocator.fillAlignmentGap(region, start);

    return start.plus(bytes);
  }

  @Override
  public ObjectReference getReferenceWhenCopiedTo(ObjectReference from, Address to) {
    return ObjectReference.fromObject(org.jikesrvm.objectmodel.ObjectModel.getReferenceWhenCopiedTo(from.toObject(), to));
  }

  @Override
  public Address getObjectEndAddress(ObjectReference object) {
    return org.jikesrvm.objectmodel.ObjectModel.getObjectEndAddress(object.toObject());
  }

  @Override
  public int getSizeWhenCopied(ObjectReference object) {
    return org.jikesrvm.objectmodel.ObjectModel.bytesRequiredWhenCopied(object.toObject());
  }

  @Override
  public int getAlignWhenCopied(ObjectReference object) {
    TIB tib = org.jikesrvm.objectmodel.ObjectModel.getTIB(object);
    RVMType type = tib.getType();
    if (type.isArrayType()) {
      return org.jikesrvm.objectmodel.ObjectModel.getAlignment(type.asArray(), object.toObject());
    } else {
      return org.jikesrvm.objectmodel.ObjectModel.getAlignment(type.asClass(), object.toObject());
    }
  }

  @Override
  public int getAlignOffsetWhenCopied(ObjectReference object) {
    TIB tib = org.jikesrvm.objectmodel.ObjectModel.getTIB(object);
    RVMType type = tib.getType();
    if (type.isArrayType()) {
      return org.jikesrvm.objectmodel.ObjectModel.getOffsetForAlignment(type.asArray(), object);
    } else {
      return org.jikesrvm.objectmodel.ObjectModel.getOffsetForAlignment(type.asClass(), object);
    }
  }

  @Override
  public int getCurrentSize(ObjectReference object) {
    return org.jikesrvm.objectmodel.ObjectModel.bytesUsed(object.toObject());
  }

  @Override
  public ObjectReference getNextObject(ObjectReference object) {
    return org.jikesrvm.objectmodel.ObjectModel.getNextObject(object);
  }

  @Override
  public ObjectReference getObjectFromStartAddress(Address start) {
    return org.jikesrvm.objectmodel.ObjectModel.getObjectFromStartAddress(start);
  }

  @Override
  public byte [] getTypeDescriptor(ObjectReference ref) {
    Atom descriptor = Magic.getObjectType(ref).getDescriptor();
    return descriptor.toByteArray();
  }

  @Override
  @Inline
  public int getArrayLength(ObjectReference object) {
    return Magic.getArrayLength(object.toObject());
  }

  @Override
  public boolean isArray(ObjectReference object) {
    return org.jikesrvm.objectmodel.ObjectModel.getObjectType(object.toObject()).isArrayType();
  }

  @Override
  public boolean isPrimitiveArray(ObjectReference object) {
    Object obj = object.toObject();
    return (obj instanceof long[]   ||
            obj instanceof int[]    ||
            obj instanceof short[]  ||
            obj instanceof byte[]   ||
            obj instanceof double[] ||
            obj instanceof float[]);
  }

  /**
   * Tests a bit available for memory manager use in an object.
   *
   * @param object the address of the object
   * @param idx the index of the bit
   * @return {@code true} if argument bit is 1, {@code false} if it is 0
   */
  public boolean testAvailableBit(ObjectReference object, int idx) {
    return org.jikesrvm.objectmodel.ObjectModel.testAvailableBit(object.toObject(), idx);
  }

  /**
   * Sets a bit available for memory manager use in an object.
   *
   * @param object the address of the object
   * @param idx the index of the bit
   * @param flag <code>true</code> to set the bit to 1,
   * <code>false</code> to set it to 0
   */
  public void setAvailableBit(ObjectReference object, int idx,
                                     boolean flag) {
    org.jikesrvm.objectmodel.ObjectModel.setAvailableBit(object.toObject(), idx, flag);
  }

  @Override
  public boolean attemptAvailableBits(ObjectReference object,
                                             Word oldVal, Word newVal) {
    return org.jikesrvm.objectmodel.ObjectModel.attemptAvailableBits(object.toObject(), oldVal,
                                               newVal);
  }

  @Override
  public Word prepareAvailableBits(ObjectReference object) {
    return org.jikesrvm.objectmodel.ObjectModel.prepareAvailableBits(object.toObject());
  }

  @Override
  public void writeAvailableByte(ObjectReference object, byte val) {
    org.jikesrvm.objectmodel.ObjectModel.writeAvailableByte(object.toObject(), val);
  }

  @Override
  public byte readAvailableByte(ObjectReference object) {
    return org.jikesrvm.objectmodel.ObjectModel.readAvailableByte(object.toObject());
  }

  @Override
  public void writeAvailableBitsWord(ObjectReference object, Word val) {
    org.jikesrvm.objectmodel.ObjectModel.writeAvailableBitsWord(object.toObject(), val);
  }

  @Override
  public Word readAvailableBitsWord(ObjectReference object) {
    return org.jikesrvm.objectmodel.ObjectModel.readAvailableBitsWord(object.toObject());
  }

  /* AJG: Should this be a variable rather than method? */
  @Override
  public Offset GC_HEADER_OFFSET() {
    return GC_HEADER_OFFSET;
  }

  @Override
  @Inline
  public Address objectStartRef(ObjectReference object) {
    return org.jikesrvm.objectmodel.ObjectModel.objectStartRef(object);
  }

  @Override
  public Address refToAddress(ObjectReference object) {
    return org.jikesrvm.objectmodel.ObjectModel.getPointerInMemoryRegion(object);
  }

  @Override
  @Inline
  public boolean isAcyclic(ObjectReference typeRef) {
    TIB tib = Magic.addressAsTIB(typeRef.toAddress());
    RVMType type = tib.getType();
    return type.isAcyclicReference();
  }

  @Override
  public void dumpObject(ObjectReference object) {
    DebugUtil.dumpRef(object);
  }
  
  public void setGlobaliseBit(ObjectReference o) {
    MiscHeader.setGlobaliseBit(o);
  }
  
  public void setAllocatingThreadAndColour(ObjectReference o, int threadID, int colour) {
    MiscHeader.setAllocatingThreadAndColour(o, threadID, colour);
  }
  
  public void setBootThreadAndColour(ObjectReference o, int threadID, int colour) {
    MiscHeader.setBootThreadAndColour(o, threadID, colour);
  }
  
  public void setColour(ObjectReference o, int colour) {
    MiscHeader.setColour(o, colour);
  }
  
  public int getAllocatingThread(ObjectReference o) {
    int threadID = MiscHeader.getAllocatingThread(o);
    if (threadID == 0) {
      return 1;
    }
    else {
      return threadID;
    }
  }
  
  @NoInstrument
  public int getOriginalAllocatingThread(ObjectReference o) {
    int threadID = MiscHeader.getAllocatingThread(o);
    if (threadID == 0 || threadID == 1024) {
      threadID = 1;
    }
    
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(threadID > 0 && threadID < 2048);
    
    if (threadID > 1024) {
      return threadID - 1024;
    }
    else {
      return threadID;
    }
  }
    
  @Inline
  public int getColour(ObjectReference o) {
    return MiscHeader.getColour(o);
  }
  
  /**
   * This should probably be moved.
   * Given an object, return an array of offsets of outgoing references.
   * 
   * Returns an array if it is an object or null if it is in an array with no outgoing references.
   */
  @NoInstrument
  public int[] getChildren(ObjectReference o) {
    RVMType type = Magic.getObjectType(o);
    
    int[] offsets = type.getReferenceOffsetsNI();
    if (offsets != RVMType.REFARRAY_OFFSET_ARRAY) {
      return offsets;
    }
    else { //array
      return null;
    }
  }
  
  public int getType(ObjectReference o) {
    return Magic.getObjectType(o).getIdNI();
  }
  
  public void setAllocID(ObjectReference o, int threadAllocN) {
    MiscHeader.setAllocID(o, threadAllocN);
  }
  
  public int getAllocID(ObjectReference o) {
    return MiscHeader.getAllocID(o);
  }
  
  public void dumpAllHeaders(ObjectReference o) {
    MiscHeader.dumpAllHeaders(o);
  }
  
  @NoInstrument
  public void requestGlobalise(ObjectReference o) {
    if (Plan.GLOBALISE_ON) {
      GlobaliserThread.requestGlobalise(o);
    }
  }
  
  public int getSpaceForObject(ObjectReference o) {
    return Map.getSpaceForAddressNI(o.toAddress()).getIndexNI();
  }
  
  public int getTypeID(ObjectReference o) {
    return Magic.getObjectType(o).getIdNI();
  }
  
  @NoInstrument
  public boolean isTIBInvalid(ObjectReference o) {
    return (Magic.getIntAtOffset(o, JavaHeader.getTibOffset()) == 0) || (!MemoryManager.mightBeTIB(o));
  }
  
  @Override
  public Address objectToAddress(ObjectReference object) {
    return Magic.objectAsAddress(object);
  }
  
  @NoInstrument
  @Inline
  public boolean isMine(ObjectReference o) {
    return MiscHeader.isMine(o);
  }
  
  public static final Atom trackedType1 = Atom.findOrCreateUnicodeAtom("Lorg/jikesrvm/classloader/Atom;");
  public static final Atom trackedType2 = Atom.findOrCreateUnicodeAtom("Ljava/lang/ref/Reference;");
  public static final Atom trackedType3 = Atom.findOrCreateUnicodeAtom("Ljava/util/WeakHashMap;");
  public static final Atom trackedType4 = Atom.findOrCreateUnicodeAtom("Lorg/jikesrvm/classloader/MethodVector;");
  
  @NoInstrument
  public boolean isType(ObjectReference o) {
    return VM.activePlan.fullyBooted() && !o.isNull() && (Magic.getObjectType(o).getDescriptorNI() == trackedType1); // || Magic.getObjectType(o).getDescriptorNI() == trackedType2 || Magic.getObjectType(o).getDescriptorNI() == trackedType3 || Magic.getObjectType(o).getDescriptorNI() == trackedType4);
  }
  
  @NoInstrument
  public boolean isRVMType(ObjectReference o) {
    if (o.isNull()) return false;
    return Magic.getObjectType(o).isRVMType();
  }
}

