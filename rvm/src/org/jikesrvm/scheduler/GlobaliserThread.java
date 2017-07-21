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
package org.jikesrvm.scheduler;

import org.jikesrvm.runtime.Entrypoints;
import org.jikesrvm.runtime.Magic;
import org.jikesrvm.runtime.Time;
import org.mmtk.plan.Plan;
import org.mmtk.utility.statistics.Timer;
import org.mmtk.vm.VM;
import org.vmmagic.pragma.Entrypoint;
import org.vmmagic.pragma.NoInline;
import org.vmmagic.pragma.NoInstrument;
import org.vmmagic.pragma.Uninterruptible;
import org.vmmagic.pragma.NonMoving;
import org.vmmagic.unboxed.Address;
import org.vmmagic.unboxed.ObjectReference;
import org.vmmagic.unboxed.Offset;

@NonMoving
public class GlobaliserThread extends SystemThread {

  public static final boolean DEBUGGING = false;
  
  //The monotonic ID of this thread. Set on execution.
  public static int threadID = -1;
  
  //Really need to put an assertion to protect overflows.
  public static final int MAX_SUPPORTED_REQUESTING_THREADS = 100; //To guarantee safety: RVMThread.MAX_THREADS;
  
  //Valid thread states w.r.t the Globaliser Thread
  //Thread is in TIGC
  public static final int IN_TIGC = 4;
  //The thread has handed over control of its globaliser queue and processing to the globaliser thread
  //  because it is unable to globalise objects itself (it is blocked, dead, etc)
  public static final int GLOBALISER_CONTROL = 2;
  //The Globaliser Thread is actively working on this thread's queue. It must be allowed to finish before
  //  state changes occur.
  public static final int GLOBALISER_CONTROL_AND_ACTIVE = 3;
  //A thread is in control of its objects, handling globalise requests periodically.
  public static final int ALLOCATING_THREAD_CONTROL = 0;
  public static final int ALLOCATING_THREAD_CONTROL_AND_ACTIVE = 1;
  
//Every thread keeps an array of threads that are waiting for this thread to globalise an object.
  //Every thread can only be waiting for one thread in this way. If it is waiting, it will set the field
  //  'objectRequiringGlobalise' to the address of the object it is waiting for.
  //Although this is stored as an array, it actually works like a cyclical queue, with a work pointer
  //  and a queue pointer to represent the head and tail.
  //To guarantee correctness the size must be set to RVMThread.MAX_THREADS.
  //To optimise the size of the array at the cost of having an assertion failure, you can reduce this.
  @Entrypoint
  public static int[][] globaliseRequests = Plan.GLOBALISE_ON ? new int[RVMThread.MAX_THREADS][MAX_SUPPORTED_REQUESTING_THREADS] : null;
  //The head of the above cyclical buffer. Items to be processed are taken from the workPointer end
  @Entrypoint
  public static int[] workPointer = Plan.GLOBALISE_ON ? new int[RVMThread.MAX_THREADS] : null;
  //The tail of the above cyclical buffer. Items to be added at added at this end.
  @Entrypoint
  public static int[] queuePointer = Plan.GLOBALISE_ON ? new int[RVMThread.MAX_THREADS] : null;
  //Locks to prevent concurrent editing of the above pointers.
  //@Entrypoint
  //public int workPointerLock = 0;
  @Entrypoint
  public static int[] queuePointerLock = Plan.GLOBALISE_ON ? new int[RVMThread.MAX_THREADS] : null;
  @Entrypoint
  public static int threadStatus[] = new int[RVMThread.MAX_THREADS];
  
  /**
   * Globaliser Thread entrypoint:
   */
  public static void boot() {
    //schedLock=new Monitor();
    if (Plan.GLOBALISE_ON) {
      GlobaliserThread gt = new GlobaliserThread();
      gt.start();
    }
  }
  
  public GlobaliserThread() {
    super("GlobaliserThread");
  }
  
  @Override
  @NoInstrument
  public void run() {
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(Plan.GLOBALISE_ON);
    GlobaliserThread.threadID = VM.activePlan.getThreadID();
    while (true) {
      boolean globalisedThisLoop = false;
      for (int i = 1; i < RVMThread.nextMonotonicID; i++) {
        
        //if (RVMThread.threadMonotonically[i] == null) {
        //  continue;
        //}
        
        globalisedThisLoop = globalisedThisLoop || GlobaliserThread.processThread(i, false);
      }
      
      if (!globalisedThisLoop) {
        RVMThread.yieldNoHandshake();
      }
    }
  }
  
  /**
   * Globalising helper methods
   */
  
  @Uninterruptible
  @NoInstrument
  public static void gainQueuePointerLock(int threadID) {
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(Plan.GLOBALISE_ON);
    while (true) {
      int m = Magic.prepareInt(Magic.getJTOC().plus(Entrypoints.queueLockFieldGT.getOffset()).loadAddress(), (Offset.fromIntZeroExtend(threadID * 4)));
      if (Magic.attemptInt(Magic.getJTOC().plus(Entrypoints.queueLockFieldGT.getOffset()).loadAddress(), (Offset.fromIntZeroExtend(threadID * 4)), 0, 1)) {
        break;
      }
    }
  }
  
  @Uninterruptible
  @NoInstrument
  public static void releaseQueuePointerLock(int threadID) {
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(Plan.GLOBALISE_ON);
    Magic.setIntAtOffset(Magic.getJTOC().plus(Entrypoints.queueLockFieldGT.getOffset()).loadAddress(), (Offset.fromIntZeroExtend(threadID * 4)), 0);
  }
  
  @Uninterruptible
  @NoInstrument
  public static int getQueuePointer(int threadID) {
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(Plan.GLOBALISE_ON);
    int queuePtr = Magic.getIntAtOffset(Magic.getJTOC().plus(Entrypoints.queuePointerFieldGT.getOffset()).loadAddress(), (Offset.fromIntZeroExtend(threadID * 4)));
    return queuePtr;
  }
  
  @Uninterruptible
  @NoInstrument
  public static void incrementQueuePointer(int threadID, int expectedOldValue) {
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(Plan.GLOBALISE_ON);
    int oldValue = GlobaliserThread.getQueuePointer(threadID);
    if (VM.VERIFY_ASSERTIONS) {
      VM.assertions._assert(oldValue == expectedOldValue);
      VM.assertions._assert(getWorkPointer(threadID) != (oldValue + 1) % MAX_SUPPORTED_REQUESTING_THREADS);
    }
    Magic.setIntAtOffset(Magic.getJTOC().plus(Entrypoints.queuePointerFieldGT.getOffset()).loadAddress(), (Offset.fromIntZeroExtend(threadID * 4)), (oldValue + 1) % MAX_SUPPORTED_REQUESTING_THREADS);
  }

  @Uninterruptible
  @NoInstrument
  public static int getWorkPointer(int threadID) {
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(Plan.GLOBALISE_ON);
    int workPtr = Magic.getIntAtOffset(Magic.getJTOC().plus(Entrypoints.workPointerFieldGT.getOffset()).loadAddress(), (Offset.fromIntZeroExtend(threadID * 4)));
    return workPtr;
  }

  @Uninterruptible
  @NoInstrument
  public static void incrementWorkPointer(int threadID, int expectedOldValue) {
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(Plan.GLOBALISE_ON);
    int oldValue = GlobaliserThread.getWorkPointer(threadID);
    if (VM.VERIFY_ASSERTIONS) {
      VM.assertions._assert(oldValue == expectedOldValue);
    }
    Magic.setIntAtOffset(Magic.getJTOC().plus(Entrypoints.workPointerFieldGT.getOffset()).loadAddress(), (Offset.fromIntZeroExtend(threadID * 4)), (oldValue + 1) % MAX_SUPPORTED_REQUESTING_THREADS);
  }
  
  @Uninterruptible
  @NoInstrument
  public static Address getThreadQueueArray(int threadID) {
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(Plan.GLOBALISE_ON);
    Address threadQueue = (Magic.getAddressAtOffset(Magic.getJTOC().plus(Entrypoints.globaliseRequestsFieldGT.getOffset()).loadAddress(), (Offset.fromIntZeroExtend(threadID * 4))));
    return threadQueue;
  }
  
  @Uninterruptible
  @NoInstrument
  public static int getThreadStatus(int threadID) {
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(Plan.GLOBALISE_ON);
    return Magic.getIntAtOffset(Magic.getJTOC().plus(Entrypoints.statusFlagFieldGT.getOffset()).loadAddress(), (Offset.fromIntZeroExtend(threadID * 4)));
  }
  
  
  /**
   * Attempts once to CAS a thread's status from oldStatus to newStatus.
   * @return true on success.
   */
  @Uninterruptible
  @NoInstrument
  public static boolean attemptThreadStatus(int threadID, int oldStatus, int newStatus) {
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(Plan.GLOBALISE_ON);
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(newStatus != oldStatus);
    int prev = Magic.prepareInt(Magic.getJTOC().plus(Entrypoints.statusFlagFieldGT.getOffset()).loadAddress(), (Offset.fromIntZeroExtend(threadID * 4)));
    if (Magic.attemptInt(Magic.getJTOC().plus(Entrypoints.statusFlagFieldGT.getOffset()).loadAddress(), (Offset.fromIntZeroExtend(threadID * 4)), oldStatus, newStatus)) {
      return true;
    }
    else {
      return false;
    }
  }
  
  /**
   * Loops until a CAS thread's status from oldStatus to newStatus succeeds.
   * However, if a thread is IN_TIGC, we break out of loop
   */
  @Uninterruptible
  @NoInstrument
  public static void setThreadStatus(int threadID, int oldStatus, int newStatus) {
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(Plan.GLOBALISE_ON);
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(newStatus != oldStatus);
    while(true) {
      int prev = Magic.prepareInt(Magic.getJTOC().plus(Entrypoints.statusFlagFieldGT.getOffset()).loadAddress(), (Offset.fromIntZeroExtend(threadID * 4)));
      if (prev == IN_TIGC) {
        Magic.attemptInt(Magic.getJTOC().plus(Entrypoints.statusFlagFieldGT.getOffset()).loadAddress(), (Offset.fromIntZeroExtend(threadID * 4)), 0, 0);
        if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(oldStatus != IN_TIGC);
        break;
      }
      else if (Magic.attemptInt(Magic.getJTOC().plus(Entrypoints.statusFlagFieldGT.getOffset()).loadAddress(), (Offset.fromIntZeroExtend(threadID * 4)), oldStatus, newStatus)) {
        break;
      }
    }
  }
  
  @Uninterruptible
  @NoInstrument
  public static void setWaiting(int threadID) {
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(Plan.GLOBALISE_ON);
    ObjectReference thread = ObjectReference.fromObject(RVMThread.threadMonotonically[threadID]);
    if (thread.isNull()) {
      VM.assertions.logStr("[Warning]: setWaiting thread is null ");
      VM.assertions.logIntln(threadID);
      return;
    }
    while (true) {
      int oldStatus = Magic.prepareInt(thread, Entrypoints.waitingField.getOffset());
      if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(oldStatus == 0);
      if (Magic.attemptInt(thread, Entrypoints.waitingField.getOffset(), oldStatus, 1)) {
        break;
      }
    }
  }

  @NoInstrument
  @Uninterruptible
  public static void clearWaiting(int threadID) {
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(Plan.GLOBALISE_ON);
    ObjectReference thread = ObjectReference.fromObject(RVMThread.threadMonotonically[threadID]);
    if (thread.isNull()) {
      VM.assertions.logStr("[Warning]: clearWaiting thread is null ");
      VM.assertions.logIntln(threadID);
      return;
    }
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(Magic.getIntAtOffset(thread, Entrypoints.waitingField.getOffset()) == 1);
    Magic.setIntAtOffset(thread, Entrypoints.waitingField.getOffset(), 0);
  }
  
  @NoInstrument
  @Uninterruptible
  public static boolean isWaiting(int threadID) {
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(Plan.GLOBALISE_ON);
    ObjectReference thread = ObjectReference.fromObject(RVMThread.threadMonotonically[threadID]);
    if (thread.isNull()) {
      VM.assertions.logStr("[Warning]: isWaiting thread is null ");
      VM.assertions.logIntln(threadID);
      return false;
    }
    return Magic.getIntAtOffset(thread, Entrypoints.waitingField.getOffset()) == 1;
  }
  
  @Uninterruptible
  @NoInstrument
  public static void prepareForTIGC(int threadID) {
    if (VM.VERIFY_ASSERTIONS) {
      VM.assertions._assert(Plan.GLOBALISE_ON);
      VM.assertions._assert(getThreadStatus(threadID) == ALLOCATING_THREAD_CONTROL);
    }
    GlobaliserThread.setThreadStatus(threadID, ALLOCATING_THREAD_CONTROL, IN_TIGC);
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(getThreadStatus(threadID) == IN_TIGC);
  }

  @Uninterruptible
  @NoInstrument
  public static void exitTIGC(int threadID) {
    if (VM.VERIFY_ASSERTIONS) {
      VM.assertions._assert(Plan.GLOBALISE_ON);
      VM.assertions._assert(getThreadStatus(threadID) == IN_TIGC);
    }
    if (!GlobaliserThread.attemptThreadStatus(threadID, IN_TIGC, ALLOCATING_THREAD_CONTROL)) {
      VM.assertions.fail("On exiting TIGC, thread status was not IN_TIGC");
    }
  }

  /*
   * Atomically append an item in allocatingThread.globaliseRequests
   */
  @Uninterruptible
  @NoInstrument
  public static void queueGlobaliseRequest(int allocatingThreadID, ObjectReference value) {
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(Plan.GLOBALISE_ON);
    ObjectReference requestingThread = ObjectReference.fromObject(RVMThread.threadMonotonically[VM.activePlan.getThreadID()]);
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(!requestingThread.isNull());

    //Gain exclusivity over allocatingThreadID's queue pointer
    GlobaliserThread.gainQueuePointerLock(allocatingThreadID);
    
    Magic.setAddressAtOffset(requestingThread, Entrypoints.objectRequiringGlobaliseField.getOffset(), value.toAddress());
    
    int queuePtr = GlobaliserThread.getQueuePointer(allocatingThreadID);
    Address threadQueue = GlobaliserThread.getThreadQueueArray(allocatingThreadID);
  
    //Add the the requestingThread into the allocatingThread's threadQueue.
    if (VM.VERIFY_ASSERTIONS) {
      VM.assertions._assert(Magic.getIntAtOffset(threadQueue, Offset.fromIntZeroExtend(queuePtr * 4)) == 0);
    }
    Magic.setIntAtOffset(threadQueue, Offset.fromIntZeroExtend(queuePtr * 4), requestingThread.toAddress().toInt());
    
    //Now that the allocatingThread's globaliseRequest's entry has been appended,
    //  increment the queuePtr so it becomes visible to allocatingThread/globaliserThread
    GlobaliserThread.incrementQueuePointer(allocatingThreadID, queuePtr);
    
    //Unlock. Allow others to use allocatingThreadID's queue pointer
    GlobaliserThread.releaseQueuePointerLock(allocatingThreadID);
  }
  
  @Uninterruptible
  @NoInstrument
  public static boolean processThread(int allocatingThreadID, boolean asAllocatingThread) {
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(Plan.GLOBALISE_ON);
    
    if (asAllocatingThread) {
      if (!GlobaliserThread.attemptThreadStatus(allocatingThreadID, ALLOCATING_THREAD_CONTROL, ALLOCATING_THREAD_CONTROL_AND_ACTIVE)) {
        return false;
      }
    }
    else {
      if (!GlobaliserThread.attemptThreadStatus(allocatingThreadID, GLOBALISER_CONTROL, GLOBALISER_CONTROL_AND_ACTIVE)) {
        return false;
      }
    }

    int threadStatus = getThreadStatus(allocatingThreadID);
    if (asAllocatingThread) {
      if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(threadStatus == ALLOCATING_THREAD_CONTROL_AND_ACTIVE);
    }
    else {
      if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(threadStatus == GLOBALISER_CONTROL_AND_ACTIVE);
    }
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(threadStatus != IN_TIGC);
    
    //By setting the thread status above, the work pointer is exclusive. The queue pointer is not, as it can be modified elsewhere.
    int workPtr = GlobaliserThread.getWorkPointer(allocatingThreadID);
    int queuePtr = GlobaliserThread.getQueuePointer(allocatingThreadID);
    
    boolean worked = !(workPtr == queuePtr);

    Address threadsQueue = GlobaliserThread.getThreadQueueArray(allocatingThreadID);
    
    while (workPtr != queuePtr) {

      //Get the thread that enqueued itself on the queue.
      Address requestingThread = Magic.getAddressAtOffset(threadsQueue, Offset.fromIntZeroExtend(workPtr * 4));
      if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(!requestingThread.isZero());
      //Get the object that the requesting thread wants to be globalised.
      ObjectReference greyObject = Magic.getAddressAtOffset(requestingThread, Entrypoints.objectRequiringGlobaliseField.getOffset()).toObjectReference();
      
      switch(VM.objectModel.getColour(greyObject)) {
      case Plan.COLOURLESS: //error;
                            break;
      case Plan.BLACK:      break;
      case Plan.GREY:       RVMThread.getCurrentThread().globalise(greyObject);
                            break;
      case Plan.WHITE:      //error
                            break;
      }

      //Zero the queue entry.
      Magic.setIntAtOffset(threadsQueue, Offset.fromIntZeroExtend(workPtr * 4), 0);
      
      GlobaliserThread.incrementWorkPointer(allocatingThreadID, workPtr);
      workPtr = GlobaliserThread.getWorkPointer(allocatingThreadID);
      clearWaiting(Magic.getIntAtOffset(requestingThread, Entrypoints.monotonicThreadID.getOffset()));
    }
    

    if (asAllocatingThread) {
      if (!GlobaliserThread.attemptThreadStatus(allocatingThreadID, ALLOCATING_THREAD_CONTROL_AND_ACTIVE, ALLOCATING_THREAD_CONTROL)) {
        VM.assertions.fail("A.T. should have had exclusive use of a queue and we did not.");
      }
    }
    else {
      if (!GlobaliserThread.attemptThreadStatus(allocatingThreadID, GLOBALISER_CONTROL_AND_ACTIVE, GLOBALISER_CONTROL)) {
        VM.assertions.fail("G.T. should have had exclusive use of a queue and we did not.");
      }
    }
    return worked;
  }
  
  @NoInstrument
  @Uninterruptible
  public static void surrenderGlobaliserQueueControl() {
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(Plan.GLOBALISE_ON);
    int threadID = RVMThread.getCurrentThread().monotonicID;
    if (threadID == GlobaliserThread.threadID) {
      return;
    }
    
    GlobaliserThread.setThreadStatus(threadID, ALLOCATING_THREAD_CONTROL, GLOBALISER_CONTROL);
  }
  
  @NoInstrument
  @Uninterruptible
  public static void reacquireGlobaliserQueueControl() {
    if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(Plan.GLOBALISE_ON);
    int threadID = RVMThread.getCurrentThread().monotonicID;
    if (threadID == GlobaliserThread.threadID) { return; }

    GlobaliserThread.setThreadStatus(threadID, GLOBALISER_CONTROL, ALLOCATING_THREAD_CONTROL);
  }
  
  @NoInstrument
  @NoInline
  @Uninterruptible
  public static void requestGlobalise(ObjectReference greyObject) {
  	if (VM.VERIFY_ASSERTIONS) VM.assertions._assert(Plan.GLOBALISE_ON);
  	int thisThreadID = VM.activePlan.getThreadID();
  	int allocatingThreadID = VM.objectModel.getOriginalAllocatingThread(greyObject);
  	
  	//The globaliser thread itself must not count as a remote thread using an object.
  	//If the globaliser thread passes this point, infinite recursion will result.
  	if (thisThreadID == VM.activePlan.getGlobaliserThreadID()) {
  		return;
  	}
  	
  	if (DEBUGGING) {
	  	VM.assertions.logStr("R Thread ");
	  	VM.assertions.logInt(thisThreadID);
	  	VM.assertions.logStr(" is asking thread ");
	  	VM.assertions.logInt(allocatingThreadID);
	  	VM.assertions.logStr(" to globalise ");
	  	VM.assertions.logHexln(greyObject.toAddress());
  	}
  	
  	//VM.objectModel.dumpAllHeaders(greyObject);
  	//VM.assertions.dumpStackNI();
  	
  	//We set ourselves as waiting for an object to be globalised.
  	//This must be done before we set the globaliseRequest array, in case a thread sees the array populated before the waiting array is set and globalises the object and 'wakes' us up before we have had a chance to 'wait'
  	setWaiting(thisThreadID);
  	
  	//Notify the thread responsible for globalising the object that we wish to globalise.
  	queueGlobaliseRequest(allocatingThreadID, greyObject);
  	
  	if (DEBUGGING) {
	  	VM.assertions.logStr("R Thread ");
	  	VM.assertions.logInt(thisThreadID);
	  	VM.assertions.logStrln(" is simulated blocking");
  	}
  	
  	//Whilst we are waiting for the globalise request, we can only check to see if there are any items in our queue to globalise
  	int counter = 0;
  	while (isWaiting(thisThreadID)) {
  		//Whilst we are waiting for another thread to globalise an object, the only actions we can take are to process
  		//our own requests and block for GCs.
  	  processThread(thisThreadID, true);
			if (DEBUGGING) {
  		  counter++;
  		  if (counter % 10000000 == 0) {
	  			VM.assertions.logInt(thisThreadID);
	  			VM.assertions.logStrln(" waiting a while");
	  			//RVMThread.threadMonotonically[allocatingThreadID].requestDumpStack = true;
	  			VM.assertions.logStr("R Thread ");
	        VM.assertions.logInt(thisThreadID);
	        VM.assertions.logStr(" was asking thread ");
	        VM.assertions.logInt(allocatingThreadID);
	        VM.assertions.logStr(" to globalise ");
	        VM.assertions.logHexln(greyObject.toAddress());
  			}
			}
      //RVMThread.logStrln("/");
  	  VM.activePlan.yield();
  	}
  	
  	if (DEBUGGING) {
	  	VM.assertions.logStr("R Thread ");
	  	VM.assertions.logInt(thisThreadID);
	  	VM.assertions.logStrln(" has resumed");
	  	VM.assertions.logStr("R COLOUR: ");
	  	VM.assertions.logHex(greyObject.toAddress());
	  	VM.assertions.logStr(" ");
	  	VM.assertions.logIntln(VM.objectModel.getColour(greyObject));
  	}

  	if (VM.VERIFY_ASSERTIONS) {
  		VM.assertions._assert(VM.objectModel.getColour(greyObject) == Plan.BLACK);
  	}
  }

}
