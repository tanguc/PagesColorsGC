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
package org.jikesrvm.compilers.opt.ir.operand;

import org.jikesrvm.classloader.TypeReference;
import org.jikesrvm.compilers.opt.util.Bits;

/**
 * Represents a constant int operand.
 *
 * @see Operand
 */
public final class IntConstantOperand extends ConstantOperand {

  /**
   * Constant 0, can be copied as convenient
   */
  public static final IntConstantOperand zero = new IntConstantOperand(0);

  /**
   * Value of this operand.
   */
  public final int value;

  /**
   * Constructs a new int constant operand with the specified value.
   * Type will be determined by value.
   *
   * @param v value
   */
  public IntConstantOperand(int v) {
    value = v;
  }

  /**
   * Return the {@link TypeReference} of the value represented by
   * the operand. For int constants we speculate on the type
   * dependenent on the constant value.
   *
   * @return a speculation on the type of the value represented by the
   * operand.
   */
  @Override
  public TypeReference getType() {
    if ((value == 0) || (value == 1)) {
      return TypeReference.Boolean;
    } else if (-128 <= value && value <= 127) {
      return TypeReference.Byte;
    } else if (-32768 <= value && value <= 32767) {
      return TypeReference.Short;
    } else {
      return TypeReference.Int;
    }
  }

  /**
   * @return <code>true</code>
   */
  @Override
  public boolean isIntLike() {
    return true;
  }

  /**
   * @return <code>true</code>
   */
  @Override
  public boolean isInt() {
    return true;
  }

  @Override
  public Operand copy() {
    return new IntConstantOperand(value);
  }

  /**
   * @return the lower 8 bits (as an int) of value
   */
  public int lower8() {
    return Bits.lower8(value);
  }

  /**
   * @return the lower 16 bits (as an int) of value
   */
  public int lower16() {
    return Bits.lower16(value);
  }

  /**
   * @return the upper 16 bits (as an int) of value
   */
  public int upper16() {
    return Bits.upper16(value);
  }

  /**
   * @return the upper 24 bits (as an int) of value
   */
  public int upper24() {
    return Bits.upper24(value);
  }

  @Override
  public boolean similar(Operand op) {
    return (op instanceof IntConstantOperand) && (value == ((IntConstantOperand) op).value);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof IntConstantOperand) && (value == ((IntConstantOperand) o).value);
  }

  @Override
  public int hashCode() {
    return value;
  }

  /**
   * Returns the string representation of this operand.
   *
   * @return a string representation of this operand.
   */
  @Override
  public String toString() {
    if (value > 0xffff || value < -0xffff) {
      return "0x" + Integer.toHexString(value);
    } else {
      return Integer.toString(value);
    }
  }
}
