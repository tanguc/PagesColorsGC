
/*
 * THIS FILE IS MACHINE_GENERATED. DO NOT EDIT.
 * See InstructionFormats.template, InstructionFormatList.dat,
 * OperatorList.dat, etc.
 */

package org.jikesrvm.compilers.opt.ir;

import org.jikesrvm.Configuration;
import org.jikesrvm.compilers.opt.ir.operand.ia32.IA32ConditionOperand; //NOPMD
import org.jikesrvm.compilers.opt.ir.operand.*;

/**
 * The MIR_LowTableSwitch InstructionFormat class.
 *
 * The header comment for {@link Instruction} contains
 * an explanation of the role of InstructionFormats in the
 * opt compiler's IR.
 */
@SuppressWarnings("unused")  // Machine generated code is never 100% clean
public final class MIR_LowTableSwitch extends InstructionFormat {
  /**
   * InstructionFormat identification method for MIR_LowTableSwitch.
   * @param i an instruction
   * @return <code>true</code> if the InstructionFormat of the argument
   *         instruction is MIR_LowTableSwitch or <code>false</code>
   *         if it is not.
   */
  public static boolean conforms(Instruction i) {
    return conforms(i.operator());
  }
  /**
   * InstructionFormat identification method for MIR_LowTableSwitch.
   * @param o an instruction
   * @return <code>true</code> if the InstructionFormat of the argument
   *         operator is MIR_LowTableSwitch or <code>false</code>
   *         if it is not.
   */
  public static boolean conforms(Operator o) {
    return o.format == MIR_LowTableSwitch_format;
  }

  /**
   * Get the operand called Index from the
   * argument instruction. Note that the returned operand
   * will still point to its containing instruction.
   * @param i the instruction to fetch the operand from
   * @return the operand called Index
   */
  public static RegisterOperand getIndex(Instruction i) {
    if (Configuration.ExtremeAssertions && !conforms(i)) fail(i, "MIR_LowTableSwitch");
    return (RegisterOperand) i.getOperand(0);
  }
  /**
   * Get the operand called Index from the argument
   * instruction clearing its instruction pointer. The returned
   * operand will not point to any containing instruction.
   * @param i the instruction to fetch the operand from
   * @return the operand called Index
   */
  public static RegisterOperand getClearIndex(Instruction i) {
    if (Configuration.ExtremeAssertions && !conforms(i)) fail(i, "MIR_LowTableSwitch");
    return (RegisterOperand) i.getClearOperand(0);
  }
  /**
   * Set the operand called Index in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param i the instruction in which to store the operand
   * @param Index the operand to store
   */
  public static void setIndex(Instruction i, RegisterOperand Index) {
    if (Configuration.ExtremeAssertions && !conforms(i)) fail(i, "MIR_LowTableSwitch");
    i.putOperand(0, Index);
  }
  /**
   * Return the index of the operand called Index
   * in the argument instruction.
   * @param i the instruction to access.
   * @return the index of the operand called Index
   *         in the argument instruction
   */
  public static int indexOfIndex(Instruction i) {
    if (Configuration.ExtremeAssertions && !conforms(i)) fail(i, "MIR_LowTableSwitch");
    return 0;
  }
  /**
   * Does the argument instruction have a non-null
   * operand named Index?
   * @param i the instruction to access.
   * @return <code>true</code> if the instruction has an non-null
   *         operand named Index or <code>false</code>
   *         if it does not.
   */
  public static boolean hasIndex(Instruction i) {
    if (Configuration.ExtremeAssertions && !conforms(i)) fail(i, "MIR_LowTableSwitch");
    return i.getOperand(0) != null;
  }

  /**
   * Get the operand called MethodStart from the
   * argument instruction. Note that the returned operand
   * will still point to its containing instruction.
   * @param i the instruction to fetch the operand from
   * @return the operand called MethodStart
   */
  public static RegisterOperand getMethodStart(Instruction i) {
    if (Configuration.ExtremeAssertions && !conforms(i)) fail(i, "MIR_LowTableSwitch");
    return (RegisterOperand) i.getOperand(1);
  }
  /**
   * Get the operand called MethodStart from the argument
   * instruction clearing its instruction pointer. The returned
   * operand will not point to any containing instruction.
   * @param i the instruction to fetch the operand from
   * @return the operand called MethodStart
   */
  public static RegisterOperand getClearMethodStart(Instruction i) {
    if (Configuration.ExtremeAssertions && !conforms(i)) fail(i, "MIR_LowTableSwitch");
    return (RegisterOperand) i.getClearOperand(1);
  }
  /**
   * Set the operand called MethodStart in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param i the instruction in which to store the operand
   * @param MethodStart the operand to store
   */
  public static void setMethodStart(Instruction i, RegisterOperand MethodStart) {
    if (Configuration.ExtremeAssertions && !conforms(i)) fail(i, "MIR_LowTableSwitch");
    i.putOperand(1, MethodStart);
  }
  /**
   * Return the index of the operand called MethodStart
   * in the argument instruction.
   * @param i the instruction to access.
   * @return the index of the operand called MethodStart
   *         in the argument instruction
   */
  public static int indexOfMethodStart(Instruction i) {
    if (Configuration.ExtremeAssertions && !conforms(i)) fail(i, "MIR_LowTableSwitch");
    return 1;
  }
  /**
   * Does the argument instruction have a non-null
   * operand named MethodStart?
   * @param i the instruction to access.
   * @return <code>true</code> if the instruction has an non-null
   *         operand named MethodStart or <code>false</code>
   *         if it does not.
   */
  public static boolean hasMethodStart(Instruction i) {
    if (Configuration.ExtremeAssertions && !conforms(i)) fail(i, "MIR_LowTableSwitch");
    return i.getOperand(1) != null;
  }

  /**
   * Get the k'th operand called Target from the
   * argument instruction. Note that the returned operand
   * will still point to its containing instruction.
   * @param i the instruction to fetch the operand from
   * @param k the index of the operand
   * @return the k'th operand called Target
   */
  public static BranchOperand getTarget(Instruction i, int k) {
    if (Configuration.ExtremeAssertions && !conforms(i)) fail(i, "MIR_LowTableSwitch");
    return (BranchOperand) i.getOperand(2+k*2+0);
  }
  /**
   * Get the k'th operand called Target from the argument
   * instruction clearing its instruction pointer. The returned
   * operand will not point to any containing instruction.
   * @param i the instruction to fetch the operand from
   * @param k the index of the operand
   * @return the k'th operand called Target
   */
  public static BranchOperand getClearTarget(Instruction i, int k) {
    if (Configuration.ExtremeAssertions && !conforms(i)) fail(i, "MIR_LowTableSwitch");
    return (BranchOperand) i.getClearOperand(2+k*2+0);
  }
  /**
   * Set the k'th operand called Target in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param i the instruction in which to store the operand
   * @param k the index of the operand
   * @param o the operand to store
   */
  public static void setTarget(Instruction i, int k, BranchOperand o) {
    if (Configuration.ExtremeAssertions && !conforms(i)) fail(i, "MIR_LowTableSwitch");
    i.putOperand(2+k*2+0, o);
  }
  /**
   * Return the index of the k'th operand called Target
   * in the argument instruction.
   * @param i the instruction to access.
   * @param k the index of the operand.
   * @return the index of the k'th operand called Target
   *         in the argument instruction
   */
  public static int indexOfTarget(Instruction i, int k) {
    if (Configuration.ExtremeAssertions && !conforms(i)) fail(i, "MIR_LowTableSwitch");
    return 2+k*2+0;
  }
  /**
   * Does the argument instruction have a non-null
   * k'th operand named Target?
   * @param i the instruction to access.
   * @param k the index of the operand.
   * @return <code>true</code> if the instruction has an non-null
   *         k'th operand named Target or <code>false</code>
   *         if it does not.
   */
  public static boolean hasTarget(Instruction i, int k) {
    if (Configuration.ExtremeAssertions && !conforms(i)) fail(i, "MIR_LowTableSwitch");
    return i.getOperand(2+k*2+0) != null;
  }

  /**
   * Return the index of the first operand called Target
   * in the argument instruction.
   * @param i the instruction to access.
   * @return the index of the first operand called Target
   *         in the argument instruction
   */
  public static int indexOfTargets(Instruction i)
  {
    if (Configuration.ExtremeAssertions && !conforms(i)) fail(i, "MIR_LowTableSwitch");
    return 2;
  }
  /**
   * Does the argument instruction have any operands
   * named Target?
   * @param i the instruction to access.
   * @return <code>true</code> if the instruction has operands
   *         named Target or <code>false</code> if it does not.
   */
  public static boolean hasTargets(Instruction i)
  {
    if (Configuration.ExtremeAssertions && !conforms(i)) fail(i, "MIR_LowTableSwitch");
    return i.getNumberOfOperands()-2 > 0 && i.getOperand(2) != null;
  }

  /**
   * How many variable-length operands called Targets
   * does the argument instruction have?
   * @param i the instruction to access
   * @return the number of operands called Targets the instruction has
   */
  public static int getNumberOfTargets(Instruction i)
  {
    if (Configuration.ExtremeAssertions && !conforms(i)) fail(i, "MIR_LowTableSwitch");
    return (i.getNumberOfOperands()-2)/2;
  }

  /**
   * Change the number of Targets that may be stored in
   * the argument instruction to numVarOps.
   * @param i the instruction to access
   * @param numVarOps the new number of variable operands called Targets
   *        that may be stored in the instruction
   */
  public static void resizeNumberOfTargets(Instruction i, int numVarOps)
  {
    if (Configuration.ExtremeAssertions && !conforms(i)) fail(i, "MIR_LowTableSwitch");
  if (2+numVarOps*2>MIN_OPERAND_ARRAY_LENGTH)
    i.resizeNumberOfOperands(2+numVarOps*2);
  else
    for (int j = 2+numVarOps*2; j < MIN_OPERAND_ARRAY_LENGTH; j++)
      i.putOperand(j, null);
  }
  /**
   * Get the k'th operand called BranchProfile from the
   * argument instruction. Note that the returned operand
   * will still point to its containing instruction.
   * @param i the instruction to fetch the operand from
   * @param k the index of the operand
   * @return the k'th operand called BranchProfile
   */
  public static BranchProfileOperand getBranchProfile(Instruction i, int k) {
    if (Configuration.ExtremeAssertions && !conforms(i)) fail(i, "MIR_LowTableSwitch");
    return (BranchProfileOperand) i.getOperand(2+k*2+1);
  }
  /**
   * Get the k'th operand called BranchProfile from the argument
   * instruction clearing its instruction pointer. The returned
   * operand will not point to any containing instruction.
   * @param i the instruction to fetch the operand from
   * @param k the index of the operand
   * @return the k'th operand called BranchProfile
   */
  public static BranchProfileOperand getClearBranchProfile(Instruction i, int k) {
    if (Configuration.ExtremeAssertions && !conforms(i)) fail(i, "MIR_LowTableSwitch");
    return (BranchProfileOperand) i.getClearOperand(2+k*2+1);
  }
  /**
   * Set the k'th operand called BranchProfile in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param i the instruction in which to store the operand
   * @param k the index of the operand
   * @param o the operand to store
   */
  public static void setBranchProfile(Instruction i, int k, BranchProfileOperand o) {
    if (Configuration.ExtremeAssertions && !conforms(i)) fail(i, "MIR_LowTableSwitch");
    i.putOperand(2+k*2+1, o);
  }
  /**
   * Return the index of the k'th operand called BranchProfile
   * in the argument instruction.
   * @param i the instruction to access.
   * @param k the index of the operand.
   * @return the index of the k'th operand called BranchProfile
   *         in the argument instruction
   */
  public static int indexOfBranchProfile(Instruction i, int k) {
    if (Configuration.ExtremeAssertions && !conforms(i)) fail(i, "MIR_LowTableSwitch");
    return 2+k*2+1;
  }
  /**
   * Does the argument instruction have a non-null
   * k'th operand named BranchProfile?
   * @param i the instruction to access.
   * @param k the index of the operand.
   * @return <code>true</code> if the instruction has an non-null
   *         k'th operand named BranchProfile or <code>false</code>
   *         if it does not.
   */
  public static boolean hasBranchProfile(Instruction i, int k) {
    if (Configuration.ExtremeAssertions && !conforms(i)) fail(i, "MIR_LowTableSwitch");
    return i.getOperand(2+k*2+1) != null;
  }

  /**
   * Return the index of the first operand called BranchProfile
   * in the argument instruction.
   * @param i the instruction to access.
   * @return the index of the first operand called BranchProfile
   *         in the argument instruction
   */
  public static int indexOfBranchProfiles(Instruction i)
  {
    if (Configuration.ExtremeAssertions && !conforms(i)) fail(i, "MIR_LowTableSwitch");
    return 3;
  }
  /**
   * Does the argument instruction have any operands
   * named BranchProfile?
   * @param i the instruction to access.
   * @return <code>true</code> if the instruction has operands
   *         named BranchProfile or <code>false</code> if it does not.
   */
  public static boolean hasBranchProfiles(Instruction i)
  {
    if (Configuration.ExtremeAssertions && !conforms(i)) fail(i, "MIR_LowTableSwitch");
    return i.getNumberOfOperands()-3 > 0 && i.getOperand(3) != null;
  }

  /**
   * How many variable-length operands called BranchProfiles
   * does the argument instruction have?
   * @param i the instruction to access
   * @return the number of operands called BranchProfiles the instruction has
   */
  public static int getNumberOfBranchProfiles(Instruction i)
  {
    if (Configuration.ExtremeAssertions && !conforms(i)) fail(i, "MIR_LowTableSwitch");
    return (i.getNumberOfOperands()-2)/2;
  }

  /**
   * Change the number of BranchProfiles that may be stored in
   * the argument instruction to numVarOps.
   * @param i the instruction to access
   * @param numVarOps the new number of variable operands called BranchProfiles
   *        that may be stored in the instruction
   */
  public static void resizeNumberOfBranchProfiles(Instruction i, int numVarOps)
  {
    if (Configuration.ExtremeAssertions && !conforms(i)) fail(i, "MIR_LowTableSwitch");
  if (2+numVarOps*2>MIN_OPERAND_ARRAY_LENGTH)
    i.resizeNumberOfOperands(2+numVarOps*2);
  else
    for (int j = 2+numVarOps*2; j < MIN_OPERAND_ARRAY_LENGTH; j++)
      i.putOperand(j, null);
  }

  /**
   * Create an instruction of the MIR_LowTableSwitch instruction format.
   * @param o the instruction's operator
   * @param Index the instruction's Index operand
   * @param MethodStart the instruction's MethodStart operand
   * @param numVarOps the number of variable length operands that
   *                 will be stored in the insruction.
   * @return the newly created MIR_LowTableSwitch instruction
   */
  public static Instruction create(Operator o
                   , RegisterOperand Index
                   , RegisterOperand MethodStart
                   , int numVarOps
                )
  {
    if (Configuration.ExtremeAssertions && !conforms(o)) fail(o, "MIR_LowTableSwitch");
    Instruction i = new Instruction(o, Math.max(2+numVarOps*2, MIN_OPERAND_ARRAY_LENGTH));
    i.putOperand(0, Index);
    i.putOperand(1, MethodStart);
    return i;
  }

  /**
   * Mutate the argument instruction into an instruction of the
   * MIR_LowTableSwitch instruction format having the specified
   * operator and operands.
   * @param i the instruction to mutate
   * @param o the instruction's operator
   * @param Index the instruction's Index operand
   * @param MethodStart the instruction's MethodStart operand
   * @param numVarOps the number of variable length operands that
   *                  will be stored in the instruction.
   * @return the mutated instruction
   */
  public static Instruction mutate(Instruction i, Operator o
                   , RegisterOperand Index
                   , RegisterOperand MethodStart
                   , int numVarOps
                )
  {
    if (Configuration.ExtremeAssertions && !conforms(o)) fail(o, "MIR_LowTableSwitch");
    if (2+numVarOps*2>MIN_OPERAND_ARRAY_LENGTH)
      i.resizeNumberOfOperands(2+numVarOps*2);

    i.changeOperatorTo(o);
    i.putOperand(0, Index);
    i.putOperand(1, MethodStart);
    return i;
  }
}

