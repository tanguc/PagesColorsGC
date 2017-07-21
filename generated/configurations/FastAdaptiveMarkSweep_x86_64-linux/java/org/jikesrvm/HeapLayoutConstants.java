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
package org.jikesrvm;

import org.vmmagic.unboxed.*;

/**
 * Constants defining heap layout constants
 */
public final class HeapLayoutConstants {

  /** The address of the start of the data section of the boot image. */
  public static final Address BOOT_IMAGE_DATA_START =
    Address.fromIntZeroExtend( 0x60000000 );

  /** The address of the start of the code section of the boot image. */
  public static final Address BOOT_IMAGE_CODE_START =
    Address.fromIntZeroExtend( 0x64000000 );

  /** The address of the start of the ref map section of the boot image. */
  public static final Address BOOT_IMAGE_RMAP_START =
    Address.fromIntZeroExtend( 0x67000000 );

  /** The address in virtual memory that is the highest that can be mapped. */
  public static final Address MAXIMUM_MAPPABLE =
    Address.fromIntZeroExtend( 0xb0000000 );

  /** The maximum boot image data size */
  public static final int BOOT_IMAGE_DATA_SIZE = 60<<20; //0x03C00000

  /** The maximum boot image code size */
  public static final int BOOT_IMAGE_CODE_SIZE = 44<<20; //0x02C00000

  /* Typical compression ratio is about 1/20 */
  public static final int BAD_MAP_COMPRESSION = 5;  // conservative heuristic
  public static final int MAX_BOOT_IMAGE_RMAP_SIZE = BOOT_IMAGE_DATA_SIZE/BAD_MAP_COMPRESSION;

  /** The address of the end of the data section of the boot image. */
  public static final Address BOOT_IMAGE_DATA_END = BOOT_IMAGE_DATA_START.plus(BOOT_IMAGE_DATA_SIZE);
  /** The address of the end of the code section of the boot image. */
  public static final Address BOOT_IMAGE_CODE_END = BOOT_IMAGE_CODE_START.plus(BOOT_IMAGE_CODE_SIZE);
  /** The address of the end of the ref map section of the boot image. */
  public static final Address BOOT_IMAGE_RMAP_END = BOOT_IMAGE_RMAP_START.plus(MAX_BOOT_IMAGE_RMAP_SIZE);
  /** The address of the end of the boot image. */
  public static final Address BOOT_IMAGE_END = BOOT_IMAGE_RMAP_END;

  private HeapLayoutConstants() {
    // prevent instantiation
  }

}
