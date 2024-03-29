
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
 * The Empty InstructionFormat class.
 *
 * The header comment for {@link Instruction} contains
 * an explanation of the role of InstructionFormats in the
 * opt compiler's IR.
 */
@SuppressWarnings("unused")  // Machine generated code is never 100% clean
public final class Empty extends InstructionFormat {
  /**
   * InstructionFormat identification method for Empty.
   * @param i an instruction
   * @return <code>true</code> if the InstructionFormat of the argument
   *         instruction is Empty or <code>false</code>
   *         if it is not.
   */
  public static boolean conforms(Instruction i) {
    return conforms(i.operator());
  }
  /**
   * InstructionFormat identification method for Empty.
   * @param o an instruction
   * @return <code>true</code> if the InstructionFormat of the argument
   *         operator is Empty or <code>false</code>
   *         if it is not.
   */
  public static boolean conforms(Operator o) {
    return o.format == Empty_format;
  }


  /**
   * Create an instruction of the Empty instruction format.
   * @param o the instruction's operator
   * @return the newly created Empty instruction
   */
  public static Instruction create(Operator o
                )
  {
    if (Configuration.ExtremeAssertions && !conforms(o)) fail(o, "Empty");
    Instruction i = new Instruction(o, 5);
    return i;
  }

  /**
   * Mutate the argument instruction into an instruction of the
   * Empty instruction format having the specified
   * operator and operands.
   * @param i the instruction to mutate
   * @param o the instruction's operator
   * @return the mutated instruction
   */
  public static Instruction mutate(Instruction i, Operator o
                )
  {
    if (Configuration.ExtremeAssertions && !conforms(o)) fail(o, "Empty");
    i.changeOperatorTo(o);
    return i;
  }
}

