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
package org.jikesrvm.compilers.opt.lir2mir.ppc;

import static org.jikesrvm.compilers.opt.ir.Operators.*;
import static org.jikesrvm.compilers.opt.lir2mir.ppc.BURS_Definitions.*;

import org.jikesrvm.compilers.opt.ir.ppc.*;
import org.jikesrvm.classloader.*;

import org.jikesrvm.*;
import org.jikesrvm.runtime.ArchEntrypoints;
import org.jikesrvm.compilers.opt.ir.*;
import org.jikesrvm.compilers.opt.ir.operand.*;
import org.jikesrvm.compilers.opt.ir.operand.ppc.*;
import org.jikesrvm.compilers.opt.lir2mir.BURS;
import org.jikesrvm.compilers.opt.OptimizingCompilerException;
import org.jikesrvm.compilers.opt.util.Bits; //NOPMD
import org.jikesrvm.ArchitectureSpecificOpt.BURS_TreeNode;

import org.vmmagic.unboxed.*;

/**
 * Machine-specific instruction selection rules.  Program generated.
 *
 * Note: some of the functions have been taken and modified
 * from the file gen.c, from the LCC compiler.
 * See $RVM_ROOT/rvm/src-generated/opt-burs/jburg/COPYRIGHT file for copyright restrictions.
 *
 * @see BURS
 *
 * NOTE: Program generated file, do not edit!
 */
@SuppressWarnings("unused") // Machine generated code is hard to get perfect
public class BURS_STATE extends BURS_Helpers {

          static final byte NOFLAGS           = 0x00;
          static final byte EMIT_INSTRUCTION  = 0x01;
   public static final byte LEFT_CHILD_FIRST  = 0x02;
   public static final byte RIGHT_CHILD_FIRST = 0x04;

   public BURS_STATE(BURS b) {
      super(b);
   }

/*****************************************************************/
/*                                                               */
/*  BURS TEMPLATE                                                */
/*                                                               */
/*****************************************************************/

   /**
    * Gets the state of a BURS node. This accessor is used by BURS.
    *
    * @param a the node
    *
    * @return the node's state
    */
   private static BURS_TreeNode STATE(BURS_TreeNode a) { return a; }

   /***********************************************************************
    *
    *   This file contains BURG utilities
    *
    *   Note: some of the functions have been taken and modified
    *    from the file gen.c, from the LCC compiler.
    *
    ************************************************************************/
   
   /**
    * Prints a debug message. No-op if debugging is disabled.
    *
    * @param p the BURS node
    * @param rule the rule
    * @param cost the rule's cost
    * @param bestcost the best cost seen so far
    */
   void trace(BURS_TreeNode p, int rule, int cost, int bestcost) {
     if (BURS.DEBUG) {
       VM.sysWrite(p+" matched "+BURS_Debug.string[rule]+" with cost "+
		   cost+"vs. "+bestcost);
     }
   }

   /**
    * Dumps the whole tree starting at the given node. No-op if
    * debugging is disabled.
    *
    * @param p the node to start at
    */
   public static void dumpTree(BURS_TreeNode p) {
     if (BURS.DEBUG) {
       VM.sysWrite(dumpTree("\n",p,1));
     }
   }

   public static String dumpTree(String out, BURS_TreeNode p, int indent) {
     if (p == null) return out;
     StringBuilder result = new StringBuilder(out);
     for (int i=0; i<indent; i++)
       result.append("   ");
     result.append(p);
     result.append('\n');
     if (p.child1 != null) {
       indent++;
       result.append(dumpTree("",p.child1,indent));
       if (p.child2 != null) {
         result.append(dumpTree("",p.child2,indent));
       }
     }
     return result.toString();
   }

   /**
    * Dumps the cover of a tree, i.e. the rules
    * that cover the tree with a minimal cost. No-op if debugging is
    * disabled.
    *
    * @param p the tree's root
    * @param goalnt the goal's non-terminal
    * @param indent number of spaces to use for indentation
    */
   public static void dumpCover(BURS_TreeNode p, byte goalnt, int indent){
      if (BURS.DEBUG) {
	if (p == null) return;
	int rule = STATE(p).rule(goalnt);
	VM.sysWrite(STATE(p).getCost(goalnt)+"\t");
	for (int i = 0; i < indent; i++)
          VM.sysWrite(' ');
	VM.sysWrite(BURS_Debug.string[rule]+"\n");
	for (int i = 0; i < nts[rule].length; i++)
          dumpCover(kids(p,rule,i), nts[rule][i], indent + 1);
      }
   }

   // caution: MARK should be used in single threaded mode,
   public static void mark(BURS_TreeNode p, byte goalnt) {
     if (p == null) return;
     int rule = STATE(p).rule(goalnt);
     byte act = action[rule];
     if ((act & EMIT_INSTRUCTION) != 0) {
       p.setNonTerminal(goalnt);
     }
     if (rule == 0) {
       throw new OptimizingCompilerException("BURS","rule missing in ",
						 p.getInstruction().toString(), dumpTree("",p,1));
     }
     mark_kids(p,rule);
   }
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
//ir.brg

/**
 * Generate from ir.template and assembled rules files.
 */
 private static final byte[] nts_0 = { r_NT,  };
 private static final byte[] nts_1 = {  };
 private static final byte[] nts_2 = { czr_NT,  };
 private static final byte[] nts_3 = { rs_NT,  };
 private static final byte[] nts_4 = { rz_NT,  };
 private static final byte[] nts_5 = { rp_NT,  };
 private static final byte[] nts_6 = { any_NT, any_NT,  };
 private static final byte[] nts_7 = { r_NT, r_NT,  };
 private static final byte[] nts_8 = { boolcmp_NT,  };
 private static final byte[] nts_9 = { r_NT, r_NT, r_NT,  };
 private static final byte[] nts_10 = { r_NT, any_NT,  };
 private static final byte[] nts_11 = { any_NT,  };

private static final byte[][] nts = {
	null,	/* 0 */
	nts_0,	// 1 
	nts_1,	// 2 
	nts_2,	// 3 
	nts_3,	// 4 
	nts_4,	// 5 
	nts_5,	// 6 
	nts_5,	// 7 
	nts_1,	// 8 
	nts_0,	// 9 
	nts_1,	// 10 
	nts_1,	// 11 
	nts_1,	// 12 
	nts_6,	// 13 
	nts_1,	// 14 
	nts_1,	// 15 
	nts_1,	// 16 
	nts_1,	// 17 
	nts_1,	// 18 
	nts_1,	// 19 
	nts_1,	// 20 
	nts_0,	// 21 
	nts_1,	// 22 
	nts_1,	// 23 
	nts_1,	// 24 
	nts_0,	// 25 
	nts_1,	// 26 
	nts_0,	// 27 
	nts_1,	// 28 
	nts_1,	// 29 
	nts_1,	// 30 
	nts_0,	// 31 
	nts_0,	// 32 
	nts_0,	// 33 
	nts_0,	// 34 
	nts_0,	// 35 
	nts_0,	// 36 
	nts_0,	// 37 
	nts_1,	// 38 
	nts_7,	// 39 
	nts_0,	// 40 
	nts_0,	// 41 
	nts_0,	// 42 
	nts_0,	// 43 
	nts_7,	// 44 
	nts_0,	// 45 
	nts_7,	// 46 
	nts_0,	// 47 
	nts_7,	// 48 
	nts_0,	// 49 
	nts_7,	// 50 
	nts_8,	// 51 
	nts_8,	// 52 
	nts_8,	// 53 
	nts_8,	// 54 
	nts_8,	// 55 
	nts_8,	// 56 
	nts_8,	// 57 
	nts_8,	// 58 
	nts_0,	// 59 
	nts_7,	// 60 
	nts_0,	// 61 
	nts_0,	// 62 
	nts_7,	// 63 
	nts_0,	// 64 
	nts_0,	// 65 
	nts_7,	// 66 
	nts_7,	// 67 
	nts_0,	// 68 
	nts_7,	// 69 
	nts_0,	// 70 
	nts_0,	// 71 
	nts_0,	// 72 
	nts_7,	// 73 
	nts_0,	// 74 
	nts_0,	// 75 
	nts_7,	// 76 
	nts_0,	// 77 
	nts_0,	// 78 
	nts_7,	// 79 
	nts_0,	// 80 
	nts_0,	// 81 
	nts_0,	// 82 
	nts_7,	// 83 
	nts_0,	// 84 
	nts_0,	// 85 
	nts_7,	// 86 
	nts_7,	// 87 
	nts_0,	// 88 
	nts_0,	// 89 
	nts_7,	// 90 
	nts_0,	// 91 
	nts_7,	// 92 
	nts_7,	// 93 
	nts_7,	// 94 
	nts_0,	// 95 
	nts_0,	// 96 
	nts_7,	// 97 
	nts_7,	// 98 
	nts_7,	// 99 
	nts_7,	// 100 
	nts_7,	// 101 
	nts_7,	// 102 
	nts_7,	// 103 
	nts_7,	// 104 
	nts_7,	// 105 
	nts_7,	// 106 
	nts_7,	// 107 
	nts_0,	// 108 
	nts_0,	// 109 
	nts_0,	// 110 
	nts_0,	// 111 
	nts_9,	// 112 
	nts_9,	// 113 
	nts_9,	// 114 
	nts_9,	// 115 
	nts_9,	// 116 
	nts_9,	// 117 
	nts_9,	// 118 
	nts_9,	// 119 
	nts_9,	// 120 
	nts_9,	// 121 
	nts_9,	// 122 
	nts_9,	// 123 
	nts_0,	// 124 
	nts_0,	// 125 
	nts_0,	// 126 
	nts_0,	// 127 
	nts_0,	// 128 
	nts_0,	// 129 
	nts_0,	// 130 
	nts_0,	// 131 
	nts_0,	// 132 
	nts_0,	// 133 
	nts_0,	// 134 
	nts_0,	// 135 
	nts_1,	// 136 
	nts_1,	// 137 
	nts_1,	// 138 
	nts_0,	// 139 
	nts_0,	// 140 
	nts_0,	// 141 
	nts_7,	// 142 
	nts_7,	// 143 
	nts_0,	// 144 
	nts_0,	// 145 
	nts_7,	// 146 
	nts_0,	// 147 
	nts_7,	// 148 
	nts_0,	// 149 
	nts_7,	// 150 
	nts_0,	// 151 
	nts_0,	// 152 
	nts_7,	// 153 
	nts_0,	// 154 
	nts_0,	// 155 
	nts_7,	// 156 
	nts_0,	// 157 
	nts_0,	// 158 
	nts_7,	// 159 
	nts_7,	// 160 
	nts_0,	// 161 
	nts_7,	// 162 
	nts_9,	// 163 
	nts_7,	// 164 
	nts_9,	// 165 
	nts_7,	// 166 
	nts_9,	// 167 
	nts_7,	// 168 
	nts_9,	// 169 
	nts_7,	// 170 
	nts_9,	// 171 
	nts_7,	// 172 
	nts_7,	// 173 
	nts_9,	// 174 
	nts_7,	// 175 
	nts_7,	// 176 
	nts_7,	// 177 
	nts_9,	// 178 
	nts_7,	// 179 
	nts_7,	// 180 
	nts_9,	// 181 
	nts_7,	// 182 
	nts_0,	// 183 
	nts_0,	// 184 
	nts_0,	// 185 
	nts_7,	// 186 
	nts_7,	// 187 
	nts_7,	// 188 
	nts_0,	// 189 
	nts_0,	// 190 
	nts_0,	// 191 
	nts_0,	// 192 
	nts_8,	// 193 
	nts_8,	// 194 
	nts_8,	// 195 
	nts_8,	// 196 
	nts_7,	// 197 
	nts_0,	// 198 
	nts_7,	// 199 
	nts_7,	// 200 
	nts_7,	// 201 
	nts_7,	// 202 
	nts_7,	// 203 
	nts_7,	// 204 
	nts_1,	// 205 
	nts_1,	// 206 
	nts_0,	// 207 
	nts_10,	// 208 
	nts_11,	// 209 
	nts_10,	// 210 
	nts_1,	// 211 
	nts_7,	// 212 
	nts_6,	// 213 
	nts_7,	// 214 
	nts_7,	// 215 
	nts_7,	// 216 
	nts_7,	// 217 
	nts_1,	// 218 
	nts_7,	// 219 
	nts_7,	// 220 
	nts_7,	// 221 
	nts_0,	// 222 
	nts_7,	// 223 
	nts_0,	// 224 
	nts_7,	// 225 
	nts_0,	// 226 
	nts_7,	// 227 
	nts_0,	// 228 
	nts_7,	// 229 
	nts_7,	// 230 
	nts_7,	// 231 
	nts_0,	// 232 
	nts_0,	// 233 
	nts_0,	// 234 
	nts_0,	// 235 
	nts_0,	// 236 
	nts_1,	// 237 
	nts_0,	// 238 
	nts_7,	// 239 
	nts_7,	// 240 
	nts_7,	// 241 
	nts_7,	// 242 
	nts_0,	// 243 
	nts_0,	// 244 
	nts_7,	// 245 
	nts_7,	// 246 
	nts_7,	// 247 
	nts_7,	// 248 
	nts_0,	// 249 
	nts_0,	// 250 
	nts_0,	// 251 
	nts_7,	// 252 
	nts_7,	// 253 
	nts_0,	// 254 
	nts_0,	// 255 
	nts_0,	// 256 
	nts_0,	// 257 
	nts_0,	// 258 
	nts_1,	// 259 
	nts_1,	// 260 
	nts_1,	// 261 
	nts_0,	// 262 
	nts_0,	// 263 
	nts_7,	// 264 
	nts_7,	// 265 
	nts_7,	// 266 
	nts_9,	// 267 
	nts_7,	// 268 
	nts_7,	// 269 
};

/*static final byte arity[] = {
	0,	// 0=GET_CAUGHT_EXCEPTION
	1,	// 1=SET_CAUGHT_EXCEPTION
	-1,	// 2=NEW
	-1,	// 3=NEW_UNRESOLVED
	-1,	// 4=NEWARRAY
	-1,	// 5=NEWARRAY_UNRESOLVED
	-1,	// 6=ATHROW
	-1,	// 7=CHECKCAST
	-1,	// 8=CHECKCAST_NOTNULL
	-1,	// 9=CHECKCAST_UNRESOLVED
	-1,	// 10=MUST_IMPLEMENT_INTERFACE
	-1,	// 11=INSTANCEOF
	-1,	// 12=INSTANCEOF_NOTNULL
	-1,	// 13=INSTANCEOF_UNRESOLVED
	-1,	// 14=MONITORENTER
	-1,	// 15=MONITOREXIT
	-1,	// 16=NEWOBJMULTIARRAY
	-1,	// 17=GETSTATIC
	-1,	// 18=PUTSTATIC
	-1,	// 19=GETFIELD
	-1,	// 20=PUTFIELD
	-1,	// 21=INT_ZERO_CHECK
	-1,	// 22=LONG_ZERO_CHECK
	-1,	// 23=BOUNDS_CHECK
	-1,	// 24=OBJARRAY_STORE_CHECK
	-1,	// 25=OBJARRAY_STORE_CHECK_NOTNULL
	0,	// 26=IG_PATCH_POINT
	-1,	// 27=IG_CLASS_TEST
	-1,	// 28=IG_METHOD_TEST
	-1,	// 29=TABLESWITCH
	-1,	// 30=LOOKUPSWITCH
	-1,	// 31=INT_ALOAD
	-1,	// 32=LONG_ALOAD
	-1,	// 33=FLOAT_ALOAD
	-1,	// 34=DOUBLE_ALOAD
	-1,	// 35=REF_ALOAD
	-1,	// 36=UBYTE_ALOAD
	-1,	// 37=BYTE_ALOAD
	-1,	// 38=USHORT_ALOAD
	-1,	// 39=SHORT_ALOAD
	-1,	// 40=INT_ASTORE
	-1,	// 41=LONG_ASTORE
	-1,	// 42=FLOAT_ASTORE
	-1,	// 43=DOUBLE_ASTORE
	-1,	// 44=REF_ASTORE
	-1,	// 45=BYTE_ASTORE
	-1,	// 46=SHORT_ASTORE
	2,	// 47=INT_IFCMP
	2,	// 48=INT_IFCMP2
	2,	// 49=LONG_IFCMP
	2,	// 50=FLOAT_IFCMP
	2,	// 51=DOUBLE_IFCMP
	-1,	// 52=REF_IFCMP
	-1,	// 53=LABEL
	-1,	// 54=BBEND
	0,	// 55=UNINT_BEGIN
	0,	// 56=UNINT_END
	0,	// 57=FENCE
	0,	// 58=READ_CEILING
	0,	// 59=WRITE_FLOOR
	-1,	// 60=PHI
	-1,	// 61=SPLIT
	-1,	// 62=PI
	0,	// 63=NOP
	-1,	// 64=INT_MOVE
	1,	// 65=LONG_MOVE
	1,	// 66=FLOAT_MOVE
	1,	// 67=DOUBLE_MOVE
	1,	// 68=REF_MOVE
	0,	// 69=GUARD_MOVE
	-1,	// 70=INT_COND_MOVE
	-1,	// 71=LONG_COND_MOVE
	-1,	// 72=FLOAT_COND_MOVE
	-1,	// 73=DOUBLE_COND_MOVE
	-1,	// 74=REF_COND_MOVE
	-1,	// 75=GUARD_COND_MOVE
	0,	// 76=GUARD_COMBINE
	2,	// 77=REF_ADD
	-1,	// 78=INT_ADD
	2,	// 79=LONG_ADD
	2,	// 80=FLOAT_ADD
	2,	// 81=DOUBLE_ADD
	2,	// 82=REF_SUB
	-1,	// 83=INT_SUB
	2,	// 84=LONG_SUB
	2,	// 85=FLOAT_SUB
	2,	// 86=DOUBLE_SUB
	2,	// 87=INT_MUL
	2,	// 88=LONG_MUL
	2,	// 89=FLOAT_MUL
	2,	// 90=DOUBLE_MUL
	2,	// 91=INT_DIV
	-1,	// 92=LONG_DIV
	2,	// 93=FLOAT_DIV
	2,	// 94=DOUBLE_DIV
	2,	// 95=INT_REM
	-1,	// 96=LONG_REM
	-1,	// 97=FLOAT_REM
	-1,	// 98=DOUBLE_REM
	1,	// 99=REF_NEG
	-1,	// 100=INT_NEG
	1,	// 101=LONG_NEG
	1,	// 102=FLOAT_NEG
	1,	// 103=DOUBLE_NEG
	1,	// 104=FLOAT_SQRT
	1,	// 105=DOUBLE_SQRT
	-1,	// 106=REF_SHL
	2,	// 107=INT_SHL
	2,	// 108=LONG_SHL
	-1,	// 109=REF_SHR
	2,	// 110=INT_SHR
	2,	// 111=LONG_SHR
	-1,	// 112=REF_USHR
	2,	// 113=INT_USHR
	2,	// 114=LONG_USHR
	2,	// 115=REF_AND
	-1,	// 116=INT_AND
	2,	// 117=LONG_AND
	2,	// 118=REF_OR
	-1,	// 119=INT_OR
	2,	// 120=LONG_OR
	2,	// 121=REF_XOR
	-1,	// 122=INT_XOR
	1,	// 123=REF_NOT
	-1,	// 124=INT_NOT
	1,	// 125=LONG_NOT
	2,	// 126=LONG_XOR
	-1,	// 127=INT_2ADDRSigExt
	-1,	// 128=INT_2ADDRZerExt
	-1,	// 129=LONG_2ADDR
	-1,	// 130=ADDR_2INT
	-1,	// 131=ADDR_2LONG
	1,	// 132=INT_2LONG
	1,	// 133=INT_2FLOAT
	1,	// 134=INT_2DOUBLE
	1,	// 135=LONG_2INT
	-1,	// 136=LONG_2FLOAT
	-1,	// 137=LONG_2DOUBLE
	1,	// 138=FLOAT_2INT
	-1,	// 139=FLOAT_2LONG
	1,	// 140=FLOAT_2DOUBLE
	1,	// 141=DOUBLE_2INT
	-1,	// 142=DOUBLE_2LONG
	1,	// 143=DOUBLE_2FLOAT
	1,	// 144=INT_2BYTE
	1,	// 145=INT_2USHORT
	1,	// 146=INT_2SHORT
	2,	// 147=LONG_CMP
	2,	// 148=FLOAT_CMPL
	2,	// 149=FLOAT_CMPG
	2,	// 150=DOUBLE_CMPL
	2,	// 151=DOUBLE_CMPG
	1,	// 152=RETURN
	1,	// 153=NULL_CHECK
	0,	// 154=GOTO
	1,	// 155=BOOLEAN_NOT
	2,	// 156=BOOLEAN_CMP_INT
	2,	// 157=BOOLEAN_CMP_ADDR
	-1,	// 158=BOOLEAN_CMP_LONG
	-1,	// 159=BOOLEAN_CMP_FLOAT
	-1,	// 160=BOOLEAN_CMP_DOUBLE
	2,	// 161=BYTE_LOAD
	2,	// 162=UBYTE_LOAD
	2,	// 163=SHORT_LOAD
	2,	// 164=USHORT_LOAD
	-1,	// 165=REF_LOAD
	-1,	// 166=REF_STORE
	2,	// 167=INT_LOAD
	2,	// 168=LONG_LOAD
	2,	// 169=FLOAT_LOAD
	2,	// 170=DOUBLE_LOAD
	2,	// 171=BYTE_STORE
	2,	// 172=SHORT_STORE
	2,	// 173=INT_STORE
	2,	// 174=LONG_STORE
	2,	// 175=FLOAT_STORE
	2,	// 176=DOUBLE_STORE
	2,	// 177=PREPARE_INT
	2,	// 178=PREPARE_ADDR
	2,	// 179=PREPARE_LONG
	2,	// 180=ATTEMPT_INT
	2,	// 181=ATTEMPT_ADDR
	2,	// 182=ATTEMPT_LONG
	2,	// 183=CALL
	2,	// 184=SYSCALL
	0,	// 185=YIELDPOINT_PROLOGUE
	0,	// 186=YIELDPOINT_EPILOGUE
	0,	// 187=YIELDPOINT_BACKEDGE
	2,	// 188=YIELDPOINT_OSR
	-1,	// 189=OSR_BARRIER
	0,	// 190=IR_PROLOGUE
	0,	// 191=RESOLVE
	-1,	// 192=RESOLVE_MEMBER
	0,	// 193=GET_TIME_BASE
	-1,	// 194=INSTRUMENTED_EVENT_COUNTER
	2,	// 195=TRAP_IF
	0,	// 196=TRAP
	1,	// 197=FLOAT_AS_INT_BITS
	1,	// 198=INT_BITS_AS_FLOAT
	1,	// 199=DOUBLE_AS_LONG_BITS
	1,	// 200=LONG_BITS_AS_DOUBLE
	-1,	// 201=ARRAYLENGTH
	-1,	// 202=GET_OBJ_TIB
	-1,	// 203=GET_CLASS_TIB
	-1,	// 204=GET_TYPE_FROM_TIB
	-1,	// 205=GET_SUPERCLASS_IDS_FROM_TIB
	-1,	// 206=GET_DOES_IMPLEMENT_FROM_TIB
	-1,	// 207=GET_ARRAY_ELEMENT_TIB_FROM_TIB
	1,	// 208=LOWTABLESWITCH
	0,	// 209=ADDRESS_CONSTANT
	0,	// 210=INT_CONSTANT
	0,	// 211=LONG_CONSTANT
	0,	// 212=REGISTER
	2,	// 213=OTHER_OPERAND
	0,	// 214=NULL
	0,	// 215=BRANCH_TARGET
	1,	// 216=DCBF
	1,	// 217=DCBST
	1,	// 218=DCBT
	1,	// 219=DCBTST
	1,	// 220=DCBZ
	1,	// 221=DCBZL
	1,	// 222=ICBI
	-1,	// 223=CALL_SAVE_VOLATILE
	-1,	// 224=MIR_START
	-1,	// 225=MIR_LOWTABLESWITCH
	-1,	// 226=PPC_DATA_INT
	-1,	// 227=PPC_DATA_LABEL
	-1,	// 228=PPC_ADD
	-1,	// 229=PPC_ADDr
	-1,	// 230=PPC_ADDC
	-1,	// 231=PPC_ADDE
	-1,	// 232=PPC_ADDZE
	-1,	// 233=PPC_ADDME
	-1,	// 234=PPC_ADDIC
	-1,	// 235=PPC_ADDICr
	-1,	// 236=PPC_SUBF
	-1,	// 237=PPC_SUBFr
	-1,	// 238=PPC_SUBFC
	-1,	// 239=PPC_SUBFCr
	-1,	// 240=PPC_SUBFIC
	-1,	// 241=PPC_SUBFE
	-1,	// 242=PPC_SUBFZE
	-1,	// 243=PPC_SUBFME
	-1,	// 244=PPC_AND
	-1,	// 245=PPC_ANDr
	-1,	// 246=PPC_ANDIr
	-1,	// 247=PPC_ANDISr
	-1,	// 248=PPC_NAND
	-1,	// 249=PPC_NANDr
	-1,	// 250=PPC_ANDC
	-1,	// 251=PPC_ANDCr
	-1,	// 252=PPC_OR
	-1,	// 253=PPC_ORr
	-1,	// 254=PPC_MOVE
	-1,	// 255=PPC_ORI
	-1,	// 256=PPC_ORIS
	-1,	// 257=PPC_NOR
	-1,	// 258=PPC_NORr
	-1,	// 259=PPC_ORC
	-1,	// 260=PPC_ORCr
	-1,	// 261=PPC_XOR
	-1,	// 262=PPC_XORr
	-1,	// 263=PPC_XORI
	-1,	// 264=PPC_XORIS
	-1,	// 265=PPC_EQV
	-1,	// 266=PPC_EQVr
	-1,	// 267=PPC_NEG
	-1,	// 268=PPC_NEGr
	-1,	// 269=PPC_CNTLZW
	-1,	// 270=PPC_EXTSB
	-1,	// 271=PPC_EXTSBr
	-1,	// 272=PPC_EXTSH
	-1,	// 273=PPC_EXTSHr
	-1,	// 274=PPC_SLW
	-1,	// 275=PPC_SLWr
	-1,	// 276=PPC_SLWI
	-1,	// 277=PPC_SLWIr
	-1,	// 278=PPC_SRW
	-1,	// 279=PPC_SRWr
	-1,	// 280=PPC_SRWI
	-1,	// 281=PPC_SRWIr
	-1,	// 282=PPC_SRAW
	-1,	// 283=PPC_SRAWr
	-1,	// 284=PPC_SRAWI
	-1,	// 285=PPC_SRAWIr
	-1,	// 286=PPC_RLWINM
	-1,	// 287=PPC_RLWINMr
	-1,	// 288=PPC_RLWIMI
	-1,	// 289=PPC_RLWIMIr
	-1,	// 290=PPC_RLWNM
	-1,	// 291=PPC_RLWNMr
	-1,	// 292=PPC_B
	-1,	// 293=PPC_BL
	-1,	// 294=PPC_BL_SYS
	-1,	// 295=PPC_BLR
	-1,	// 296=PPC_BCTR
	-1,	// 297=PPC_BCTRL
	-1,	// 298=PPC_BCTRL_SYS
	-1,	// 299=PPC_BCLR
	-1,	// 300=PPC_BLRL
	-1,	// 301=PPC_BCLRL
	-1,	// 302=PPC_BC
	-1,	// 303=PPC_BCL
	-1,	// 304=PPC_BCOND
	-1,	// 305=PPC_BCOND2
	-1,	// 306=PPC_BCCTR
	-1,	// 307=PPC_BCC
	-1,	// 308=PPC_ADDI
	-1,	// 309=PPC_ADDIS
	-1,	// 310=PPC_LDI
	-1,	// 311=PPC_LDIS
	-1,	// 312=PPC_CMP
	-1,	// 313=PPC_CMPI
	-1,	// 314=PPC_CMPL
	-1,	// 315=PPC_CMPLI
	-1,	// 316=PPC_CRAND
	-1,	// 317=PPC_CRANDC
	-1,	// 318=PPC_CROR
	-1,	// 319=PPC_CRORC
	-1,	// 320=PPC_FMR
	-1,	// 321=PPC_FRSP
	-1,	// 322=PPC_FCTIW
	-1,	// 323=PPC_FCTIWZ
	-1,	// 324=PPC_FADD
	-1,	// 325=PPC_FADDS
	-1,	// 326=PPC_FSQRT
	-1,	// 327=PPC_FSQRTS
	-1,	// 328=PPC_FABS
	-1,	// 329=PPC_FCMPO
	-1,	// 330=PPC_FCMPU
	-1,	// 331=PPC_FDIV
	-1,	// 332=PPC_FDIVS
	-1,	// 333=PPC_DIVW
	-1,	// 334=PPC_DIVWU
	-1,	// 335=PPC_FMUL
	-1,	// 336=PPC_FMULS
	-1,	// 337=PPC_FSEL
	-1,	// 338=PPC_FMADD
	-1,	// 339=PPC_FMADDS
	-1,	// 340=PPC_FMSUB
	-1,	// 341=PPC_FMSUBS
	-1,	// 342=PPC_FNMADD
	-1,	// 343=PPC_FNMADDS
	-1,	// 344=PPC_FNMSUB
	-1,	// 345=PPC_FNMSUBS
	-1,	// 346=PPC_MULLI
	-1,	// 347=PPC_MULLW
	-1,	// 348=PPC_MULHW
	-1,	// 349=PPC_MULHWU
	-1,	// 350=PPC_FNEG
	-1,	// 351=PPC_FSUB
	-1,	// 352=PPC_FSUBS
	-1,	// 353=PPC_LWZ
	-1,	// 354=PPC_LWZU
	-1,	// 355=PPC_LWZUX
	-1,	// 356=PPC_LWZX
	-1,	// 357=PPC_LWARX
	-1,	// 358=PPC_LBZ
	-1,	// 359=PPC_LBZUX
	-1,	// 360=PPC_LBZX
	-1,	// 361=PPC_LHA
	-1,	// 362=PPC_LHAX
	-1,	// 363=PPC_LHZ
	-1,	// 364=PPC_LHZX
	-1,	// 365=PPC_LFD
	-1,	// 366=PPC_LFDX
	-1,	// 367=PPC_LFS
	-1,	// 368=PPC_LFSX
	-1,	// 369=PPC_LMW
	-1,	// 370=PPC_STW
	-1,	// 371=PPC_STWX
	-1,	// 372=PPC_STWCXr
	-1,	// 373=PPC_STWU
	-1,	// 374=PPC_STB
	-1,	// 375=PPC_STBX
	-1,	// 376=PPC_STH
	-1,	// 377=PPC_STHX
	-1,	// 378=PPC_STFD
	-1,	// 379=PPC_STFDX
	-1,	// 380=PPC_STFDU
	-1,	// 381=PPC_STFS
	-1,	// 382=PPC_STFSX
	-1,	// 383=PPC_STFSU
	-1,	// 384=PPC_STMW
	-1,	// 385=PPC_TW
	-1,	// 386=PPC_TWI
	-1,	// 387=PPC_MFSPR
	-1,	// 388=PPC_MTSPR
	-1,	// 389=PPC_MFTB
	-1,	// 390=PPC_MFTBU
	-1,	// 391=PPC_SYNC
	-1,	// 392=PPC_ISYNC
	-1,	// 393=PPC_DCBF
	-1,	// 394=PPC_DCBST
	-1,	// 395=PPC_DCBT
	-1,	// 396=PPC_DCBTST
	-1,	// 397=PPC_DCBZ
	-1,	// 398=PPC_DCBZL
	-1,	// 399=PPC_ICBI
	-1,	// 400=PPC64_EXTSW
	-1,	// 401=PPC64_EXTSWr
	-1,	// 402=PPC64_EXTZW
	-1,	// 403=PPC64_RLDICL
	-1,	// 404=PPC64_RLDICR
	-1,	// 405=PPC64_SLD
	-1,	// 406=PPC64_SLDr
	-1,	// 407=PPC64_SLDI
	-1,	// 408=PPC64_SRD
	-1,	// 409=PPC64_SRDr
	-1,	// 410=PPC64_SRAD
	-1,	// 411=PPC64_SRADr
	-1,	// 412=PPC64_SRADI
	-1,	// 413=PPC64_SRADIr
	-1,	// 414=PPC64_SRDI
	-1,	// 415=PPC64_RLDIMI
	-1,	// 416=PPC64_RLDIMIr
	-1,	// 417=PPC64_CMP
	-1,	// 418=PPC64_CMPI
	-1,	// 419=PPC64_CMPL
	-1,	// 420=PPC64_CMPLI
	-1,	// 421=PPC64_FCFID
	-1,	// 422=PPC64_FCTIDZ
	-1,	// 423=PPC64_DIVD
	-1,	// 424=PPC64_MULLD
	-1,	// 425=PPC64_LD
	-1,	// 426=PPC64_LDX
	-1,	// 427=PPC64_STD
	-1,	// 428=PPC64_STDX
	-1,	// 429=PPC64_TD
	-1,	// 430=PPC64_TDI
	-1,	// 431=PPC_CNTLZAddr
	-1,	// 432=PPC_SRAAddrI
	-1,	// 433=PPC_SRAddrI
	-1,	// 434=PPC_LInt
	-1,	// 435=PPC_LIntUX
	-1,	// 436=PPC_LIntX
	-1,	// 437=PPC_LAddr
	-1,	// 438=PPC_LAddrU
	-1,	// 439=PPC_LAddrUX
	-1,	// 440=PPC_LAddrX
	-1,	// 441=PPC_LAddrARX
	-1,	// 442=PPC_STAddr
	-1,	// 443=PPC_STAddrX
	-1,	// 444=PPC_STAddrCXr
	-1,	// 445=PPC_STAddrU
	-1,	// 446=PPC_STAddrUX
	-1,	// 447=PPC_TAddr
	-1,	// 448=MIR_END
};*/

static final char[][] decode = {null,
	{// stm_NT
	0,
	1,
	14,
	15,
	16,
	17,
	18,
	19,
	20,
	21,
	22,
	25,
	27,
	28,
	29,
	30,
	31,
	32,
	33,
	34,
	35,
	36,
	37,
	38,
	39,
	40,
	41,
	162,
	163,
	164,
	165,
	166,
	167,
	168,
	169,
	170,
	171,
	172,
	173,
	174,
	175,
	176,
	177,
	178,
	179,
	180,
	181,
	182,
	183,
	184,
	185,
	186,
	187,
	188,
	189,
	190,
	191,
	192,
	193,
	194,
	195,
	196,
	197,
	198,
	199,
	200,
	201,
	202,
	203,
	204,
	205,
	206,
	207,
	218,
	239,
	240,
	241,
	242,
	243,
	244,
	245,
	246,
	247,
	248,
	249,
	250,
	251,
	252,
	253,
	265,
	266,
	267,
},
	{// r_NT
	0,
	2,
	3,
	4,
	5,
	23,
	24,
	26,
	42,
	43,
	44,
	47,
	48,
	55,
	56,
	57,
	58,
	59,
	60,
	61,
	62,
	63,
	64,
	65,
	66,
	67,
	68,
	69,
	70,
	71,
	83,
	86,
	87,
	90,
	91,
	92,
	93,
	94,
	95,
	96,
	97,
	98,
	99,
	100,
	101,
	102,
	103,
	104,
	105,
	106,
	107,
	108,
	109,
	110,
	111,
	112,
	113,
	114,
	115,
	116,
	117,
	118,
	119,
	120,
	121,
	122,
	123,
	127,
	128,
	129,
	130,
	131,
	132,
	133,
	134,
	135,
	139,
	140,
	151,
	152,
	153,
	154,
	155,
	156,
	208,
	209,
	210,
	211,
	212,
	213,
	214,
	215,
	216,
	217,
	219,
	220,
	221,
	222,
	223,
	224,
	225,
	226,
	227,
	228,
	229,
	230,
	231,
	232,
	233,
	234,
	235,
	236,
	237,
	238,
	255,
	256,
	257,
	258,
	259,
	260,
	261,
	262,
	263,
	264,
	268,
	269,
},
	{// czr_NT
	0,
	84,
	254,
},
	{// rs_NT
	0,
	6,
	75,
	76,
	124,
	126,
	136,
	137,
	138,
	141,
	142,
	147,
	148,
	157,
	158,
	159,
	160,
	161,
},
	{// rz_NT
	0,
	7,
	72,
	73,
	74,
	79,
},
	{// rp_NT
	0,
	77,
	78,
	80,
	81,
	82,
	85,
	88,
	89,
	125,
	143,
	144,
	145,
	146,
	149,
	150,
},
	{// any_NT
	0,
	8,
	9,
	10,
	11,
	12,
	13,
},
	{// boolcmp_NT
	0,
	45,
	46,
	49,
	50,
	51,
	52,
	53,
	54,
},
};

static void closure_r(BURS_TreeNode p, int c) {
	if (c < p.cost_any) {
		p.cost_any = (char)(c);
		p.word0 = (p.word0 & 0x8FFFFFFF) | 0x20000000; // p.any = 2
	}
	if (c < p.cost_stm) {
		p.cost_stm = (char)(c);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0x1; // p.stm = 1
	}
}

static void closure_czr(BURS_TreeNode p, int c) {
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x100; // p.r = 2
		closure_r(p, c);
	}
}

static void closure_rs(BURS_TreeNode p, int c) {
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x180; // p.r = 3
		closure_r(p, c);
	}
}

static void closure_rz(BURS_TreeNode p, int c) {
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x200; // p.r = 4
		closure_r(p, c);
	}
}

static void closure_rp(BURS_TreeNode p, int c) {
	if (c < p.cost_rz) {
		p.cost_rz = (char)(c);
		p.word0 = (p.word0 & 0xFF1FFFFF) | 0x200000; // p.rz = 1
		closure_rz(p, c);
	}
	if (c < p.cost_rs) {
		p.cost_rs = (char)(c);
		p.word0 = (p.word0 & 0xFFE0FFFF) | 0x10000; // p.rs = 1
		closure_rs(p, c);
	}
}

private void label_GET_CAUGHT_EXCEPTION(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	// r: GET_CAUGHT_EXCEPTION
	if (11 < p.cost_r) {
		p.cost_r = (char)(11);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x380; // p.r = 7
		closure_r(p, 11);
	}
}

private void label_SET_CAUGHT_EXCEPTION(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// stm: SET_CAUGHT_EXCEPTION(r)
	c = STATE(lchild).cost_r + 11;
	if (c < p.cost_stm) {
		p.cost_stm = (char)(c);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0xC; // p.stm = 12
	}
}

private void label_IG_PATCH_POINT(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	// stm: IG_PATCH_POINT
	if (10 < p.cost_stm) {
		p.cost_stm = (char)(10);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0x3; // p.stm = 3
	}
}

private void label_INT_IFCMP(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// stm: INT_IFCMP(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 20;
	if (c < p.cost_stm) {
		p.cost_stm = (char)(c);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0x2F; // p.stm = 47
	}
	if (	// stm: INT_IFCMP(r,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + 20;
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x30; // p.stm = 48
		}
	}
	if (	// stm: INT_IFCMP(INT_2BYTE(r),INT_CONSTANT)
		lchild.getOpcode() == INT_2BYTE_opcode && 
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + ((!IfCmp.getCond(P(p)).isUNSIGNED())&&ZERO(IfCmp.getVal2(P(p)))?20:INFINITE);
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x31; // p.stm = 49
		}
	}
	if (	// stm: INT_IFCMP(INT_2SHORT(r),INT_CONSTANT)
		lchild.getOpcode() == INT_2SHORT_opcode && 
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + ((!IfCmp.getCond(P(p)).isUNSIGNED())&&ZERO(IfCmp.getVal2(P(p)))?20:INFINITE);
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x32; // p.stm = 50
		}
	}
	if (	// stm: INT_IFCMP(INT_USHR(r,r),INT_CONSTANT)
		lchild.getOpcode() == INT_USHR_opcode && 
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + STATE(lchild.child2).cost_r + ((!IfCmp.getCond(P(p)).isUNSIGNED())&&ZERO(IfCmp.getVal2(P(p)))?20:INFINITE);
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x33; // p.stm = 51
		}
	}
	if (	// stm: INT_IFCMP(INT_SHL(r,r),INT_CONSTANT)
		lchild.getOpcode() == INT_SHL_opcode && 
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + STATE(lchild.child2).cost_r + ((!IfCmp.getCond(P(p)).isUNSIGNED())&&ZERO(IfCmp.getVal2(P(p)))?20:INFINITE);
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x34; // p.stm = 52
		}
	}
	if (	// stm: INT_IFCMP(INT_SHR(r,r),INT_CONSTANT)
		lchild.getOpcode() == INT_SHR_opcode && 
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + STATE(lchild.child2).cost_r + ((!IfCmp.getCond(P(p)).isUNSIGNED())&&ZERO(IfCmp.getVal2(P(p)))?20:INFINITE);
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x35; // p.stm = 53
		}
	}
	if (	// stm: INT_IFCMP(INT_USHR(r,INT_CONSTANT),INT_CONSTANT)
		lchild.getOpcode() == INT_USHR_opcode && 
		lchild.child2.getOpcode() == INT_CONSTANT_opcode && 
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + ((!IfCmp.getCond(P(p)).isUNSIGNED())&&ZERO(IfCmp.getVal2(P(p)))?20:INFINITE);
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x36; // p.stm = 54
		}
	}
	if (	// stm: INT_IFCMP(INT_SHL(r,INT_CONSTANT),INT_CONSTANT)
		lchild.getOpcode() == INT_SHL_opcode && 
		lchild.child2.getOpcode() == INT_CONSTANT_opcode && 
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + ((!IfCmp.getCond(P(p)).isUNSIGNED())&&ZERO(IfCmp.getVal2(P(p)))?20:INFINITE);
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x37; // p.stm = 55
		}
	}
	if (	// stm: INT_IFCMP(INT_SHR(r,INT_CONSTANT),INT_CONSTANT)
		lchild.getOpcode() == INT_SHR_opcode && 
		lchild.child2.getOpcode() == INT_CONSTANT_opcode && 
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + ((!IfCmp.getCond(P(p)).isUNSIGNED())&&ZERO(IfCmp.getVal2(P(p)))?20:INFINITE);
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x38; // p.stm = 56
		}
	}
	if (	// stm: INT_IFCMP(REF_AND(r,INT_CONSTANT),INT_CONSTANT)
		lchild.getOpcode() == REF_AND_opcode && 
		lchild.child2.getOpcode() == INT_CONSTANT_opcode && 
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + ((!IfCmp.getCond(P(p)).isUNSIGNED())&&ZERO(IfCmp.getVal2(P(p)))?20:INFINITE);
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x39; // p.stm = 57
		}
	}
	if (	// stm: INT_IFCMP(boolcmp,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_boolcmp + (VR(p) == 0 && IfCmp.getCond(P(p)).isNOT_EQUAL()?20:INFINITE);
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x3A; // p.stm = 58
		}
	}
	if (	// stm: INT_IFCMP(boolcmp,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_boolcmp + (VR(p) == 0 && IfCmp.getCond(P(p)).isEQUAL()?20:INFINITE);
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x3B; // p.stm = 59
		}
	}
	if (	// stm: INT_IFCMP(boolcmp,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_boolcmp + (VR(p) == 1 && IfCmp.getCond(P(p)).isEQUAL()?20:INFINITE);
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x3C; // p.stm = 60
		}
	}
	if (	// stm: INT_IFCMP(boolcmp,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_boolcmp + (VR(p) == 1 && (IfCmp.getCond(P(p)).isNOT_EQUAL())?26:INFINITE);
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x3D; // p.stm = 61
		}
	}
	if (	// stm: INT_IFCMP(ATTEMPT_INT(r,r),INT_CONSTANT)
		lchild.getOpcode() == ATTEMPT_INT_opcode && 
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + STATE(lchild.child2).cost_r + ((!IfCmp.getCond(P(p)).isUNSIGNED())&&ZERO(IfCmp.getVal2(P(p)))?20:INFINITE);
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x4C; // p.stm = 76
		}
	}
	if (	// stm: INT_IFCMP(ATTEMPT_ADDR(r,r),INT_CONSTANT)
		lchild.getOpcode() == ATTEMPT_ADDR_opcode && 
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + STATE(lchild.child2).cost_r + ((!IfCmp.getCond(P(p)).isUNSIGNED())&&ZERO(IfCmp.getVal2(P(p)))?20:INFINITE);
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x4D; // p.stm = 77
		}
	}
	if (	// stm: INT_IFCMP(REF_NEG(r),INT_CONSTANT)
		lchild.getOpcode() == REF_NEG_opcode && 
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + ((!IfCmp.getCond(P(p)).isUNSIGNED())&&ZERO(IfCmp.getVal2(P(p)))?20:INFINITE);
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x4E; // p.stm = 78
		}
	}
	if (	// stm: INT_IFCMP(REF_NOT(r),INT_CONSTANT)
		lchild.getOpcode() == REF_NOT_opcode && 
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + ((!IfCmp.getCond(P(p)).isUNSIGNED())&&ZERO(IfCmp.getVal2(P(p)))?20:INFINITE);
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x4F; // p.stm = 79
		}
	}
	if (	// stm: INT_IFCMP(REF_ADD(r,r),INT_CONSTANT)
		lchild.getOpcode() == REF_ADD_opcode && 
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + STATE(lchild.child2).cost_r + ((!IfCmp.getCond(P(p)).isUNSIGNED())&&ZERO(IfCmp.getVal2(P(p)))?20:INFINITE);
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x50; // p.stm = 80
		}
	}
	if (	// stm: INT_IFCMP(REF_AND(r,r),INT_CONSTANT)
		lchild.getOpcode() == REF_AND_opcode && 
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + STATE(lchild.child2).cost_r + ((!IfCmp.getCond(P(p)).isUNSIGNED())&&ZERO(IfCmp.getVal2(P(p)))?20:INFINITE);
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x51; // p.stm = 81
		}
	}
	if (	// stm: INT_IFCMP(REF_OR(r,r),INT_CONSTANT)
		lchild.getOpcode() == REF_OR_opcode && 
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + STATE(lchild.child2).cost_r + ((!IfCmp.getCond(P(p)).isUNSIGNED())&&ZERO(IfCmp.getVal2(P(p)))?20:INFINITE);
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x52; // p.stm = 82
		}
	}
	if (	// stm: INT_IFCMP(REF_XOR(r,r),INT_CONSTANT)
		lchild.getOpcode() == REF_XOR_opcode && 
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + STATE(lchild.child2).cost_r + ((!IfCmp.getCond(P(p)).isUNSIGNED())&&ZERO(IfCmp.getVal2(P(p)))?20:INFINITE);
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x53; // p.stm = 83
		}
	}
	if (	// stm: INT_IFCMP(REF_AND(r,REF_MOVE(INT_CONSTANT)),INT_CONSTANT)
		lchild.getOpcode() == REF_AND_opcode && 
		lchild.child2.getOpcode() == REF_MOVE_opcode && 
		lchild.child2.child1.getOpcode() == INT_CONSTANT_opcode && 
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + ((!IfCmp.getCond(P(p)).isUNSIGNED())&&ZERO(IfCmp.getVal2(P(p)))&&U16(IV(Move.getVal(PLR(p))))?20:INFINITE);
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x54; // p.stm = 84
		}
	}
	if (	// stm: INT_IFCMP(REF_AND(r,REF_MOVE(INT_CONSTANT)),INT_CONSTANT)
		lchild.getOpcode() == REF_AND_opcode && 
		lchild.child2.getOpcode() == REF_MOVE_opcode && 
		lchild.child2.child1.getOpcode() == INT_CONSTANT_opcode && 
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + ((!IfCmp.getCond(P(p)).isUNSIGNED())&&ZERO(IfCmp.getVal2(P(p)))&&MASK(IV(Move.getVal(PLR(p))))?20:INFINITE);
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x55; // p.stm = 85
		}
	}
	if (	// stm: INT_IFCMP(REF_ADD(r,INT_CONSTANT),INT_CONSTANT)
		lchild.getOpcode() == REF_ADD_opcode && 
		lchild.child2.getOpcode() == INT_CONSTANT_opcode && 
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + ((!IfCmp.getCond(P(p)).isUNSIGNED())&&ZERO(IfCmp.getVal2(P(p)))?20:INFINITE);
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x56; // p.stm = 86
		}
	}
	if (	// stm: INT_IFCMP(REF_AND(r,REF_NOT(r)),INT_CONSTANT)
		lchild.getOpcode() == REF_AND_opcode && 
		lchild.child2.getOpcode() == REF_NOT_opcode && 
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + STATE(lchild.child2.child1).cost_r + ((!IfCmp.getCond(P(p)).isUNSIGNED())&&ZERO(IfCmp.getVal2(P(p)))?20:INFINITE);
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x57; // p.stm = 87
		}
	}
	if (	// stm: INT_IFCMP(REF_OR(r,REF_NOT(r)),INT_CONSTANT)
		lchild.getOpcode() == REF_OR_opcode && 
		lchild.child2.getOpcode() == REF_NOT_opcode && 
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + STATE(lchild.child2.child1).cost_r + ((!IfCmp.getCond(P(p)).isUNSIGNED())&&ZERO(IfCmp.getVal2(P(p)))?20:INFINITE);
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x58; // p.stm = 88
		}
	}
}

private void label_INT_IFCMP2(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// stm: INT_IFCMP2(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 20;
	if (c < p.cost_stm) {
		p.cost_stm = (char)(c);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0x3E; // p.stm = 62
	}
	if (	// stm: INT_IFCMP2(r,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + 20;
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x3F; // p.stm = 63
		}
	}
}

private void label_LONG_IFCMP(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// stm: LONG_IFCMP(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 30;
	if (c < p.cost_stm) {
		p.cost_stm = (char)(c);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0x4B; // p.stm = 75
	}
}

private void label_FLOAT_IFCMP(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// stm: FLOAT_IFCMP(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 20;
	if (c < p.cost_stm) {
		p.cost_stm = (char)(c);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0x40; // p.stm = 64
	}
}

private void label_DOUBLE_IFCMP(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// stm: DOUBLE_IFCMP(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 20;
	if (c < p.cost_stm) {
		p.cost_stm = (char)(c);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0x41; // p.stm = 65
	}
}

private void label_UNINT_BEGIN(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	// stm: UNINT_BEGIN
	if (10 < p.cost_stm) {
		p.cost_stm = (char)(10);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0x4; // p.stm = 4
	}
}

private void label_UNINT_END(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	// stm: UNINT_END
	if (10 < p.cost_stm) {
		p.cost_stm = (char)(10);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0x5; // p.stm = 5
	}
}

private void label_FENCE(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	// stm: FENCE
	if (11 < p.cost_stm) {
		p.cost_stm = (char)(11);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0xD; // p.stm = 13
	}
}

private void label_READ_CEILING(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	// stm: READ_CEILING
	if (11 < p.cost_stm) {
		p.cost_stm = (char)(11);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0xF; // p.stm = 15
	}
}

private void label_WRITE_FLOOR(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	// stm: WRITE_FLOOR
	if (11 < p.cost_stm) {
		p.cost_stm = (char)(11);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0xE; // p.stm = 14
	}
}

private void label_NOP(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	// stm: NOP
	if (10 < p.cost_stm) {
		p.cost_stm = (char)(10);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0xA; // p.stm = 10
	}
}

private void label_LONG_MOVE(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	if (	// r: LONG_MOVE(LONG_CONSTANT)
		lchild.getOpcode() == LONG_CONSTANT_opcode 
	) {
		c = 40;
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x3800; // p.r = 112
			closure_r(p, c);
		}
	}
	// r: LONG_MOVE(r)
	c = STATE(lchild).cost_r + 22;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x3880; // p.r = 113
		closure_r(p, c);
	}
}

private void label_FLOAT_MOVE(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// r: FLOAT_MOVE(r)
	c = STATE(lchild).cost_r + 10;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x2600; // p.r = 76
		closure_r(p, c);
	}
}

private void label_DOUBLE_MOVE(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// r: DOUBLE_MOVE(r)
	c = STATE(lchild).cost_r + 10;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x2680; // p.r = 77
		closure_r(p, c);
	}
}

private void label_REF_MOVE(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// r: REF_MOVE(r)
	c = STATE(lchild).cost_r + 11;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x2580; // p.r = 75
		closure_r(p, c);
	}
	if (	// rs: REF_MOVE(INT_CONSTANT)
		lchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = (SI16(IV(Move.getVal(P(p))))?11:INFINITE);
		if (c < p.cost_rs) {
			p.cost_rs = (char)(c);
			p.word0 = (p.word0 & 0xFFE0FFFF) | 0x60000; // p.rs = 6
			closure_rs(p, c);
		}
	}
	if (	// rs: REF_MOVE(INT_CONSTANT)
		lchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = (U16(IV(Move.getVal(P(p))))?11:INFINITE);
		if (c < p.cost_rs) {
			p.cost_rs = (char)(c);
			p.word0 = (p.word0 & 0xFFE0FFFF) | 0x70000; // p.rs = 7
			closure_rs(p, c);
		}
	}
	if (	// rs: REF_MOVE(INT_CONSTANT)
		lchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = 22;
		if (c < p.cost_rs) {
			p.cost_rs = (char)(c);
			p.word0 = (p.word0 & 0xFFE0FFFF) | 0x80000; // p.rs = 8
			closure_rs(p, c);
		}
	}
	if (	// r: REF_MOVE(ADDRESS_CONSTANT)
		lchild.getOpcode() == ADDRESS_CONSTANT_opcode 
	) {
		c = (SI16(AV(Move.getVal(P(p))))?11:INFINITE);
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x3B00; // p.r = 118
			closure_r(p, c);
		}
	}
	if (	// r: REF_MOVE(ADDRESS_CONSTANT)
		lchild.getOpcode() == ADDRESS_CONSTANT_opcode 
	) {
		c = (U16(AV(Move.getVal(P(p))))?11:INFINITE);
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x3B80; // p.r = 119
			closure_r(p, c);
		}
	}
	if (	// r: REF_MOVE(ADDRESS_CONSTANT)
		lchild.getOpcode() == ADDRESS_CONSTANT_opcode 
	) {
		c = 22;
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x3C00; // p.r = 120
			closure_r(p, c);
		}
	}
}

private void label_GUARD_MOVE(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	// r: GUARD_MOVE
	if (11 < p.cost_r) {
		p.cost_r = (char)(11);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x280; // p.r = 5
		closure_r(p, 11);
	}
}

private void label_GUARD_COMBINE(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	// r: GUARD_COMBINE
	if (11 < p.cost_r) {
		p.cost_r = (char)(11);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x300; // p.r = 6
		closure_r(p, 11);
	}
}

private void label_REF_ADD(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	if (	// r: REF_ADD(r,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + 11;
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x880; // p.r = 17
			closure_r(p, c);
		}
	}
	// r: REF_ADD(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 11;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x900; // p.r = 18
		closure_r(p, c);
	}
	if (	// r: REF_ADD(r,REF_MOVE(INT_CONSTANT))
		rchild.getOpcode() == REF_MOVE_opcode && 
		rchild.child1.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + 20;
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x980; // p.r = 19
			closure_r(p, c);
		}
	}
	if (	// r: REF_ADD(r,REF_MOVE(INT_CONSTANT))
		rchild.getOpcode() == REF_MOVE_opcode && 
		rchild.child1.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + (U16(IV(Move.getVal(PR(p))))?10:INFINITE);
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0xA00; // p.r = 20
			closure_r(p, c);
		}
	}
}

private void label_LONG_ADD(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// r: LONG_ADD(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 20;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x2F00; // p.r = 94
		closure_r(p, c);
	}
}

private void label_FLOAT_ADD(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// r: FLOAT_ADD(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 10;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x1580; // p.r = 43
		closure_r(p, c);
	}
	if (	// r: FLOAT_ADD(FLOAT_MUL(r,r),r)
		lchild.getOpcode() == FLOAT_MUL_opcode 
	) {
		c = STATE(lchild.child1).cost_r + STATE(lchild.child2).cost_r + STATE(rchild).cost_r + (burs.ir.strictFP(P(p),PL(p))?INFINITE:10);
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x1B80; // p.r = 55
			closure_r(p, c);
		}
	}
	if (	// r: FLOAT_ADD(r,FLOAT_MUL(r,r))
		rchild.getOpcode() == FLOAT_MUL_opcode 
	) {
		c = STATE(lchild).cost_r + STATE(rchild.child1).cost_r + STATE(rchild.child2).cost_r + (burs.ir.strictFP(P(p),PR(p))?INFINITE:10);
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x1C80; // p.r = 57
			closure_r(p, c);
		}
	}
}

private void label_DOUBLE_ADD(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// r: DOUBLE_ADD(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 10;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x1600; // p.r = 44
		closure_r(p, c);
	}
	if (	// r: DOUBLE_ADD(DOUBLE_MUL(r,r),r)
		lchild.getOpcode() == DOUBLE_MUL_opcode 
	) {
		c = STATE(lchild.child1).cost_r + STATE(lchild.child2).cost_r + STATE(rchild).cost_r + (burs.ir.strictFP(P(p),PL(p))?INFINITE:10);
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x1C00; // p.r = 56
			closure_r(p, c);
		}
	}
	if (	// r: DOUBLE_ADD(r,DOUBLE_MUL(r,r))
		rchild.getOpcode() == DOUBLE_MUL_opcode 
	) {
		c = STATE(lchild).cost_r + STATE(rchild.child1).cost_r + STATE(rchild.child2).cost_r + (burs.ir.strictFP(P(p),PR(p))?INFINITE:10);
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x1D00; // p.r = 58
			closure_r(p, c);
		}
	}
}

private void label_REF_SUB(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// r: REF_SUB(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 11;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0xA80; // p.r = 21
		closure_r(p, c);
	}
	if (	// r: REF_SUB(INT_CONSTANT,r)
		lchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(rchild).cost_r + (SI16(IV(Binary.getVal1(P(p))))?11:INFINITE);
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0xB00; // p.r = 22
			closure_r(p, c);
		}
	}
}

private void label_LONG_SUB(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// r: LONG_SUB(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 20;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x2F80; // p.r = 95
		closure_r(p, c);
	}
}

private void label_FLOAT_SUB(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// r: FLOAT_SUB(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 10;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x1780; // p.r = 47
		closure_r(p, c);
	}
	if (	// r: FLOAT_SUB(FLOAT_MUL(r,r),r)
		lchild.getOpcode() == FLOAT_MUL_opcode 
	) {
		c = STATE(lchild.child1).cost_r + STATE(lchild.child2).cost_r + STATE(rchild).cost_r + (burs.ir.strictFP(P(p),PL(p))?INFINITE:10);
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x1D80; // p.r = 59
			closure_r(p, c);
		}
	}
}

private void label_DOUBLE_SUB(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// r: DOUBLE_SUB(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 10;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x1800; // p.r = 48
		closure_r(p, c);
	}
	if (	// r: DOUBLE_SUB(DOUBLE_MUL(r,r),r)
		lchild.getOpcode() == DOUBLE_MUL_opcode 
	) {
		c = STATE(lchild.child1).cost_r + STATE(lchild.child2).cost_r + STATE(rchild).cost_r + (burs.ir.strictFP(P(p),PL(p))?INFINITE:10);
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x1E00; // p.r = 60
			closure_r(p, c);
		}
	}
}

private void label_INT_MUL(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	if (	// r: INT_MUL(r,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + 11;
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0xB80; // p.r = 23
			closure_r(p, c);
		}
	}
	// r: INT_MUL(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 11;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0xC00; // p.r = 24
		closure_r(p, c);
	}
}

private void label_LONG_MUL(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// r: LONG_MUL(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 20;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x3000; // p.r = 96
		closure_r(p, c);
	}
}

private void label_FLOAT_MUL(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// r: FLOAT_MUL(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 10;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x1680; // p.r = 45
		closure_r(p, c);
	}
}

private void label_DOUBLE_MUL(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// r: DOUBLE_MUL(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 10;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x1700; // p.r = 46
		closure_r(p, c);
	}
}

private void label_INT_DIV(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// r: INT_DIV(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 20;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0xC80; // p.r = 25
		closure_r(p, c);
	}
	if (	// r: INT_DIV(r,REF_MOVE(INT_CONSTANT))
		rchild.getOpcode() == REF_MOVE_opcode && 
		rchild.child1.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + 20;
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0xD00; // p.r = 26
			closure_r(p, c);
		}
	}
}

private void label_FLOAT_DIV(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// r: FLOAT_DIV(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 10;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x1880; // p.r = 49
		closure_r(p, c);
	}
}

private void label_DOUBLE_DIV(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// r: DOUBLE_DIV(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 10;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x1900; // p.r = 50
		closure_r(p, c);
	}
}

private void label_INT_REM(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// r: INT_REM(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 20;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0xD80; // p.r = 27
		closure_r(p, c);
	}
	if (	// r: INT_REM(r,REF_MOVE(INT_CONSTANT))
		rchild.getOpcode() == REF_MOVE_opcode && 
		rchild.child1.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + 20;
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0xE00; // p.r = 28
			closure_r(p, c);
		}
	}
}

private void label_REF_NEG(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// r: REF_NEG(r)
	c = STATE(lchild).cost_r + 11;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0xE80; // p.r = 29
		closure_r(p, c);
	}
}

private void label_LONG_NEG(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// r: LONG_NEG(r)
	c = STATE(lchild).cost_r + 20;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x3080; // p.r = 97
		closure_r(p, c);
	}
}

private void label_FLOAT_NEG(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// r: FLOAT_NEG(r)
	c = STATE(lchild).cost_r + 10;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x1980; // p.r = 51
		closure_r(p, c);
	}
	if (	// r: FLOAT_NEG(FLOAT_ADD(FLOAT_MUL(r,r),r))
		lchild.getOpcode() == FLOAT_ADD_opcode && 
		lchild.child1.getOpcode() == FLOAT_MUL_opcode 
	) {
		c = STATE(lchild.child1.child1).cost_r + STATE(lchild.child1.child2).cost_r + STATE(lchild.child2).cost_r + (burs.ir.strictFP(P(p),PL(p),PLL(p))?INFINITE:10);
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x1E80; // p.r = 61
			closure_r(p, c);
		}
	}
	if (	// r: FLOAT_NEG(FLOAT_ADD(r,FLOAT_MUL(r,r)))
		lchild.getOpcode() == FLOAT_ADD_opcode && 
		lchild.child2.getOpcode() == FLOAT_MUL_opcode 
	) {
		c = STATE(lchild.child1).cost_r + STATE(lchild.child2.child1).cost_r + STATE(lchild.child2.child2).cost_r + (burs.ir.strictFP(P(p),PL(p),PLR(p))?INFINITE:10);
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x1F80; // p.r = 63
			closure_r(p, c);
		}
	}
	if (	// r: FLOAT_NEG(FLOAT_SUB(FLOAT_MUL(r,r),r))
		lchild.getOpcode() == FLOAT_SUB_opcode && 
		lchild.child1.getOpcode() == FLOAT_MUL_opcode 
	) {
		c = STATE(lchild.child1.child1).cost_r + STATE(lchild.child1.child2).cost_r + STATE(lchild.child2).cost_r + (burs.ir.strictFP(P(p),PL(p),PLL(p))?INFINITE:10);
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x2080; // p.r = 65
			closure_r(p, c);
		}
	}
}

private void label_DOUBLE_NEG(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// r: DOUBLE_NEG(r)
	c = STATE(lchild).cost_r + 10;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x1A00; // p.r = 52
		closure_r(p, c);
	}
	if (	// r: DOUBLE_NEG(DOUBLE_ADD(DOUBLE_MUL(r,r),r))
		lchild.getOpcode() == DOUBLE_ADD_opcode && 
		lchild.child1.getOpcode() == DOUBLE_MUL_opcode 
	) {
		c = STATE(lchild.child1.child1).cost_r + STATE(lchild.child1.child2).cost_r + STATE(lchild.child2).cost_r + (burs.ir.strictFP(P(p),PL(p),PLL(p))?INFINITE:10);
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x1F00; // p.r = 62
			closure_r(p, c);
		}
	}
	if (	// r: DOUBLE_NEG(DOUBLE_ADD(r,DOUBLE_MUL(r,r)))
		lchild.getOpcode() == DOUBLE_ADD_opcode && 
		lchild.child2.getOpcode() == DOUBLE_MUL_opcode 
	) {
		c = STATE(lchild.child1).cost_r + STATE(lchild.child2.child1).cost_r + STATE(lchild.child2.child2).cost_r + (burs.ir.strictFP(P(p),PL(p),PLR(p))?INFINITE:10);
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x2000; // p.r = 64
			closure_r(p, c);
		}
	}
	if (	// r: DOUBLE_NEG(DOUBLE_SUB(DOUBLE_MUL(r,r),r))
		lchild.getOpcode() == DOUBLE_SUB_opcode && 
		lchild.child1.getOpcode() == DOUBLE_MUL_opcode 
	) {
		c = STATE(lchild.child1.child1).cost_r + STATE(lchild.child1.child2).cost_r + STATE(lchild.child2).cost_r + (burs.ir.strictFP(P(p),PL(p),PLL(p))?INFINITE:10);
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x2100; // p.r = 66
			closure_r(p, c);
		}
	}
}

private void label_FLOAT_SQRT(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// r: FLOAT_SQRT(r)
	c = STATE(lchild).cost_r + 10;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x1A80; // p.r = 53
		closure_r(p, c);
	}
}

private void label_DOUBLE_SQRT(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// r: DOUBLE_SQRT(r)
	c = STATE(lchild).cost_r + 10;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x1B00; // p.r = 54
		closure_r(p, c);
	}
}

private void label_INT_SHL(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	if (	// rz: INT_SHL(r,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + 10;
		if (c < p.cost_rz) {
			p.cost_rz = (char)(c);
			p.word0 = (p.word0 & 0xFF1FFFFF) | 0x400000; // p.rz = 2
			closure_rz(p, c);
		}
	}
	// rz: INT_SHL(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 20;
	if (c < p.cost_rz) {
		p.cost_rz = (char)(c);
		p.word0 = (p.word0 & 0xFF1FFFFF) | 0x600000; // p.rz = 3
		closure_rz(p, c);
	}
	if (	// rz: INT_SHL(INT_USHR(r,INT_CONSTANT),INT_CONSTANT)
		lchild.getOpcode() == INT_USHR_opcode && 
		lchild.child2.getOpcode() == INT_CONSTANT_opcode && 
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + 10;
		if (c < p.cost_rz) {
			p.cost_rz = (char)(c);
			p.word0 = (p.word0 & 0xFF1FFFFF) | 0x800000; // p.rz = 4
			closure_rz(p, c);
		}
	}
}

private void label_LONG_SHL(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// r: LONG_SHL(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 20;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x3100; // p.r = 98
		closure_r(p, c);
	}
	if (	// r: LONG_SHL(r,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + 20;
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x3180; // p.r = 99
			closure_r(p, c);
		}
	}
}

private void label_INT_SHR(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	if (	// rs: INT_SHR(r,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + 10;
		if (c < p.cost_rs) {
			p.cost_rs = (char)(c);
			p.word0 = (p.word0 & 0xFFE0FFFF) | 0x20000; // p.rs = 2
			closure_rs(p, c);
		}
	}
	// rs: INT_SHR(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 20;
	if (c < p.cost_rs) {
		p.cost_rs = (char)(c);
		p.word0 = (p.word0 & 0xFFE0FFFF) | 0x30000; // p.rs = 3
		closure_rs(p, c);
	}
	if (	// rp: INT_SHR(REF_AND(r,INT_CONSTANT),INT_CONSTANT)
		lchild.getOpcode() == REF_AND_opcode && 
		lchild.child2.getOpcode() == INT_CONSTANT_opcode && 
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + (POSITIVE_MASK(IV(Binary.getVal2(PL(p))))?10:INFINITE);
		if (c < p.cost_rp) {
			p.cost_rp = (char)(c);
			p.word0 = (p.word0 & 0xF0FFFFFF) | 0x1000000; // p.rp = 1
			closure_rp(p, c);
		}
	}
}

private void label_LONG_SHR(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// r: LONG_SHR(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 20;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x3200; // p.r = 100
		closure_r(p, c);
	}
	if (	// r: LONG_SHR(r,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + 20;
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x3280; // p.r = 101
			closure_r(p, c);
		}
	}
}

private void label_INT_USHR(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	if (	// rp: INT_USHR(r,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + 10;
		if (c < p.cost_rp) {
			p.cost_rp = (char)(c);
			p.word0 = (p.word0 & 0xF0FFFFFF) | 0x2000000; // p.rp = 2
			closure_rp(p, c);
		}
	}
	// rz: INT_USHR(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 20;
	if (c < p.cost_rz) {
		p.cost_rz = (char)(c);
		p.word0 = (p.word0 & 0xFF1FFFFF) | 0xA00000; // p.rz = 5
		closure_rz(p, c);
	}
	if (	// rp: INT_USHR(REF_AND(r,INT_CONSTANT),INT_CONSTANT)
		lchild.getOpcode() == REF_AND_opcode && 
		lchild.child2.getOpcode() == INT_CONSTANT_opcode && 
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + (POSITIVE_MASK(IV(Binary.getVal2(PL(p))))?10:INFINITE);
		if (c < p.cost_rp) {
			p.cost_rp = (char)(c);
			p.word0 = (p.word0 & 0xF0FFFFFF) | 0x3000000; // p.rp = 3
			closure_rp(p, c);
		}
	}
	if (	// rp: INT_USHR(REF_AND(r,REF_MOVE(INT_CONSTANT)),INT_CONSTANT)
		lchild.getOpcode() == REF_AND_opcode && 
		lchild.child2.getOpcode() == REF_MOVE_opcode && 
		lchild.child2.child1.getOpcode() == INT_CONSTANT_opcode && 
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + (POSITIVE_MASK(IV(Move.getVal(PLR(p))))?10:INFINITE);
		if (c < p.cost_rp) {
			p.cost_rp = (char)(c);
			p.word0 = (p.word0 & 0xF0FFFFFF) | 0x4000000; // p.rp = 4
			closure_rp(p, c);
		}
	}
	if (	// rp: INT_USHR(INT_SHL(r,INT_CONSTANT),INT_CONSTANT)
		lchild.getOpcode() == INT_SHL_opcode && 
		lchild.child2.getOpcode() == INT_CONSTANT_opcode && 
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + 10;
		if (c < p.cost_rp) {
			p.cost_rp = (char)(c);
			p.word0 = (p.word0 & 0xF0FFFFFF) | 0x5000000; // p.rp = 5
			closure_rp(p, c);
		}
	}
}

private void label_LONG_USHR(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// r: LONG_USHR(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 20;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x3300; // p.r = 102
		closure_r(p, c);
	}
	if (	// r: LONG_USHR(r,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + 20;
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x3380; // p.r = 103
			closure_r(p, c);
		}
	}
}

private void label_REF_AND(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// r: REF_AND(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 10;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0xF00; // p.r = 30
		closure_r(p, c);
	}
	if (	// czr: REF_AND(r,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + 11;
		if (c < p.cost_czr) {
			p.cost_czr = (char)(c);
			p.word0 = (p.word0 & 0xFFFF3FFF) | 0x4000; // p.czr = 1
			closure_czr(p, c);
		}
	}
	if (	// rp: REF_AND(r,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + (MASK(IV(Binary.getVal2(P(p))))?10:INFINITE);
		if (c < p.cost_rp) {
			p.cost_rp = (char)(c);
			p.word0 = (p.word0 & 0xF0FFFFFF) | 0x6000000; // p.rp = 6
			closure_rp(p, c);
		}
	}
	if (	// r: REF_AND(REF_NOT(r),REF_NOT(r))
		lchild.getOpcode() == REF_NOT_opcode && 
		rchild.getOpcode() == REF_NOT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + STATE(rchild.child1).cost_r + 10;
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0xF80; // p.r = 31
			closure_r(p, c);
		}
	}
	if (	// r: REF_AND(r,REF_NOT(r))
		rchild.getOpcode() == REF_NOT_opcode 
	) {
		c = STATE(lchild).cost_r + STATE(rchild.child1).cost_r + 10;
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x1000; // p.r = 32
			closure_r(p, c);
		}
	}
	if (	// rp: REF_AND(INT_USHR(r,INT_CONSTANT),INT_CONSTANT)
		lchild.getOpcode() == INT_USHR_opcode && 
		lchild.child2.getOpcode() == INT_CONSTANT_opcode && 
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + (POSITIVE_MASK(IV(Binary.getVal2(P(p))))?10:INFINITE);
		if (c < p.cost_rp) {
			p.cost_rp = (char)(c);
			p.word0 = (p.word0 & 0xF0FFFFFF) | 0x7000000; // p.rp = 7
			closure_rp(p, c);
		}
	}
	if (	// rp: REF_AND(INT_USHR(r,INT_CONSTANT),REF_MOVE(INT_CONSTANT))
		lchild.getOpcode() == INT_USHR_opcode && 
		lchild.child2.getOpcode() == INT_CONSTANT_opcode && 
		rchild.getOpcode() == REF_MOVE_opcode && 
		rchild.child1.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + (POSITIVE_MASK(IV(Move.getVal(PR(p))))?10:INFINITE);
		if (c < p.cost_rp) {
			p.cost_rp = (char)(c);
			p.word0 = (p.word0 & 0xF0FFFFFF) | 0x8000000; // p.rp = 8
			closure_rp(p, c);
		}
	}
	if (	// rp: REF_AND(BYTE_LOAD(r,r),INT_CONSTANT)
		lchild.getOpcode() == BYTE_LOAD_opcode && 
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + STATE(lchild.child2).cost_r + (VR(p) == 0xff ? 10 : INFINITE);
		if (c < p.cost_rp) {
			p.cost_rp = (char)(c);
			p.word0 = (p.word0 & 0xF0FFFFFF) | 0xA000000; // p.rp = 10
			closure_rp(p, c);
		}
	}
	if (	// rp: REF_AND(BYTE_LOAD(r,INT_CONSTANT),INT_CONSTANT)
		lchild.getOpcode() == BYTE_LOAD_opcode && 
		lchild.child2.getOpcode() == INT_CONSTANT_opcode && 
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + (VR(p) == 0xff ? 10 : INFINITE);
		if (c < p.cost_rp) {
			p.cost_rp = (char)(c);
			p.word0 = (p.word0 & 0xF0FFFFFF) | 0xB000000; // p.rp = 11
			closure_rp(p, c);
		}
	}
	if (	// czr: REF_AND(r,REF_MOVE(INT_CONSTANT))
		rchild.getOpcode() == REF_MOVE_opcode && 
		rchild.child1.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + (U16(IV(Move.getVal(PR(p))))?11:INFINITE);
		if (c < p.cost_czr) {
			p.cost_czr = (char)(c);
			p.word0 = (p.word0 & 0xFFFF3FFF) | 0x8000; // p.czr = 2
			closure_czr(p, c);
		}
	}
	if (	// r: REF_AND(r,REF_MOVE(INT_CONSTANT))
		rchild.getOpcode() == REF_MOVE_opcode && 
		rchild.child1.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + (MASK(IV(Move.getVal(PR(p))))?10:INFINITE);
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x3900; // p.r = 114
			closure_r(p, c);
		}
	}
}

private void label_LONG_AND(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// r: LONG_AND(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 20;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x3400; // p.r = 104
		closure_r(p, c);
	}
}

private void label_REF_OR(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// r: REF_OR(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 10;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x1080; // p.r = 33
		closure_r(p, c);
	}
	if (	// r: REF_OR(r,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + 10;
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x1100; // p.r = 34
			closure_r(p, c);
		}
	}
	if (	// r: REF_OR(REF_NOT(r),REF_NOT(r))
		lchild.getOpcode() == REF_NOT_opcode && 
		rchild.getOpcode() == REF_NOT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + STATE(rchild.child1).cost_r + 10;
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x1180; // p.r = 35
			closure_r(p, c);
		}
	}
	if (	// r: REF_OR(r,REF_NOT(r))
		rchild.getOpcode() == REF_NOT_opcode 
	) {
		c = STATE(lchild).cost_r + STATE(rchild.child1).cost_r + 10;
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x1200; // p.r = 36
			closure_r(p, c);
		}
	}
	if (	// r: REF_OR(r,REF_MOVE(INT_CONSTANT))
		rchild.getOpcode() == REF_MOVE_opcode && 
		rchild.child1.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + (U16(IV(Move.getVal(PR(p))))?10:INFINITE);
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x3980; // p.r = 115
			closure_r(p, c);
		}
	}
	if (	// r: REF_OR(r,REF_MOVE(INT_CONSTANT))
		rchild.getOpcode() == REF_MOVE_opcode && 
		rchild.child1.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + 20;
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x3A00; // p.r = 116
			closure_r(p, c);
		}
	}
}

private void label_LONG_OR(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// r: LONG_OR(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 20;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x3480; // p.r = 105
		closure_r(p, c);
	}
}

private void label_REF_XOR(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// r: REF_XOR(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 10;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x1280; // p.r = 37
		closure_r(p, c);
	}
	if (	// r: REF_XOR(r,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + 10;
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x1300; // p.r = 38
			closure_r(p, c);
		}
	}
	if (	// r: REF_XOR(r,REF_MOVE(INT_CONSTANT))
		rchild.getOpcode() == REF_MOVE_opcode && 
		rchild.child1.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + (U16(IV(Move.getVal(PR(p))))?10:INFINITE);
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x3A80; // p.r = 117
			closure_r(p, c);
		}
	}
}

private void label_REF_NOT(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// r: REF_NOT(r)
	c = STATE(lchild).cost_r + 10;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x1380; // p.r = 39
		closure_r(p, c);
	}
	if (	// r: REF_NOT(REF_OR(r,r))
		lchild.getOpcode() == REF_OR_opcode 
	) {
		c = STATE(lchild.child1).cost_r + STATE(lchild.child2).cost_r + 10;
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x1400; // p.r = 40
			closure_r(p, c);
		}
	}
	if (	// r: REF_NOT(REF_AND(r,r))
		lchild.getOpcode() == REF_AND_opcode 
	) {
		c = STATE(lchild.child1).cost_r + STATE(lchild.child2).cost_r + 10;
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x1480; // p.r = 41
			closure_r(p, c);
		}
	}
	if (	// r: REF_NOT(REF_XOR(r,r))
		lchild.getOpcode() == REF_XOR_opcode 
	) {
		c = STATE(lchild.child1).cost_r + STATE(lchild.child2).cost_r + 10;
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x1500; // p.r = 42
			closure_r(p, c);
		}
	}
}

private void label_LONG_NOT(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// r: LONG_NOT(r)
	c = STATE(lchild).cost_r + 20;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x3580; // p.r = 107
		closure_r(p, c);
	}
}

private void label_LONG_XOR(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// r: LONG_XOR(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 20;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x3500; // p.r = 106
		closure_r(p, c);
	}
}

private void label_INT_2LONG(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// r: INT_2LONG(r)
	c = STATE(lchild).cost_r + 20;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x3600; // p.r = 108
		closure_r(p, c);
	}
}

private void label_INT_2FLOAT(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// r: INT_2FLOAT(r)
	c = STATE(lchild).cost_r + 10;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x2180; // p.r = 67
		closure_r(p, c);
	}
}

private void label_INT_2DOUBLE(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// r: INT_2DOUBLE(r)
	c = STATE(lchild).cost_r + 10;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x2200; // p.r = 68
		closure_r(p, c);
	}
}

private void label_LONG_2INT(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// r: LONG_2INT(r)
	c = STATE(lchild).cost_r + 10;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x3680; // p.r = 109
		closure_r(p, c);
	}
}

private void label_FLOAT_2INT(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// r: FLOAT_2INT(r)
	c = STATE(lchild).cost_r + 10;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x2280; // p.r = 69
		closure_r(p, c);
	}
}

private void label_FLOAT_2DOUBLE(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// r: FLOAT_2DOUBLE(r)
	c = STATE(lchild).cost_r + 10;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x2300; // p.r = 70
		closure_r(p, c);
	}
}

private void label_DOUBLE_2INT(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// r: DOUBLE_2INT(r)
	c = STATE(lchild).cost_r + 10;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x2380; // p.r = 71
		closure_r(p, c);
	}
}

private void label_DOUBLE_2FLOAT(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// r: DOUBLE_2FLOAT(r)
	c = STATE(lchild).cost_r + 10;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x2400; // p.r = 72
		closure_r(p, c);
	}
}

private void label_INT_2BYTE(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// rs: INT_2BYTE(r)
	c = STATE(lchild).cost_r + 10;
	if (c < p.cost_rs) {
		p.cost_rs = (char)(c);
		p.word0 = (p.word0 & 0xFFE0FFFF) | 0x40000; // p.rs = 4
		closure_rs(p, c);
	}
}

private void label_INT_2USHORT(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// rp: INT_2USHORT(r)
	c = STATE(lchild).cost_r + 20;
	if (c < p.cost_rp) {
		p.cost_rp = (char)(c);
		p.word0 = (p.word0 & 0xF0FFFFFF) | 0x9000000; // p.rp = 9
		closure_rp(p, c);
	}
}

private void label_INT_2SHORT(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// rs: INT_2SHORT(r)
	c = STATE(lchild).cost_r + 10;
	if (c < p.cost_rs) {
		p.cost_rs = (char)(c);
		p.word0 = (p.word0 & 0xFFE0FFFF) | 0x50000; // p.rs = 5
		closure_rs(p, c);
	}
}

private void label_LONG_CMP(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// stm: LONG_CMP(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 40;
	if (c < p.cost_stm) {
		p.cost_stm = (char)(c);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0x4A; // p.stm = 74
	}
}

private void label_FLOAT_CMPL(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// stm: FLOAT_CMPL(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 40;
	if (c < p.cost_stm) {
		p.cost_stm = (char)(c);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0x42; // p.stm = 66
	}
}

private void label_FLOAT_CMPG(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// stm: FLOAT_CMPG(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 40;
	if (c < p.cost_stm) {
		p.cost_stm = (char)(c);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0x43; // p.stm = 67
	}
}

private void label_DOUBLE_CMPL(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// stm: DOUBLE_CMPL(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 40;
	if (c < p.cost_stm) {
		p.cost_stm = (char)(c);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0x44; // p.stm = 68
	}
}

private void label_DOUBLE_CMPG(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// stm: DOUBLE_CMPG(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 40;
	if (c < p.cost_stm) {
		p.cost_stm = (char)(c);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0x45; // p.stm = 69
	}
}

private void label_RETURN(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	if (	// stm: RETURN(NULL)
		lchild.getOpcode() == NULL_opcode 
	) {
		c = 10;
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x47; // p.stm = 71
		}
	}
	// stm: RETURN(r)
	c = STATE(lchild).cost_r + 10;
	if (c < p.cost_stm) {
		p.cost_stm = (char)(c);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0x48; // p.stm = 72
	}
}

private void label_NULL_CHECK(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// stm: NULL_CHECK(r)
	c = STATE(lchild).cost_r + 11;
	if (c < p.cost_stm) {
		p.cost_stm = (char)(c);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0xB; // p.stm = 11
	}
}

private void label_GOTO(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	// stm: GOTO
	if (11 < p.cost_stm) {
		p.cost_stm = (char)(11);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0x46; // p.stm = 70
	}
}

private void label_BOOLEAN_NOT(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// r: BOOLEAN_NOT(r)
	c = STATE(lchild).cost_r + 11;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x400; // p.r = 8
		closure_r(p, c);
	}
}

private void label_BOOLEAN_CMP_INT(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	if (	// r: BOOLEAN_CMP_INT(r,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + 10;
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x480; // p.r = 9
			closure_r(p, c);
		}
	}
	// r: BOOLEAN_CMP_INT(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 10;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x500; // p.r = 10
		closure_r(p, c);
	}
	if (	// boolcmp: BOOLEAN_CMP_INT(r,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + 0;
		if (c < p.cost_boolcmp) {
			p.cost_boolcmp = (char)(c);
			p.word1 = (p.word1 & 0xFFFFFFF0) | 0x1; // p.boolcmp = 1
		}
	}
	// boolcmp: BOOLEAN_CMP_INT(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 0;
	if (c < p.cost_boolcmp) {
		p.cost_boolcmp = (char)(c);
		p.word1 = (p.word1 & 0xFFFFFFF0) | 0x2; // p.boolcmp = 2
	}
	if (	// boolcmp: BOOLEAN_CMP_INT(boolcmp,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_boolcmp + (VR(p) == 1 && BooleanCmp.getCond(P(p)).isEQUAL()?0:INFINITE);
		if (c < p.cost_boolcmp) {
			p.cost_boolcmp = (char)(c);
			p.word1 = (p.word1 & 0xFFFFFFF0) | 0x5; // p.boolcmp = 5
		}
	}
	if (	// boolcmp: BOOLEAN_CMP_INT(boolcmp,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_boolcmp + (VR(p) == 1 && BooleanCmp.getCond(P(p)).isNOT_EQUAL()?0:INFINITE);
		if (c < p.cost_boolcmp) {
			p.cost_boolcmp = (char)(c);
			p.word1 = (p.word1 & 0xFFFFFFF0) | 0x6; // p.boolcmp = 6
		}
	}
	if (	// boolcmp: BOOLEAN_CMP_INT(boolcmp,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_boolcmp + (VR(p) == 0 && BooleanCmp.getCond(P(p)).isNOT_EQUAL()?0:INFINITE);
		if (c < p.cost_boolcmp) {
			p.cost_boolcmp = (char)(c);
			p.word1 = (p.word1 & 0xFFFFFFF0) | 0x7; // p.boolcmp = 7
		}
	}
	if (	// boolcmp: BOOLEAN_CMP_INT(boolcmp,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_boolcmp + (VR(p) == 0 && BooleanCmp.getCond(P(p)).isEQUAL()?0:INFINITE);
		if (c < p.cost_boolcmp) {
			p.cost_boolcmp = (char)(c);
			p.word1 = (p.word1 & 0xFFFFFFF0) | 0x8; // p.boolcmp = 8
		}
	}
	if (	// r: BOOLEAN_CMP_INT(boolcmp,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_boolcmp + (VR(p) == 1 && BooleanCmp.getCond(P(p)).isEQUAL()?10:INFINITE);
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x680; // p.r = 13
			closure_r(p, c);
		}
	}
	if (	// r: BOOLEAN_CMP_INT(boolcmp,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_boolcmp + (VR(p) == 1 && BooleanCmp.getCond(P(p)).isNOT_EQUAL()?10:INFINITE);
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x700; // p.r = 14
			closure_r(p, c);
		}
	}
	if (	// r: BOOLEAN_CMP_INT(boolcmp,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_boolcmp + (VR(p) == 0 && BooleanCmp.getCond(P(p)).isNOT_EQUAL()?10:INFINITE);
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x780; // p.r = 15
			closure_r(p, c);
		}
	}
	if (	// r: BOOLEAN_CMP_INT(boolcmp,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_boolcmp + (VR(p) == 0 && BooleanCmp.getCond(P(p)).isEQUAL()?10:INFINITE);
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x800; // p.r = 16
			closure_r(p, c);
		}
	}
}

private void label_BOOLEAN_CMP_ADDR(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	if (	// r: BOOLEAN_CMP_ADDR(r,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + 10;
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x580; // p.r = 11
			closure_r(p, c);
		}
	}
	// r: BOOLEAN_CMP_ADDR(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 10;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x600; // p.r = 12
		closure_r(p, c);
	}
	if (	// boolcmp: BOOLEAN_CMP_ADDR(r,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + 0;
		if (c < p.cost_boolcmp) {
			p.cost_boolcmp = (char)(c);
			p.word1 = (p.word1 & 0xFFFFFFF0) | 0x3; // p.boolcmp = 3
		}
	}
	// boolcmp: BOOLEAN_CMP_ADDR(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 0;
	if (c < p.cost_boolcmp) {
		p.cost_boolcmp = (char)(c);
		p.word1 = (p.word1 & 0xFFFFFFF0) | 0x4; // p.boolcmp = 4
	}
}

private void label_BYTE_LOAD(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	if (	// rs: BYTE_LOAD(r,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + 22;
		if (c < p.cost_rs) {
			p.cost_rs = (char)(c);
			p.word0 = (p.word0 & 0xFFE0FFFF) | 0x90000; // p.rs = 9
			closure_rs(p, c);
		}
	}
	// rs: BYTE_LOAD(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 22;
	if (c < p.cost_rs) {
		p.cost_rs = (char)(c);
		p.word0 = (p.word0 & 0xFFE0FFFF) | 0xA0000; // p.rs = 10
		closure_rs(p, c);
	}
}

private void label_UBYTE_LOAD(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	if (	// rp: UBYTE_LOAD(r,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + 11;
		if (c < p.cost_rp) {
			p.cost_rp = (char)(c);
			p.word0 = (p.word0 & 0xF0FFFFFF) | 0xC000000; // p.rp = 12
			closure_rp(p, c);
		}
	}
	// rp: UBYTE_LOAD(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 11;
	if (c < p.cost_rp) {
		p.cost_rp = (char)(c);
		p.word0 = (p.word0 & 0xF0FFFFFF) | 0xD000000; // p.rp = 13
		closure_rp(p, c);
	}
}

private void label_SHORT_LOAD(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	if (	// rs: SHORT_LOAD(r,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + 11;
		if (c < p.cost_rs) {
			p.cost_rs = (char)(c);
			p.word0 = (p.word0 & 0xFFE0FFFF) | 0xB0000; // p.rs = 11
			closure_rs(p, c);
		}
	}
	// rs: SHORT_LOAD(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 11;
	if (c < p.cost_rs) {
		p.cost_rs = (char)(c);
		p.word0 = (p.word0 & 0xFFE0FFFF) | 0xC0000; // p.rs = 12
		closure_rs(p, c);
	}
}

private void label_USHORT_LOAD(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	if (	// rp: USHORT_LOAD(r,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + 11;
		if (c < p.cost_rp) {
			p.cost_rp = (char)(c);
			p.word0 = (p.word0 & 0xF0FFFFFF) | 0xE000000; // p.rp = 14
			closure_rp(p, c);
		}
	}
	// rp: USHORT_LOAD(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 11;
	if (c < p.cost_rp) {
		p.cost_rp = (char)(c);
		p.word0 = (p.word0 & 0xF0FFFFFF) | 0xF000000; // p.rp = 15
		closure_rp(p, c);
	}
}

private void label_INT_LOAD(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	if (	// rs: INT_LOAD(r,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + 11;
		if (c < p.cost_rs) {
			p.cost_rs = (char)(c);
			p.word0 = (p.word0 & 0xFFE0FFFF) | 0xD0000; // p.rs = 13
			closure_rs(p, c);
		}
	}
	if (	// rs: INT_LOAD(r,REF_MOVE(ADDRESS_CONSTANT))
		rchild.getOpcode() == REF_MOVE_opcode && 
		rchild.child1.getOpcode() == ADDRESS_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + FITS(Move.getVal(PR(p)),32,22);
		if (c < p.cost_rs) {
			p.cost_rs = (char)(c);
			p.word0 = (p.word0 & 0xFFE0FFFF) | 0xE0000; // p.rs = 14
			closure_rs(p, c);
		}
	}
	// rs: INT_LOAD(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 11;
	if (c < p.cost_rs) {
		p.cost_rs = (char)(c);
		p.word0 = (p.word0 & 0xFFE0FFFF) | 0xF0000; // p.rs = 15
		closure_rs(p, c);
	}
	if (	// rs: INT_LOAD(REF_ADD(r,r),INT_CONSTANT)
		lchild.getOpcode() == REF_ADD_opcode && 
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + STATE(lchild.child2).cost_r + isZERO(VR(p), 11);
		if (c < p.cost_rs) {
			p.cost_rs = (char)(c);
			p.word0 = (p.word0 & 0xFFE0FFFF) | 0x100000; // p.rs = 16
			closure_rs(p, c);
		}
	}
	if (	// rs: INT_LOAD(REF_ADD(r,INT_CONSTANT),INT_CONSTANT)
		lchild.getOpcode() == REF_ADD_opcode && 
		lchild.child2.getOpcode() == INT_CONSTANT_opcode && 
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + (SI16(VR(p)+VLR(p)) ? 14 : INFINITE);
		if (c < p.cost_rs) {
			p.cost_rs = (char)(c);
			p.word0 = (p.word0 & 0xFFE0FFFF) | 0x110000; // p.rs = 17
			closure_rs(p, c);
		}
	}
}

private void label_LONG_LOAD(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	if (	// r: LONG_LOAD(r,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + 21;
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x3C80; // p.r = 121
			closure_r(p, c);
		}
	}
	if (	// r: LONG_LOAD(r,REF_MOVE(ADDRESS_CONSTANT))
		rchild.getOpcode() == REF_MOVE_opcode && 
		rchild.child1.getOpcode() == ADDRESS_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + 21;
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x3D00; // p.r = 122
			closure_r(p, c);
		}
	}
	// r: LONG_LOAD(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 21;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x3D80; // p.r = 123
		closure_r(p, c);
	}
}

private void label_FLOAT_LOAD(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	if (	// r: FLOAT_LOAD(r,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + 11;
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x2700; // p.r = 78
			closure_r(p, c);
		}
	}
	if (	// r: FLOAT_LOAD(r,REF_MOVE(ADDRESS_CONSTANT))
		rchild.getOpcode() == REF_MOVE_opcode && 
		rchild.child1.getOpcode() == ADDRESS_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + FITS(Move.getVal(PR(p)),32,22);
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x2780; // p.r = 79
			closure_r(p, c);
		}
	}
	// r: FLOAT_LOAD(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 11;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x2800; // p.r = 80
		closure_r(p, c);
	}
}

private void label_DOUBLE_LOAD(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	if (	// r: DOUBLE_LOAD(r,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + 11;
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x2880; // p.r = 81
			closure_r(p, c);
		}
	}
	if (	// r: DOUBLE_LOAD(r,REF_MOVE(ADDRESS_CONSTANT))
		rchild.getOpcode() == REF_MOVE_opcode && 
		rchild.child1.getOpcode() == ADDRESS_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + FITS(Move.getVal(PR(p)),32,22);
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x2900; // p.r = 82
			closure_r(p, c);
		}
	}
	// r: DOUBLE_LOAD(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 11;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x2980; // p.r = 83
		closure_r(p, c);
	}
}

private void label_BYTE_STORE(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	if (	// stm: BYTE_STORE(r,OTHER_OPERAND(r,INT_CONSTANT))
		rchild.getOpcode() == OTHER_OPERAND_opcode && 
		rchild.child2.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + STATE(rchild.child1).cost_r + 11;
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x1B; // p.stm = 27
		}
	}
	if (	// stm: BYTE_STORE(r,OTHER_OPERAND(r,r))
		rchild.getOpcode() == OTHER_OPERAND_opcode 
	) {
		c = STATE(lchild).cost_r + STATE(rchild.child1).cost_r + STATE(rchild.child2).cost_r + 11;
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x1C; // p.stm = 28
		}
	}
	if (	// stm: BYTE_STORE(INT_2BYTE(r),OTHER_OPERAND(r,INT_CONSTANT))
		lchild.getOpcode() == INT_2BYTE_opcode && 
		rchild.getOpcode() == OTHER_OPERAND_opcode && 
		rchild.child2.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + STATE(rchild.child1).cost_r + 11;
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x1D; // p.stm = 29
		}
	}
	if (	// stm: BYTE_STORE(INT_2BYTE(r),OTHER_OPERAND(r,r))
		lchild.getOpcode() == INT_2BYTE_opcode && 
		rchild.getOpcode() == OTHER_OPERAND_opcode 
	) {
		c = STATE(lchild.child1).cost_r + STATE(rchild.child1).cost_r + STATE(rchild.child2).cost_r + 11;
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x1E; // p.stm = 30
		}
	}
}

private void label_SHORT_STORE(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	if (	// stm: SHORT_STORE(r,OTHER_OPERAND(r,INT_CONSTANT))
		rchild.getOpcode() == OTHER_OPERAND_opcode && 
		rchild.child2.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + STATE(rchild.child1).cost_r + 11;
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x1F; // p.stm = 31
		}
	}
	if (	// stm: SHORT_STORE(r,OTHER_OPERAND(r,r))
		rchild.getOpcode() == OTHER_OPERAND_opcode 
	) {
		c = STATE(lchild).cost_r + STATE(rchild.child1).cost_r + STATE(rchild.child2).cost_r + 11;
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x20; // p.stm = 32
		}
	}
	if (	// stm: SHORT_STORE(INT_2SHORT(r),OTHER_OPERAND(r,INT_CONSTANT))
		lchild.getOpcode() == INT_2SHORT_opcode && 
		rchild.getOpcode() == OTHER_OPERAND_opcode && 
		rchild.child2.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + STATE(rchild.child1).cost_r + 11;
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x21; // p.stm = 33
		}
	}
	if (	// stm: SHORT_STORE(INT_2SHORT(r),OTHER_OPERAND(r,r))
		lchild.getOpcode() == INT_2SHORT_opcode && 
		rchild.getOpcode() == OTHER_OPERAND_opcode 
	) {
		c = STATE(lchild.child1).cost_r + STATE(rchild.child1).cost_r + STATE(rchild.child2).cost_r + 11;
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x22; // p.stm = 34
		}
	}
	if (	// stm: SHORT_STORE(INT_2USHORT(r),OTHER_OPERAND(r,INT_CONSTANT))
		lchild.getOpcode() == INT_2USHORT_opcode && 
		rchild.getOpcode() == OTHER_OPERAND_opcode && 
		rchild.child2.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild.child1).cost_r + STATE(rchild.child1).cost_r + 11;
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x23; // p.stm = 35
		}
	}
	if (	// stm: SHORT_STORE(INT_2USHORT(r),OTHER_OPERAND(r,r))
		lchild.getOpcode() == INT_2USHORT_opcode && 
		rchild.getOpcode() == OTHER_OPERAND_opcode 
	) {
		c = STATE(lchild.child1).cost_r + STATE(rchild.child1).cost_r + STATE(rchild.child2).cost_r + 11;
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x24; // p.stm = 36
		}
	}
}

private void label_INT_STORE(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	if (	// stm: INT_STORE(r,OTHER_OPERAND(r,INT_CONSTANT))
		rchild.getOpcode() == OTHER_OPERAND_opcode && 
		rchild.child2.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + STATE(rchild.child1).cost_r + 11;
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x25; // p.stm = 37
		}
	}
	if (	// stm: INT_STORE(r,OTHER_OPERAND(r,REF_MOVE(ADDRESS_CONSTANT)))
		rchild.getOpcode() == OTHER_OPERAND_opcode && 
		rchild.child2.getOpcode() == REF_MOVE_opcode && 
		rchild.child2.child1.getOpcode() == ADDRESS_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + STATE(rchild.child1).cost_r + FITS(Move.getVal(PRR(p)),32,22);
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x26; // p.stm = 38
		}
	}
	if (	// stm: INT_STORE(r,OTHER_OPERAND(r,r))
		rchild.getOpcode() == OTHER_OPERAND_opcode 
	) {
		c = STATE(lchild).cost_r + STATE(rchild.child1).cost_r + STATE(rchild.child2).cost_r + 11;
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x27; // p.stm = 39
		}
	}
	if (	// stm: INT_STORE(r,OTHER_OPERAND(REF_ADD(r,INT_CONSTANT),INT_CONSTANT))
		rchild.getOpcode() == OTHER_OPERAND_opcode && 
		rchild.child1.getOpcode() == REF_ADD_opcode && 
		rchild.child1.child2.getOpcode() == INT_CONSTANT_opcode && 
		rchild.child2.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + STATE(rchild.child1.child1).cost_r + (SI16(VRR(p)+VRLR(p))?14:INFINITE);
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x28; // p.stm = 40
		}
	}
}

private void label_LONG_STORE(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	if (	// stm: LONG_STORE(r,OTHER_OPERAND(r,INT_CONSTANT))
		rchild.getOpcode() == OTHER_OPERAND_opcode && 
		rchild.child2.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + STATE(rchild.child1).cost_r + 22;
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x59; // p.stm = 89
		}
	}
	if (	// stm: LONG_STORE(r,OTHER_OPERAND(r,REF_MOVE(ADDRESS_CONSTANT)))
		rchild.getOpcode() == OTHER_OPERAND_opcode && 
		rchild.child2.getOpcode() == REF_MOVE_opcode && 
		rchild.child2.child1.getOpcode() == ADDRESS_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + STATE(rchild.child1).cost_r + 22;
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x5A; // p.stm = 90
		}
	}
	if (	// stm: LONG_STORE(r,OTHER_OPERAND(r,r))
		rchild.getOpcode() == OTHER_OPERAND_opcode 
	) {
		c = STATE(lchild).cost_r + STATE(rchild.child1).cost_r + STATE(rchild.child2).cost_r + 22;
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x5B; // p.stm = 91
		}
	}
}

private void label_FLOAT_STORE(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	if (	// stm: FLOAT_STORE(r,OTHER_OPERAND(r,INT_CONSTANT))
		rchild.getOpcode() == OTHER_OPERAND_opcode && 
		rchild.child2.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + STATE(rchild.child1).cost_r + 11;
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x29; // p.stm = 41
		}
	}
	if (	// stm: FLOAT_STORE(r,OTHER_OPERAND(r,REF_MOVE(ADDRESS_CONSTANT)))
		rchild.getOpcode() == OTHER_OPERAND_opcode && 
		rchild.child2.getOpcode() == REF_MOVE_opcode && 
		rchild.child2.child1.getOpcode() == ADDRESS_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + STATE(rchild.child1).cost_r + FITS(Move.getVal(PRR(p)),32,22);
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x2A; // p.stm = 42
		}
	}
	if (	// stm: FLOAT_STORE(r,OTHER_OPERAND(r,r))
		rchild.getOpcode() == OTHER_OPERAND_opcode 
	) {
		c = STATE(lchild).cost_r + STATE(rchild.child1).cost_r + STATE(rchild.child2).cost_r + 11;
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x2B; // p.stm = 43
		}
	}
}

private void label_DOUBLE_STORE(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	if (	// stm: DOUBLE_STORE(r,OTHER_OPERAND(r,INT_CONSTANT))
		rchild.getOpcode() == OTHER_OPERAND_opcode && 
		rchild.child2.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + STATE(rchild.child1).cost_r + 11;
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x2C; // p.stm = 44
		}
	}
	if (	// stm: DOUBLE_STORE(r,OTHER_OPERAND(r,REF_MOVE(ADDRESS_CONSTANT)))
		rchild.getOpcode() == OTHER_OPERAND_opcode && 
		rchild.child2.getOpcode() == REF_MOVE_opcode && 
		rchild.child2.child1.getOpcode() == ADDRESS_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + STATE(rchild.child1).cost_r + FITS(Move.getVal(PRR(p)),32,22);
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x2D; // p.stm = 45
		}
	}
	if (	// stm: DOUBLE_STORE(r,OTHER_OPERAND(r,r))
		rchild.getOpcode() == OTHER_OPERAND_opcode 
	) {
		c = STATE(lchild).cost_r + STATE(rchild.child1).cost_r + STATE(rchild.child2).cost_r + 11;
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x2E; // p.stm = 46
		}
	}
}

private void label_PREPARE_INT(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// r: PREPARE_INT(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 11;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x2D00; // p.r = 90
		closure_r(p, c);
	}
}

private void label_PREPARE_ADDR(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// r: PREPARE_ADDR(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 11;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x3E00; // p.r = 124
		closure_r(p, c);
	}
}

private void label_PREPARE_LONG(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// r: PREPARE_LONG(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 11;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x2D80; // p.r = 91
		closure_r(p, c);
	}
}

private void label_ATTEMPT_INT(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// r: ATTEMPT_INT(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 11;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x2E00; // p.r = 92
		closure_r(p, c);
	}
}

private void label_ATTEMPT_ADDR(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// r: ATTEMPT_ADDR(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 11;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x3E80; // p.r = 125
		closure_r(p, c);
	}
}

private void label_ATTEMPT_LONG(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// r: ATTEMPT_LONG(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 11;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x2E80; // p.r = 93
		closure_r(p, c);
	}
}

private void label_CALL(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// r: CALL(r,any)
	c = STATE(lchild).cost_r + STATE(rchild).cost_any + 10;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x2A00; // p.r = 84
		closure_r(p, c);
	}
	if (	// r: CALL(BRANCH_TARGET,any)
		lchild.getOpcode() == BRANCH_TARGET_opcode 
	) {
		c = STATE(rchild).cost_any + 10;
		if (c < p.cost_r) {
			p.cost_r = (char)(c);
			p.word0 = (p.word0 & 0xFFFFC07F) | 0x2A80; // p.r = 85
			closure_r(p, c);
		}
	}
}

private void label_SYSCALL(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// r: SYSCALL(r,any)
	c = STATE(lchild).cost_r + STATE(rchild).cost_any + 10;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x2B00; // p.r = 86
		closure_r(p, c);
	}
}

private void label_YIELDPOINT_PROLOGUE(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	// stm: YIELDPOINT_PROLOGUE
	if (10 < p.cost_stm) {
		p.cost_stm = (char)(10);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0x6; // p.stm = 6
	}
}

private void label_YIELDPOINT_EPILOGUE(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	// stm: YIELDPOINT_EPILOGUE
	if (10 < p.cost_stm) {
		p.cost_stm = (char)(10);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0x7; // p.stm = 7
	}
}

private void label_YIELDPOINT_BACKEDGE(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	// stm: YIELDPOINT_BACKEDGE
	if (10 < p.cost_stm) {
		p.cost_stm = (char)(10);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0x8; // p.stm = 8
	}
}

private void label_YIELDPOINT_OSR(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// r: YIELDPOINT_OSR(any,any)
	c = STATE(lchild).cost_any + STATE(rchild).cost_any + 11;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x2C80; // p.r = 89
		closure_r(p, c);
	}
}

private void label_IR_PROLOGUE(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	// stm: IR_PROLOGUE
	if (11 < p.cost_stm) {
		p.cost_stm = (char)(11);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0x49; // p.stm = 73
	}
}

private void label_RESOLVE(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	// stm: RESOLVE
	if (10 < p.cost_stm) {
		p.cost_stm = (char)(10);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0x2; // p.stm = 2
	}
}

private void label_GET_TIME_BASE(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	// r: GET_TIME_BASE
	if (11 < p.cost_r) {
		p.cost_r = (char)(11);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x2B80; // p.r = 87
		closure_r(p, 11);
	}
}

private void label_TRAP_IF(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// stm: TRAP_IF(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 10;
	if (c < p.cost_stm) {
		p.cost_stm = (char)(c);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0x18; // p.stm = 24
	}
	if (	// stm: TRAP_IF(r,INT_CONSTANT)
		rchild.getOpcode() == INT_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + 10;
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x19; // p.stm = 25
		}
	}
	if (	// stm: TRAP_IF(r,LONG_CONSTANT)
		rchild.getOpcode() == LONG_CONSTANT_opcode 
	) {
		c = STATE(lchild).cost_r + 10;
		if (c < p.cost_stm) {
			p.cost_stm = (char)(c);
			p.word0 = (p.word0 & 0xFFFFFF80) | 0x1A; // p.stm = 26
		}
	}
}

private void label_TRAP(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	// stm: TRAP
	if (10 < p.cost_stm) {
		p.cost_stm = (char)(10);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0x17; // p.stm = 23
	}
}

private void label_FLOAT_AS_INT_BITS(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// r: FLOAT_AS_INT_BITS(r)
	c = STATE(lchild).cost_r + 20;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x2480; // p.r = 73
		closure_r(p, c);
	}
}

private void label_INT_BITS_AS_FLOAT(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// r: INT_BITS_AS_FLOAT(r)
	c = STATE(lchild).cost_r + 20;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x2500; // p.r = 74
		closure_r(p, c);
	}
}

private void label_DOUBLE_AS_LONG_BITS(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// r: DOUBLE_AS_LONG_BITS(r)
	c = STATE(lchild).cost_r + 40;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x3700; // p.r = 110
		closure_r(p, c);
	}
}

private void label_LONG_BITS_AS_DOUBLE(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// r: LONG_BITS_AS_DOUBLE(r)
	c = STATE(lchild).cost_r + 40;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x3780; // p.r = 111
		closure_r(p, c);
	}
}

private void label_LOWTABLESWITCH(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// stm: LOWTABLESWITCH(r)
	c = STATE(lchild).cost_r + 10;
	if (c < p.cost_stm) {
		p.cost_stm = (char)(c);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0x9; // p.stm = 9
	}
}

private void label_ADDRESS_CONSTANT(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	// any: ADDRESS_CONSTANT
	if (0 < p.cost_any) {
		p.cost_any = (char)(0);
		p.word0 = (p.word0 & 0x8FFFFFFF) | 0x30000000; // p.any = 3
	}
}

private void label_INT_CONSTANT(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	// any: INT_CONSTANT
	if (0 < p.cost_any) {
		p.cost_any = (char)(0);
		p.word0 = (p.word0 & 0x8FFFFFFF) | 0x40000000; // p.any = 4
	}
}

private void label_LONG_CONSTANT(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	// any: LONG_CONSTANT
	if (0 < p.cost_any) {
		p.cost_any = (char)(0);
		p.word0 = (p.word0 & 0x8FFFFFFF) | 0x50000000; // p.any = 5
	}
}

private void label_REGISTER(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	// r: REGISTER
	if (0 < p.cost_r) {
		p.cost_r = (char)(0);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x80; // p.r = 1
		closure_r(p, 0);
	}
}

private void label_OTHER_OPERAND(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild, rchild;
	lchild = p.child1;
	rchild = p.child2;
	label(lchild);
	label(rchild);
	int c;
	// any: OTHER_OPERAND(any,any)
	c = STATE(lchild).cost_any + STATE(rchild).cost_any + 0;
	if (c < p.cost_any) {
		p.cost_any = (char)(c);
		p.word0 = (p.word0 & 0x8FFFFFFF) | 0x60000000; // p.any = 6
	}
	// r: OTHER_OPERAND(r,r)
	c = STATE(lchild).cost_r + STATE(rchild).cost_r + 0;
	if (c < p.cost_r) {
		p.cost_r = (char)(c);
		p.word0 = (p.word0 & 0xFFFFC07F) | 0x2C00; // p.r = 88
		closure_r(p, c);
	}
}

private void label_NULL(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	// any: NULL
	if (0 < p.cost_any) {
		p.cost_any = (char)(0);
		p.word0 = (p.word0 & 0x8FFFFFFF) | 0x10000000; // p.any = 1
	}
}

private void label_BRANCH_TARGET(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
}

private void label_DCBF(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// stm: DCBF(r)
	c = STATE(lchild).cost_r + 11;
	if (c < p.cost_stm) {
		p.cost_stm = (char)(c);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0x10; // p.stm = 16
	}
}

private void label_DCBST(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// stm: DCBST(r)
	c = STATE(lchild).cost_r + 11;
	if (c < p.cost_stm) {
		p.cost_stm = (char)(c);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0x11; // p.stm = 17
	}
}

private void label_DCBT(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// stm: DCBT(r)
	c = STATE(lchild).cost_r + 11;
	if (c < p.cost_stm) {
		p.cost_stm = (char)(c);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0x12; // p.stm = 18
	}
}

private void label_DCBTST(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// stm: DCBTST(r)
	c = STATE(lchild).cost_r + 11;
	if (c < p.cost_stm) {
		p.cost_stm = (char)(c);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0x13; // p.stm = 19
	}
}

private void label_DCBZ(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// stm: DCBZ(r)
	c = STATE(lchild).cost_r + 11;
	if (c < p.cost_stm) {
		p.cost_stm = (char)(c);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0x14; // p.stm = 20
	}
}

private void label_DCBZL(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// stm: DCBZL(r)
	c = STATE(lchild).cost_r + 11;
	if (c < p.cost_stm) {
		p.cost_stm = (char)(c);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0x15; // p.stm = 21
	}
}

private void label_ICBI(BURS_TreeNode p) {
	p.word0 = 0;
	p.initCost();
	BURS_TreeNode lchild;
	lchild = p.child1;
	label(lchild);
	int c;
	// stm: ICBI(r)
	c = STATE(lchild).cost_r + 11;
	if (c < p.cost_stm) {
		p.cost_stm = (char)(c);
		p.word0 = (p.word0 & 0xFFFFFF80) | 0x16; // p.stm = 22
	}
}

public void label(BURS_TreeNode p) {
	p.initCost();
	switch (p.getOpcode()) {
	case GET_CAUGHT_EXCEPTION_opcode: label_GET_CAUGHT_EXCEPTION(p); break;
	case SET_CAUGHT_EXCEPTION_opcode: label_SET_CAUGHT_EXCEPTION(p); break;
	case IG_PATCH_POINT_opcode: label_IG_PATCH_POINT(p); break;
	case INT_IFCMP_opcode: label_INT_IFCMP(p); break;
	case INT_IFCMP2_opcode: label_INT_IFCMP2(p); break;
	case LONG_IFCMP_opcode: label_LONG_IFCMP(p); break;
	case FLOAT_IFCMP_opcode: label_FLOAT_IFCMP(p); break;
	case DOUBLE_IFCMP_opcode: label_DOUBLE_IFCMP(p); break;
	case UNINT_BEGIN_opcode: label_UNINT_BEGIN(p); break;
	case UNINT_END_opcode: label_UNINT_END(p); break;
	case FENCE_opcode: label_FENCE(p); break;
	case READ_CEILING_opcode: label_READ_CEILING(p); break;
	case WRITE_FLOOR_opcode: label_WRITE_FLOOR(p); break;
	case NOP_opcode: label_NOP(p); break;
	case LONG_MOVE_opcode: label_LONG_MOVE(p); break;
	case FLOAT_MOVE_opcode: label_FLOAT_MOVE(p); break;
	case DOUBLE_MOVE_opcode: label_DOUBLE_MOVE(p); break;
	case REF_MOVE_opcode: label_REF_MOVE(p); break;
	case GUARD_MOVE_opcode: label_GUARD_MOVE(p); break;
	case GUARD_COMBINE_opcode: label_GUARD_COMBINE(p); break;
	case REF_ADD_opcode: label_REF_ADD(p); break;
	case LONG_ADD_opcode: label_LONG_ADD(p); break;
	case FLOAT_ADD_opcode: label_FLOAT_ADD(p); break;
	case DOUBLE_ADD_opcode: label_DOUBLE_ADD(p); break;
	case REF_SUB_opcode: label_REF_SUB(p); break;
	case LONG_SUB_opcode: label_LONG_SUB(p); break;
	case FLOAT_SUB_opcode: label_FLOAT_SUB(p); break;
	case DOUBLE_SUB_opcode: label_DOUBLE_SUB(p); break;
	case INT_MUL_opcode: label_INT_MUL(p); break;
	case LONG_MUL_opcode: label_LONG_MUL(p); break;
	case FLOAT_MUL_opcode: label_FLOAT_MUL(p); break;
	case DOUBLE_MUL_opcode: label_DOUBLE_MUL(p); break;
	case INT_DIV_opcode: label_INT_DIV(p); break;
	case FLOAT_DIV_opcode: label_FLOAT_DIV(p); break;
	case DOUBLE_DIV_opcode: label_DOUBLE_DIV(p); break;
	case INT_REM_opcode: label_INT_REM(p); break;
	case REF_NEG_opcode: label_REF_NEG(p); break;
	case LONG_NEG_opcode: label_LONG_NEG(p); break;
	case FLOAT_NEG_opcode: label_FLOAT_NEG(p); break;
	case DOUBLE_NEG_opcode: label_DOUBLE_NEG(p); break;
	case FLOAT_SQRT_opcode: label_FLOAT_SQRT(p); break;
	case DOUBLE_SQRT_opcode: label_DOUBLE_SQRT(p); break;
	case INT_SHL_opcode: label_INT_SHL(p); break;
	case LONG_SHL_opcode: label_LONG_SHL(p); break;
	case INT_SHR_opcode: label_INT_SHR(p); break;
	case LONG_SHR_opcode: label_LONG_SHR(p); break;
	case INT_USHR_opcode: label_INT_USHR(p); break;
	case LONG_USHR_opcode: label_LONG_USHR(p); break;
	case REF_AND_opcode: label_REF_AND(p); break;
	case LONG_AND_opcode: label_LONG_AND(p); break;
	case REF_OR_opcode: label_REF_OR(p); break;
	case LONG_OR_opcode: label_LONG_OR(p); break;
	case REF_XOR_opcode: label_REF_XOR(p); break;
	case REF_NOT_opcode: label_REF_NOT(p); break;
	case LONG_NOT_opcode: label_LONG_NOT(p); break;
	case LONG_XOR_opcode: label_LONG_XOR(p); break;
	case INT_2LONG_opcode: label_INT_2LONG(p); break;
	case INT_2FLOAT_opcode: label_INT_2FLOAT(p); break;
	case INT_2DOUBLE_opcode: label_INT_2DOUBLE(p); break;
	case LONG_2INT_opcode: label_LONG_2INT(p); break;
	case FLOAT_2INT_opcode: label_FLOAT_2INT(p); break;
	case FLOAT_2DOUBLE_opcode: label_FLOAT_2DOUBLE(p); break;
	case DOUBLE_2INT_opcode: label_DOUBLE_2INT(p); break;
	case DOUBLE_2FLOAT_opcode: label_DOUBLE_2FLOAT(p); break;
	case INT_2BYTE_opcode: label_INT_2BYTE(p); break;
	case INT_2USHORT_opcode: label_INT_2USHORT(p); break;
	case INT_2SHORT_opcode: label_INT_2SHORT(p); break;
	case LONG_CMP_opcode: label_LONG_CMP(p); break;
	case FLOAT_CMPL_opcode: label_FLOAT_CMPL(p); break;
	case FLOAT_CMPG_opcode: label_FLOAT_CMPG(p); break;
	case DOUBLE_CMPL_opcode: label_DOUBLE_CMPL(p); break;
	case DOUBLE_CMPG_opcode: label_DOUBLE_CMPG(p); break;
	case RETURN_opcode: label_RETURN(p); break;
	case NULL_CHECK_opcode: label_NULL_CHECK(p); break;
	case GOTO_opcode: label_GOTO(p); break;
	case BOOLEAN_NOT_opcode: label_BOOLEAN_NOT(p); break;
	case BOOLEAN_CMP_INT_opcode: label_BOOLEAN_CMP_INT(p); break;
	case BOOLEAN_CMP_ADDR_opcode: label_BOOLEAN_CMP_ADDR(p); break;
	case BYTE_LOAD_opcode: label_BYTE_LOAD(p); break;
	case UBYTE_LOAD_opcode: label_UBYTE_LOAD(p); break;
	case SHORT_LOAD_opcode: label_SHORT_LOAD(p); break;
	case USHORT_LOAD_opcode: label_USHORT_LOAD(p); break;
	case INT_LOAD_opcode: label_INT_LOAD(p); break;
	case LONG_LOAD_opcode: label_LONG_LOAD(p); break;
	case FLOAT_LOAD_opcode: label_FLOAT_LOAD(p); break;
	case DOUBLE_LOAD_opcode: label_DOUBLE_LOAD(p); break;
	case BYTE_STORE_opcode: label_BYTE_STORE(p); break;
	case SHORT_STORE_opcode: label_SHORT_STORE(p); break;
	case INT_STORE_opcode: label_INT_STORE(p); break;
	case LONG_STORE_opcode: label_LONG_STORE(p); break;
	case FLOAT_STORE_opcode: label_FLOAT_STORE(p); break;
	case DOUBLE_STORE_opcode: label_DOUBLE_STORE(p); break;
	case PREPARE_INT_opcode: label_PREPARE_INT(p); break;
	case PREPARE_ADDR_opcode: label_PREPARE_ADDR(p); break;
	case PREPARE_LONG_opcode: label_PREPARE_LONG(p); break;
	case ATTEMPT_INT_opcode: label_ATTEMPT_INT(p); break;
	case ATTEMPT_ADDR_opcode: label_ATTEMPT_ADDR(p); break;
	case ATTEMPT_LONG_opcode: label_ATTEMPT_LONG(p); break;
	case CALL_opcode: label_CALL(p); break;
	case SYSCALL_opcode: label_SYSCALL(p); break;
	case YIELDPOINT_PROLOGUE_opcode: label_YIELDPOINT_PROLOGUE(p); break;
	case YIELDPOINT_EPILOGUE_opcode: label_YIELDPOINT_EPILOGUE(p); break;
	case YIELDPOINT_BACKEDGE_opcode: label_YIELDPOINT_BACKEDGE(p); break;
	case YIELDPOINT_OSR_opcode: label_YIELDPOINT_OSR(p); break;
	case IR_PROLOGUE_opcode: label_IR_PROLOGUE(p); break;
	case RESOLVE_opcode: label_RESOLVE(p); break;
	case GET_TIME_BASE_opcode: label_GET_TIME_BASE(p); break;
	case TRAP_IF_opcode: label_TRAP_IF(p); break;
	case TRAP_opcode: label_TRAP(p); break;
	case FLOAT_AS_INT_BITS_opcode: label_FLOAT_AS_INT_BITS(p); break;
	case INT_BITS_AS_FLOAT_opcode: label_INT_BITS_AS_FLOAT(p); break;
	case DOUBLE_AS_LONG_BITS_opcode: label_DOUBLE_AS_LONG_BITS(p); break;
	case LONG_BITS_AS_DOUBLE_opcode: label_LONG_BITS_AS_DOUBLE(p); break;
	case LOWTABLESWITCH_opcode: label_LOWTABLESWITCH(p); break;
	case ADDRESS_CONSTANT_opcode: label_ADDRESS_CONSTANT(p); break;
	case INT_CONSTANT_opcode: label_INT_CONSTANT(p); break;
	case LONG_CONSTANT_opcode: label_LONG_CONSTANT(p); break;
	case REGISTER_opcode: label_REGISTER(p); break;
	case OTHER_OPERAND_opcode: label_OTHER_OPERAND(p); break;
	case NULL_opcode: label_NULL(p); break;
	case BRANCH_TARGET_opcode: label_BRANCH_TARGET(p); break;
	case DCBF_opcode: label_DCBF(p); break;
	case DCBST_opcode: label_DCBST(p); break;
	case DCBT_opcode: label_DCBT(p); break;
	case DCBTST_opcode: label_DCBTST(p); break;
	case DCBZ_opcode: label_DCBZ(p); break;
	case DCBZL_opcode: label_DCBZL(p); break;
	case ICBI_opcode: label_ICBI(p); break;
	default:
		throw new OptimizingCompilerException("BURS","terminal not in grammar:",OperatorNames.operatorName[p.getOpcode()]);	}
}

static BURS_TreeNode kids(BURS_TreeNode p, int eruleno, int kidnumber)  { 
	if (BURS.DEBUG) {
	switch (eruleno) {
	case 9: // any: r
	case 7: // rz: rp
	case 6: // rs: rp
	case 5: // r: rz
	case 4: // r: rs
	case 3: // r: czr
	case 1: // stm: r
		if (kidnumber == 0)  return p;
		break;
	case 261: // r: REF_MOVE(ADDRESS_CONSTANT)
	case 260: // r: REF_MOVE(ADDRESS_CONSTANT)
	case 259: // r: REF_MOVE(ADDRESS_CONSTANT)
	case 237: // r: LONG_MOVE(LONG_CONSTANT)
	case 218: // stm: IR_PROLOGUE
	case 211: // r: GET_TIME_BASE
	case 206: // stm: RETURN(NULL)
	case 205: // stm: GOTO
	case 138: // rs: REF_MOVE(INT_CONSTANT)
	case 137: // rs: REF_MOVE(INT_CONSTANT)
	case 136: // rs: REF_MOVE(INT_CONSTANT)
	case 38: // stm: TRAP
	case 30: // stm: READ_CEILING
	case 29: // stm: WRITE_FLOOR
	case 28: // stm: FENCE
	case 26: // r: GET_CAUGHT_EXCEPTION
	case 24: // r: GUARD_COMBINE
	case 23: // r: GUARD_MOVE
	case 22: // stm: NOP
	case 20: // stm: YIELDPOINT_BACKEDGE
	case 19: // stm: YIELDPOINT_EPILOGUE
	case 18: // stm: YIELDPOINT_PROLOGUE
	case 17: // stm: UNINT_END
	case 16: // stm: UNINT_BEGIN
	case 15: // stm: IG_PATCH_POINT
	case 14: // stm: RESOLVE
	case 12: // any: LONG_CONSTANT
	case 11: // any: INT_CONSTANT
	case 10: // any: ADDRESS_CONSTANT
	case 8: // any: NULL
	case 2: // r: REGISTER
		break;
	case 269: // r: ATTEMPT_ADDR(r,r)
	case 268: // r: PREPARE_ADDR(r,r)
	case 264: // r: LONG_LOAD(r,r)
	case 240: // stm: LONG_IFCMP(r,r)
	case 239: // stm: LONG_CMP(r,r)
	case 231: // r: LONG_XOR(r,r)
	case 230: // r: LONG_OR(r,r)
	case 229: // r: LONG_AND(r,r)
	case 227: // r: LONG_USHR(r,r)
	case 225: // r: LONG_SHR(r,r)
	case 223: // r: LONG_SHL(r,r)
	case 221: // r: LONG_MUL(r,r)
	case 220: // r: LONG_SUB(r,r)
	case 219: // r: LONG_ADD(r,r)
	case 217: // r: ATTEMPT_LONG(r,r)
	case 216: // r: ATTEMPT_INT(r,r)
	case 215: // r: PREPARE_LONG(r,r)
	case 214: // r: PREPARE_INT(r,r)
	case 213: // r: YIELDPOINT_OSR(any,any)
	case 212: // r: OTHER_OPERAND(r,r)
	case 210: // r: SYSCALL(r,any)
	case 208: // r: CALL(r,any)
	case 204: // stm: DOUBLE_CMPG(r,r)
	case 203: // stm: DOUBLE_CMPL(r,r)
	case 202: // stm: FLOAT_CMPG(r,r)
	case 201: // stm: FLOAT_CMPL(r,r)
	case 200: // stm: DOUBLE_IFCMP(r,r)
	case 199: // stm: FLOAT_IFCMP(r,r)
	case 197: // stm: INT_IFCMP2(r,r)
	case 182: // stm: INT_IFCMP(r,r)
	case 159: // rs: INT_LOAD(r,r)
	case 156: // r: DOUBLE_LOAD(r,r)
	case 153: // r: FLOAT_LOAD(r,r)
	case 150: // rp: USHORT_LOAD(r,r)
	case 148: // rs: SHORT_LOAD(r,r)
	case 146: // rp: UBYTE_LOAD(r,r)
	case 142: // rs: BYTE_LOAD(r,r)
	case 107: // r: DOUBLE_DIV(r,r)
	case 106: // r: FLOAT_DIV(r,r)
	case 105: // r: DOUBLE_SUB(r,r)
	case 104: // r: FLOAT_SUB(r,r)
	case 103: // r: DOUBLE_MUL(r,r)
	case 102: // r: FLOAT_MUL(r,r)
	case 101: // r: DOUBLE_ADD(r,r)
	case 100: // r: FLOAT_ADD(r,r)
	case 94: // r: REF_XOR(r,r)
	case 90: // r: REF_OR(r,r)
	case 83: // r: REF_AND(r,r)
	case 79: // rz: INT_USHR(r,r)
	case 76: // rs: INT_SHR(r,r)
	case 73: // rz: INT_SHL(r,r)
	case 69: // r: INT_REM(r,r)
	case 67: // r: INT_DIV(r,r)
	case 66: // r: INT_MUL(r,r)
	case 63: // r: REF_SUB(r,r)
	case 60: // r: REF_ADD(r,r)
	case 50: // boolcmp: BOOLEAN_CMP_ADDR(r,r)
	case 48: // r: BOOLEAN_CMP_ADDR(r,r)
	case 46: // boolcmp: BOOLEAN_CMP_INT(r,r)
	case 44: // r: BOOLEAN_CMP_INT(r,r)
	case 39: // stm: TRAP_IF(r,r)
	case 13: // any: OTHER_OPERAND(any,any)
		if (kidnumber == 0)  return p.child1;
		if (kidnumber == 1)  return p.child2;
		break;
	case 263: // r: LONG_LOAD(r,REF_MOVE(ADDRESS_CONSTANT))
	case 262: // r: LONG_LOAD(r,INT_CONSTANT)
	case 258: // r: REF_XOR(r,REF_MOVE(INT_CONSTANT))
	case 257: // r: REF_OR(r,REF_MOVE(INT_CONSTANT))
	case 256: // r: REF_OR(r,REF_MOVE(INT_CONSTANT))
	case 255: // r: REF_AND(r,REF_MOVE(INT_CONSTANT))
	case 254: // czr: REF_AND(r,REF_MOVE(INT_CONSTANT))
	case 238: // r: LONG_MOVE(r)
	case 236: // r: LONG_BITS_AS_DOUBLE(r)
	case 235: // r: DOUBLE_AS_LONG_BITS(r)
	case 234: // r: LONG_2INT(r)
	case 233: // r: INT_2LONG(r)
	case 232: // r: LONG_NOT(r)
	case 228: // r: LONG_USHR(r,INT_CONSTANT)
	case 226: // r: LONG_SHR(r,INT_CONSTANT)
	case 224: // r: LONG_SHL(r,INT_CONSTANT)
	case 222: // r: LONG_NEG(r)
	case 207: // stm: RETURN(r)
	case 198: // stm: INT_IFCMP2(r,INT_CONSTANT)
	case 196: // stm: INT_IFCMP(boolcmp,INT_CONSTANT)
	case 195: // stm: INT_IFCMP(boolcmp,INT_CONSTANT)
	case 194: // stm: INT_IFCMP(boolcmp,INT_CONSTANT)
	case 193: // stm: INT_IFCMP(boolcmp,INT_CONSTANT)
	case 183: // stm: INT_IFCMP(r,INT_CONSTANT)
	case 158: // rs: INT_LOAD(r,REF_MOVE(ADDRESS_CONSTANT))
	case 157: // rs: INT_LOAD(r,INT_CONSTANT)
	case 155: // r: DOUBLE_LOAD(r,REF_MOVE(ADDRESS_CONSTANT))
	case 154: // r: DOUBLE_LOAD(r,INT_CONSTANT)
	case 152: // r: FLOAT_LOAD(r,REF_MOVE(ADDRESS_CONSTANT))
	case 151: // r: FLOAT_LOAD(r,INT_CONSTANT)
	case 149: // rp: USHORT_LOAD(r,INT_CONSTANT)
	case 147: // rs: SHORT_LOAD(r,INT_CONSTANT)
	case 145: // rp: UBYTE_LOAD(r,INT_CONSTANT)
	case 141: // rs: BYTE_LOAD(r,INT_CONSTANT)
	case 140: // r: DOUBLE_MOVE(r)
	case 139: // r: FLOAT_MOVE(r)
	case 135: // r: REF_MOVE(r)
	case 134: // r: INT_BITS_AS_FLOAT(r)
	case 133: // r: FLOAT_AS_INT_BITS(r)
	case 132: // r: DOUBLE_2FLOAT(r)
	case 131: // r: DOUBLE_2INT(r)
	case 130: // r: FLOAT_2DOUBLE(r)
	case 129: // r: FLOAT_2INT(r)
	case 128: // r: INT_2DOUBLE(r)
	case 127: // r: INT_2FLOAT(r)
	case 126: // rs: INT_2SHORT(r)
	case 125: // rp: INT_2USHORT(r)
	case 124: // rs: INT_2BYTE(r)
	case 111: // r: DOUBLE_SQRT(r)
	case 110: // r: FLOAT_SQRT(r)
	case 109: // r: DOUBLE_NEG(r)
	case 108: // r: FLOAT_NEG(r)
	case 96: // r: REF_NOT(r)
	case 95: // r: REF_XOR(r,INT_CONSTANT)
	case 91: // r: REF_OR(r,INT_CONSTANT)
	case 85: // rp: REF_AND(r,INT_CONSTANT)
	case 84: // czr: REF_AND(r,INT_CONSTANT)
	case 78: // rp: INT_USHR(r,INT_CONSTANT)
	case 75: // rs: INT_SHR(r,INT_CONSTANT)
	case 72: // rz: INT_SHL(r,INT_CONSTANT)
	case 71: // r: REF_NEG(r)
	case 70: // r: INT_REM(r,REF_MOVE(INT_CONSTANT))
	case 68: // r: INT_DIV(r,REF_MOVE(INT_CONSTANT))
	case 65: // r: INT_MUL(r,INT_CONSTANT)
	case 62: // r: REF_ADD(r,REF_MOVE(INT_CONSTANT))
	case 61: // r: REF_ADD(r,REF_MOVE(INT_CONSTANT))
	case 59: // r: REF_ADD(r,INT_CONSTANT)
	case 58: // r: BOOLEAN_CMP_INT(boolcmp,INT_CONSTANT)
	case 57: // r: BOOLEAN_CMP_INT(boolcmp,INT_CONSTANT)
	case 56: // r: BOOLEAN_CMP_INT(boolcmp,INT_CONSTANT)
	case 55: // r: BOOLEAN_CMP_INT(boolcmp,INT_CONSTANT)
	case 54: // boolcmp: BOOLEAN_CMP_INT(boolcmp,INT_CONSTANT)
	case 53: // boolcmp: BOOLEAN_CMP_INT(boolcmp,INT_CONSTANT)
	case 52: // boolcmp: BOOLEAN_CMP_INT(boolcmp,INT_CONSTANT)
	case 51: // boolcmp: BOOLEAN_CMP_INT(boolcmp,INT_CONSTANT)
	case 49: // boolcmp: BOOLEAN_CMP_ADDR(r,INT_CONSTANT)
	case 47: // r: BOOLEAN_CMP_ADDR(r,INT_CONSTANT)
	case 45: // boolcmp: BOOLEAN_CMP_INT(r,INT_CONSTANT)
	case 43: // r: BOOLEAN_CMP_INT(r,INT_CONSTANT)
	case 42: // r: BOOLEAN_NOT(r)
	case 41: // stm: TRAP_IF(r,LONG_CONSTANT)
	case 40: // stm: TRAP_IF(r,INT_CONSTANT)
	case 37: // stm: ICBI(r)
	case 36: // stm: DCBZL(r)
	case 35: // stm: DCBZ(r)
	case 34: // stm: DCBTST(r)
	case 33: // stm: DCBT(r)
	case 32: // stm: DCBST(r)
	case 31: // stm: DCBF(r)
	case 27: // stm: SET_CAUGHT_EXCEPTION(r)
	case 25: // stm: NULL_CHECK(r)
	case 21: // stm: LOWTABLESWITCH(r)
		if (kidnumber == 0)  return p.child1;
		break;
	case 209: // r: CALL(BRANCH_TARGET,any)
	case 64: // r: REF_SUB(INT_CONSTANT,r)
		if (kidnumber == 0)  return p.child2;
		break;
	case 251: // stm: INT_IFCMP(REF_ADD(r,INT_CONSTANT),INT_CONSTANT)
	case 250: // stm: INT_IFCMP(REF_AND(r,REF_MOVE(INT_CONSTANT)),INT_CONSTANT)
	case 249: // stm: INT_IFCMP(REF_AND(r,REF_MOVE(INT_CONSTANT)),INT_CONSTANT)
	case 244: // stm: INT_IFCMP(REF_NOT(r),INT_CONSTANT)
	case 243: // stm: INT_IFCMP(REF_NEG(r),INT_CONSTANT)
	case 192: // stm: INT_IFCMP(REF_AND(r,INT_CONSTANT),INT_CONSTANT)
	case 191: // stm: INT_IFCMP(INT_SHR(r,INT_CONSTANT),INT_CONSTANT)
	case 190: // stm: INT_IFCMP(INT_SHL(r,INT_CONSTANT),INT_CONSTANT)
	case 189: // stm: INT_IFCMP(INT_USHR(r,INT_CONSTANT),INT_CONSTANT)
	case 185: // stm: INT_IFCMP(INT_2SHORT(r),INT_CONSTANT)
	case 184: // stm: INT_IFCMP(INT_2BYTE(r),INT_CONSTANT)
	case 161: // rs: INT_LOAD(REF_ADD(r,INT_CONSTANT),INT_CONSTANT)
	case 144: // rp: REF_AND(BYTE_LOAD(r,INT_CONSTANT),INT_CONSTANT)
	case 89: // rp: REF_AND(INT_USHR(r,INT_CONSTANT),REF_MOVE(INT_CONSTANT))
	case 88: // rp: REF_AND(INT_USHR(r,INT_CONSTANT),INT_CONSTANT)
	case 82: // rp: INT_USHR(INT_SHL(r,INT_CONSTANT),INT_CONSTANT)
	case 81: // rp: INT_USHR(REF_AND(r,REF_MOVE(INT_CONSTANT)),INT_CONSTANT)
	case 80: // rp: INT_USHR(REF_AND(r,INT_CONSTANT),INT_CONSTANT)
	case 77: // rp: INT_SHR(REF_AND(r,INT_CONSTANT),INT_CONSTANT)
	case 74: // rz: INT_SHL(INT_USHR(r,INT_CONSTANT),INT_CONSTANT)
		if (kidnumber == 0)  return p.child1.child1;
		break;
	case 170: // stm: SHORT_STORE(INT_2USHORT(r),OTHER_OPERAND(r,INT_CONSTANT))
	case 168: // stm: SHORT_STORE(INT_2SHORT(r),OTHER_OPERAND(r,INT_CONSTANT))
	case 164: // stm: BYTE_STORE(INT_2BYTE(r),OTHER_OPERAND(r,INT_CONSTANT))
	case 92: // r: REF_OR(REF_NOT(r),REF_NOT(r))
	case 86: // r: REF_AND(REF_NOT(r),REF_NOT(r))
		if (kidnumber == 0)  return p.child1.child1;
		if (kidnumber == 1)  return p.child2.child1;
		break;
	case 266: // stm: LONG_STORE(r,OTHER_OPERAND(r,REF_MOVE(ADDRESS_CONSTANT)))
	case 265: // stm: LONG_STORE(r,OTHER_OPERAND(r,INT_CONSTANT))
	case 180: // stm: DOUBLE_STORE(r,OTHER_OPERAND(r,REF_MOVE(ADDRESS_CONSTANT)))
	case 179: // stm: DOUBLE_STORE(r,OTHER_OPERAND(r,INT_CONSTANT))
	case 177: // stm: FLOAT_STORE(r,OTHER_OPERAND(r,REF_MOVE(ADDRESS_CONSTANT)))
	case 176: // stm: FLOAT_STORE(r,OTHER_OPERAND(r,INT_CONSTANT))
	case 173: // stm: INT_STORE(r,OTHER_OPERAND(r,REF_MOVE(ADDRESS_CONSTANT)))
	case 172: // stm: INT_STORE(r,OTHER_OPERAND(r,INT_CONSTANT))
	case 166: // stm: SHORT_STORE(r,OTHER_OPERAND(r,INT_CONSTANT))
	case 162: // stm: BYTE_STORE(r,OTHER_OPERAND(r,INT_CONSTANT))
	case 93: // r: REF_OR(r,REF_NOT(r))
	case 87: // r: REF_AND(r,REF_NOT(r))
		if (kidnumber == 0)  return p.child1;
		if (kidnumber == 1)  return p.child2.child1;
		break;
	case 248: // stm: INT_IFCMP(REF_XOR(r,r),INT_CONSTANT)
	case 247: // stm: INT_IFCMP(REF_OR(r,r),INT_CONSTANT)
	case 246: // stm: INT_IFCMP(REF_AND(r,r),INT_CONSTANT)
	case 245: // stm: INT_IFCMP(REF_ADD(r,r),INT_CONSTANT)
	case 242: // stm: INT_IFCMP(ATTEMPT_ADDR(r,r),INT_CONSTANT)
	case 241: // stm: INT_IFCMP(ATTEMPT_INT(r,r),INT_CONSTANT)
	case 188: // stm: INT_IFCMP(INT_SHR(r,r),INT_CONSTANT)
	case 187: // stm: INT_IFCMP(INT_SHL(r,r),INT_CONSTANT)
	case 186: // stm: INT_IFCMP(INT_USHR(r,r),INT_CONSTANT)
	case 160: // rs: INT_LOAD(REF_ADD(r,r),INT_CONSTANT)
	case 143: // rp: REF_AND(BYTE_LOAD(r,r),INT_CONSTANT)
	case 99: // r: REF_NOT(REF_XOR(r,r))
	case 98: // r: REF_NOT(REF_AND(r,r))
	case 97: // r: REF_NOT(REF_OR(r,r))
		if (kidnumber == 0)  return p.child1.child1;
		if (kidnumber == 1)  return p.child1.child2;
		break;
	case 117: // r: DOUBLE_SUB(DOUBLE_MUL(r,r),r)
	case 116: // r: FLOAT_SUB(FLOAT_MUL(r,r),r)
	case 113: // r: DOUBLE_ADD(DOUBLE_MUL(r,r),r)
	case 112: // r: FLOAT_ADD(FLOAT_MUL(r,r),r)
		if (kidnumber == 0)  return p.child1.child1;
		if (kidnumber == 1)  return p.child1.child2;
		if (kidnumber == 2)  return p.child2;
		break;
	case 267: // stm: LONG_STORE(r,OTHER_OPERAND(r,r))
	case 181: // stm: DOUBLE_STORE(r,OTHER_OPERAND(r,r))
	case 178: // stm: FLOAT_STORE(r,OTHER_OPERAND(r,r))
	case 174: // stm: INT_STORE(r,OTHER_OPERAND(r,r))
	case 167: // stm: SHORT_STORE(r,OTHER_OPERAND(r,r))
	case 163: // stm: BYTE_STORE(r,OTHER_OPERAND(r,r))
	case 115: // r: DOUBLE_ADD(r,DOUBLE_MUL(r,r))
	case 114: // r: FLOAT_ADD(r,FLOAT_MUL(r,r))
		if (kidnumber == 0)  return p.child1;
		if (kidnumber == 1)  return p.child2.child1;
		if (kidnumber == 2)  return p.child2.child2;
		break;
	case 123: // r: DOUBLE_NEG(DOUBLE_SUB(DOUBLE_MUL(r,r),r))
	case 122: // r: FLOAT_NEG(FLOAT_SUB(FLOAT_MUL(r,r),r))
	case 119: // r: DOUBLE_NEG(DOUBLE_ADD(DOUBLE_MUL(r,r),r))
	case 118: // r: FLOAT_NEG(FLOAT_ADD(FLOAT_MUL(r,r),r))
		if (kidnumber == 0)  return p.child1.child1.child1;
		if (kidnumber == 1)  return p.child1.child1.child2;
		if (kidnumber == 2)  return p.child1.child2;
		break;
	case 121: // r: DOUBLE_NEG(DOUBLE_ADD(r,DOUBLE_MUL(r,r)))
	case 120: // r: FLOAT_NEG(FLOAT_ADD(r,FLOAT_MUL(r,r)))
		if (kidnumber == 0)  return p.child1.child1;
		if (kidnumber == 1)  return p.child1.child2.child1;
		if (kidnumber == 2)  return p.child1.child2.child2;
		break;
	case 171: // stm: SHORT_STORE(INT_2USHORT(r),OTHER_OPERAND(r,r))
	case 169: // stm: SHORT_STORE(INT_2SHORT(r),OTHER_OPERAND(r,r))
	case 165: // stm: BYTE_STORE(INT_2BYTE(r),OTHER_OPERAND(r,r))
		if (kidnumber == 0)  return p.child1.child1;
		if (kidnumber == 1)  return p.child2.child1;
		if (kidnumber == 2)  return p.child2.child2;
		break;
	case 175: // stm: INT_STORE(r,OTHER_OPERAND(REF_ADD(r,INT_CONSTANT),INT_CONSTANT))
		if (kidnumber == 0)  return p.child1;
		if (kidnumber == 1)  return p.child2.child1.child1;
		break;
	case 253: // stm: INT_IFCMP(REF_OR(r,REF_NOT(r)),INT_CONSTANT)
	case 252: // stm: INT_IFCMP(REF_AND(r,REF_NOT(r)),INT_CONSTANT)
		if (kidnumber == 0)  return p.child1.child1;
		if (kidnumber == 1)  return p.child1.child2.child1;
		break;
	}
	throw new OptimizingCompilerException("BURS","Bad rule number ",Integer.toString(eruleno));
} else return null;
}

static void mark_kids(BURS_TreeNode p, int eruleno)
	 {
	byte[] ntsrule = nts[eruleno];
	switch (eruleno) {
	case 9: // any: r
	case 7: // rz: rp
	case 6: // rs: rp
	case 5: // r: rz
	case 4: // r: rs
	case 3: // r: czr
	case 1: // stm: r
		mark(p, ntsrule[0]);
		break;
	case 261: // r: REF_MOVE(ADDRESS_CONSTANT)
	case 260: // r: REF_MOVE(ADDRESS_CONSTANT)
	case 259: // r: REF_MOVE(ADDRESS_CONSTANT)
	case 237: // r: LONG_MOVE(LONG_CONSTANT)
	case 218: // stm: IR_PROLOGUE
	case 211: // r: GET_TIME_BASE
	case 206: // stm: RETURN(NULL)
	case 205: // stm: GOTO
	case 138: // rs: REF_MOVE(INT_CONSTANT)
	case 137: // rs: REF_MOVE(INT_CONSTANT)
	case 136: // rs: REF_MOVE(INT_CONSTANT)
	case 38: // stm: TRAP
	case 30: // stm: READ_CEILING
	case 29: // stm: WRITE_FLOOR
	case 28: // stm: FENCE
	case 26: // r: GET_CAUGHT_EXCEPTION
	case 24: // r: GUARD_COMBINE
	case 23: // r: GUARD_MOVE
	case 22: // stm: NOP
	case 20: // stm: YIELDPOINT_BACKEDGE
	case 19: // stm: YIELDPOINT_EPILOGUE
	case 18: // stm: YIELDPOINT_PROLOGUE
	case 17: // stm: UNINT_END
	case 16: // stm: UNINT_BEGIN
	case 15: // stm: IG_PATCH_POINT
	case 14: // stm: RESOLVE
	case 12: // any: LONG_CONSTANT
	case 11: // any: INT_CONSTANT
	case 10: // any: ADDRESS_CONSTANT
	case 8: // any: NULL
	case 2: // r: REGISTER
		break;
	case 269: // r: ATTEMPT_ADDR(r,r)
	case 268: // r: PREPARE_ADDR(r,r)
	case 264: // r: LONG_LOAD(r,r)
	case 240: // stm: LONG_IFCMP(r,r)
	case 239: // stm: LONG_CMP(r,r)
	case 231: // r: LONG_XOR(r,r)
	case 230: // r: LONG_OR(r,r)
	case 229: // r: LONG_AND(r,r)
	case 227: // r: LONG_USHR(r,r)
	case 225: // r: LONG_SHR(r,r)
	case 223: // r: LONG_SHL(r,r)
	case 221: // r: LONG_MUL(r,r)
	case 220: // r: LONG_SUB(r,r)
	case 219: // r: LONG_ADD(r,r)
	case 217: // r: ATTEMPT_LONG(r,r)
	case 216: // r: ATTEMPT_INT(r,r)
	case 215: // r: PREPARE_LONG(r,r)
	case 214: // r: PREPARE_INT(r,r)
	case 213: // r: YIELDPOINT_OSR(any,any)
	case 212: // r: OTHER_OPERAND(r,r)
	case 210: // r: SYSCALL(r,any)
	case 208: // r: CALL(r,any)
	case 204: // stm: DOUBLE_CMPG(r,r)
	case 203: // stm: DOUBLE_CMPL(r,r)
	case 202: // stm: FLOAT_CMPG(r,r)
	case 201: // stm: FLOAT_CMPL(r,r)
	case 200: // stm: DOUBLE_IFCMP(r,r)
	case 199: // stm: FLOAT_IFCMP(r,r)
	case 197: // stm: INT_IFCMP2(r,r)
	case 182: // stm: INT_IFCMP(r,r)
	case 159: // rs: INT_LOAD(r,r)
	case 156: // r: DOUBLE_LOAD(r,r)
	case 153: // r: FLOAT_LOAD(r,r)
	case 150: // rp: USHORT_LOAD(r,r)
	case 148: // rs: SHORT_LOAD(r,r)
	case 146: // rp: UBYTE_LOAD(r,r)
	case 142: // rs: BYTE_LOAD(r,r)
	case 107: // r: DOUBLE_DIV(r,r)
	case 106: // r: FLOAT_DIV(r,r)
	case 105: // r: DOUBLE_SUB(r,r)
	case 104: // r: FLOAT_SUB(r,r)
	case 103: // r: DOUBLE_MUL(r,r)
	case 102: // r: FLOAT_MUL(r,r)
	case 101: // r: DOUBLE_ADD(r,r)
	case 100: // r: FLOAT_ADD(r,r)
	case 94: // r: REF_XOR(r,r)
	case 90: // r: REF_OR(r,r)
	case 83: // r: REF_AND(r,r)
	case 79: // rz: INT_USHR(r,r)
	case 76: // rs: INT_SHR(r,r)
	case 73: // rz: INT_SHL(r,r)
	case 69: // r: INT_REM(r,r)
	case 67: // r: INT_DIV(r,r)
	case 66: // r: INT_MUL(r,r)
	case 63: // r: REF_SUB(r,r)
	case 60: // r: REF_ADD(r,r)
	case 50: // boolcmp: BOOLEAN_CMP_ADDR(r,r)
	case 48: // r: BOOLEAN_CMP_ADDR(r,r)
	case 46: // boolcmp: BOOLEAN_CMP_INT(r,r)
	case 44: // r: BOOLEAN_CMP_INT(r,r)
	case 39: // stm: TRAP_IF(r,r)
	case 13: // any: OTHER_OPERAND(any,any)
		mark(p.child1, ntsrule[0]);
		mark(p.child2, ntsrule[1]);
		break;
	case 263: // r: LONG_LOAD(r,REF_MOVE(ADDRESS_CONSTANT))
	case 262: // r: LONG_LOAD(r,INT_CONSTANT)
	case 258: // r: REF_XOR(r,REF_MOVE(INT_CONSTANT))
	case 257: // r: REF_OR(r,REF_MOVE(INT_CONSTANT))
	case 256: // r: REF_OR(r,REF_MOVE(INT_CONSTANT))
	case 255: // r: REF_AND(r,REF_MOVE(INT_CONSTANT))
	case 254: // czr: REF_AND(r,REF_MOVE(INT_CONSTANT))
	case 238: // r: LONG_MOVE(r)
	case 236: // r: LONG_BITS_AS_DOUBLE(r)
	case 235: // r: DOUBLE_AS_LONG_BITS(r)
	case 234: // r: LONG_2INT(r)
	case 233: // r: INT_2LONG(r)
	case 232: // r: LONG_NOT(r)
	case 228: // r: LONG_USHR(r,INT_CONSTANT)
	case 226: // r: LONG_SHR(r,INT_CONSTANT)
	case 224: // r: LONG_SHL(r,INT_CONSTANT)
	case 222: // r: LONG_NEG(r)
	case 207: // stm: RETURN(r)
	case 198: // stm: INT_IFCMP2(r,INT_CONSTANT)
	case 196: // stm: INT_IFCMP(boolcmp,INT_CONSTANT)
	case 195: // stm: INT_IFCMP(boolcmp,INT_CONSTANT)
	case 194: // stm: INT_IFCMP(boolcmp,INT_CONSTANT)
	case 193: // stm: INT_IFCMP(boolcmp,INT_CONSTANT)
	case 183: // stm: INT_IFCMP(r,INT_CONSTANT)
	case 158: // rs: INT_LOAD(r,REF_MOVE(ADDRESS_CONSTANT))
	case 157: // rs: INT_LOAD(r,INT_CONSTANT)
	case 155: // r: DOUBLE_LOAD(r,REF_MOVE(ADDRESS_CONSTANT))
	case 154: // r: DOUBLE_LOAD(r,INT_CONSTANT)
	case 152: // r: FLOAT_LOAD(r,REF_MOVE(ADDRESS_CONSTANT))
	case 151: // r: FLOAT_LOAD(r,INT_CONSTANT)
	case 149: // rp: USHORT_LOAD(r,INT_CONSTANT)
	case 147: // rs: SHORT_LOAD(r,INT_CONSTANT)
	case 145: // rp: UBYTE_LOAD(r,INT_CONSTANT)
	case 141: // rs: BYTE_LOAD(r,INT_CONSTANT)
	case 140: // r: DOUBLE_MOVE(r)
	case 139: // r: FLOAT_MOVE(r)
	case 135: // r: REF_MOVE(r)
	case 134: // r: INT_BITS_AS_FLOAT(r)
	case 133: // r: FLOAT_AS_INT_BITS(r)
	case 132: // r: DOUBLE_2FLOAT(r)
	case 131: // r: DOUBLE_2INT(r)
	case 130: // r: FLOAT_2DOUBLE(r)
	case 129: // r: FLOAT_2INT(r)
	case 128: // r: INT_2DOUBLE(r)
	case 127: // r: INT_2FLOAT(r)
	case 126: // rs: INT_2SHORT(r)
	case 125: // rp: INT_2USHORT(r)
	case 124: // rs: INT_2BYTE(r)
	case 111: // r: DOUBLE_SQRT(r)
	case 110: // r: FLOAT_SQRT(r)
	case 109: // r: DOUBLE_NEG(r)
	case 108: // r: FLOAT_NEG(r)
	case 96: // r: REF_NOT(r)
	case 95: // r: REF_XOR(r,INT_CONSTANT)
	case 91: // r: REF_OR(r,INT_CONSTANT)
	case 85: // rp: REF_AND(r,INT_CONSTANT)
	case 84: // czr: REF_AND(r,INT_CONSTANT)
	case 78: // rp: INT_USHR(r,INT_CONSTANT)
	case 75: // rs: INT_SHR(r,INT_CONSTANT)
	case 72: // rz: INT_SHL(r,INT_CONSTANT)
	case 71: // r: REF_NEG(r)
	case 70: // r: INT_REM(r,REF_MOVE(INT_CONSTANT))
	case 68: // r: INT_DIV(r,REF_MOVE(INT_CONSTANT))
	case 65: // r: INT_MUL(r,INT_CONSTANT)
	case 62: // r: REF_ADD(r,REF_MOVE(INT_CONSTANT))
	case 61: // r: REF_ADD(r,REF_MOVE(INT_CONSTANT))
	case 59: // r: REF_ADD(r,INT_CONSTANT)
	case 58: // r: BOOLEAN_CMP_INT(boolcmp,INT_CONSTANT)
	case 57: // r: BOOLEAN_CMP_INT(boolcmp,INT_CONSTANT)
	case 56: // r: BOOLEAN_CMP_INT(boolcmp,INT_CONSTANT)
	case 55: // r: BOOLEAN_CMP_INT(boolcmp,INT_CONSTANT)
	case 54: // boolcmp: BOOLEAN_CMP_INT(boolcmp,INT_CONSTANT)
	case 53: // boolcmp: BOOLEAN_CMP_INT(boolcmp,INT_CONSTANT)
	case 52: // boolcmp: BOOLEAN_CMP_INT(boolcmp,INT_CONSTANT)
	case 51: // boolcmp: BOOLEAN_CMP_INT(boolcmp,INT_CONSTANT)
	case 49: // boolcmp: BOOLEAN_CMP_ADDR(r,INT_CONSTANT)
	case 47: // r: BOOLEAN_CMP_ADDR(r,INT_CONSTANT)
	case 45: // boolcmp: BOOLEAN_CMP_INT(r,INT_CONSTANT)
	case 43: // r: BOOLEAN_CMP_INT(r,INT_CONSTANT)
	case 42: // r: BOOLEAN_NOT(r)
	case 41: // stm: TRAP_IF(r,LONG_CONSTANT)
	case 40: // stm: TRAP_IF(r,INT_CONSTANT)
	case 37: // stm: ICBI(r)
	case 36: // stm: DCBZL(r)
	case 35: // stm: DCBZ(r)
	case 34: // stm: DCBTST(r)
	case 33: // stm: DCBT(r)
	case 32: // stm: DCBST(r)
	case 31: // stm: DCBF(r)
	case 27: // stm: SET_CAUGHT_EXCEPTION(r)
	case 25: // stm: NULL_CHECK(r)
	case 21: // stm: LOWTABLESWITCH(r)
		mark(p.child1, ntsrule[0]);
		break;
	case 209: // r: CALL(BRANCH_TARGET,any)
	case 64: // r: REF_SUB(INT_CONSTANT,r)
		mark(p.child2, ntsrule[0]);
		break;
	case 251: // stm: INT_IFCMP(REF_ADD(r,INT_CONSTANT),INT_CONSTANT)
	case 250: // stm: INT_IFCMP(REF_AND(r,REF_MOVE(INT_CONSTANT)),INT_CONSTANT)
	case 249: // stm: INT_IFCMP(REF_AND(r,REF_MOVE(INT_CONSTANT)),INT_CONSTANT)
	case 244: // stm: INT_IFCMP(REF_NOT(r),INT_CONSTANT)
	case 243: // stm: INT_IFCMP(REF_NEG(r),INT_CONSTANT)
	case 192: // stm: INT_IFCMP(REF_AND(r,INT_CONSTANT),INT_CONSTANT)
	case 191: // stm: INT_IFCMP(INT_SHR(r,INT_CONSTANT),INT_CONSTANT)
	case 190: // stm: INT_IFCMP(INT_SHL(r,INT_CONSTANT),INT_CONSTANT)
	case 189: // stm: INT_IFCMP(INT_USHR(r,INT_CONSTANT),INT_CONSTANT)
	case 185: // stm: INT_IFCMP(INT_2SHORT(r),INT_CONSTANT)
	case 184: // stm: INT_IFCMP(INT_2BYTE(r),INT_CONSTANT)
	case 161: // rs: INT_LOAD(REF_ADD(r,INT_CONSTANT),INT_CONSTANT)
	case 144: // rp: REF_AND(BYTE_LOAD(r,INT_CONSTANT),INT_CONSTANT)
	case 89: // rp: REF_AND(INT_USHR(r,INT_CONSTANT),REF_MOVE(INT_CONSTANT))
	case 88: // rp: REF_AND(INT_USHR(r,INT_CONSTANT),INT_CONSTANT)
	case 82: // rp: INT_USHR(INT_SHL(r,INT_CONSTANT),INT_CONSTANT)
	case 81: // rp: INT_USHR(REF_AND(r,REF_MOVE(INT_CONSTANT)),INT_CONSTANT)
	case 80: // rp: INT_USHR(REF_AND(r,INT_CONSTANT),INT_CONSTANT)
	case 77: // rp: INT_SHR(REF_AND(r,INT_CONSTANT),INT_CONSTANT)
	case 74: // rz: INT_SHL(INT_USHR(r,INT_CONSTANT),INT_CONSTANT)
		mark(p.child1.child1, ntsrule[0]);
		break;
	case 170: // stm: SHORT_STORE(INT_2USHORT(r),OTHER_OPERAND(r,INT_CONSTANT))
	case 168: // stm: SHORT_STORE(INT_2SHORT(r),OTHER_OPERAND(r,INT_CONSTANT))
	case 164: // stm: BYTE_STORE(INT_2BYTE(r),OTHER_OPERAND(r,INT_CONSTANT))
	case 92: // r: REF_OR(REF_NOT(r),REF_NOT(r))
	case 86: // r: REF_AND(REF_NOT(r),REF_NOT(r))
		mark(p.child1.child1, ntsrule[0]);
		mark(p.child2.child1, ntsrule[1]);
		break;
	case 266: // stm: LONG_STORE(r,OTHER_OPERAND(r,REF_MOVE(ADDRESS_CONSTANT)))
	case 265: // stm: LONG_STORE(r,OTHER_OPERAND(r,INT_CONSTANT))
	case 180: // stm: DOUBLE_STORE(r,OTHER_OPERAND(r,REF_MOVE(ADDRESS_CONSTANT)))
	case 179: // stm: DOUBLE_STORE(r,OTHER_OPERAND(r,INT_CONSTANT))
	case 177: // stm: FLOAT_STORE(r,OTHER_OPERAND(r,REF_MOVE(ADDRESS_CONSTANT)))
	case 176: // stm: FLOAT_STORE(r,OTHER_OPERAND(r,INT_CONSTANT))
	case 173: // stm: INT_STORE(r,OTHER_OPERAND(r,REF_MOVE(ADDRESS_CONSTANT)))
	case 172: // stm: INT_STORE(r,OTHER_OPERAND(r,INT_CONSTANT))
	case 166: // stm: SHORT_STORE(r,OTHER_OPERAND(r,INT_CONSTANT))
	case 162: // stm: BYTE_STORE(r,OTHER_OPERAND(r,INT_CONSTANT))
	case 93: // r: REF_OR(r,REF_NOT(r))
	case 87: // r: REF_AND(r,REF_NOT(r))
		mark(p.child1, ntsrule[0]);
		mark(p.child2.child1, ntsrule[1]);
		break;
	case 248: // stm: INT_IFCMP(REF_XOR(r,r),INT_CONSTANT)
	case 247: // stm: INT_IFCMP(REF_OR(r,r),INT_CONSTANT)
	case 246: // stm: INT_IFCMP(REF_AND(r,r),INT_CONSTANT)
	case 245: // stm: INT_IFCMP(REF_ADD(r,r),INT_CONSTANT)
	case 242: // stm: INT_IFCMP(ATTEMPT_ADDR(r,r),INT_CONSTANT)
	case 241: // stm: INT_IFCMP(ATTEMPT_INT(r,r),INT_CONSTANT)
	case 188: // stm: INT_IFCMP(INT_SHR(r,r),INT_CONSTANT)
	case 187: // stm: INT_IFCMP(INT_SHL(r,r),INT_CONSTANT)
	case 186: // stm: INT_IFCMP(INT_USHR(r,r),INT_CONSTANT)
	case 160: // rs: INT_LOAD(REF_ADD(r,r),INT_CONSTANT)
	case 143: // rp: REF_AND(BYTE_LOAD(r,r),INT_CONSTANT)
	case 99: // r: REF_NOT(REF_XOR(r,r))
	case 98: // r: REF_NOT(REF_AND(r,r))
	case 97: // r: REF_NOT(REF_OR(r,r))
		mark(p.child1.child1, ntsrule[0]);
		mark(p.child1.child2, ntsrule[1]);
		break;
	case 117: // r: DOUBLE_SUB(DOUBLE_MUL(r,r),r)
	case 116: // r: FLOAT_SUB(FLOAT_MUL(r,r),r)
	case 113: // r: DOUBLE_ADD(DOUBLE_MUL(r,r),r)
	case 112: // r: FLOAT_ADD(FLOAT_MUL(r,r),r)
		mark(p.child1.child1, ntsrule[0]);
		mark(p.child1.child2, ntsrule[1]);
		mark(p.child2, ntsrule[2]);
		break;
	case 267: // stm: LONG_STORE(r,OTHER_OPERAND(r,r))
	case 181: // stm: DOUBLE_STORE(r,OTHER_OPERAND(r,r))
	case 178: // stm: FLOAT_STORE(r,OTHER_OPERAND(r,r))
	case 174: // stm: INT_STORE(r,OTHER_OPERAND(r,r))
	case 167: // stm: SHORT_STORE(r,OTHER_OPERAND(r,r))
	case 163: // stm: BYTE_STORE(r,OTHER_OPERAND(r,r))
	case 115: // r: DOUBLE_ADD(r,DOUBLE_MUL(r,r))
	case 114: // r: FLOAT_ADD(r,FLOAT_MUL(r,r))
		mark(p.child1, ntsrule[0]);
		mark(p.child2.child1, ntsrule[1]);
		mark(p.child2.child2, ntsrule[2]);
		break;
	case 123: // r: DOUBLE_NEG(DOUBLE_SUB(DOUBLE_MUL(r,r),r))
	case 122: // r: FLOAT_NEG(FLOAT_SUB(FLOAT_MUL(r,r),r))
	case 119: // r: DOUBLE_NEG(DOUBLE_ADD(DOUBLE_MUL(r,r),r))
	case 118: // r: FLOAT_NEG(FLOAT_ADD(FLOAT_MUL(r,r),r))
		mark(p.child1.child1.child1, ntsrule[0]);
		mark(p.child1.child1.child2, ntsrule[1]);
		mark(p.child1.child2, ntsrule[2]);
		break;
	case 121: // r: DOUBLE_NEG(DOUBLE_ADD(r,DOUBLE_MUL(r,r)))
	case 120: // r: FLOAT_NEG(FLOAT_ADD(r,FLOAT_MUL(r,r)))
		mark(p.child1.child1, ntsrule[0]);
		mark(p.child1.child2.child1, ntsrule[1]);
		mark(p.child1.child2.child2, ntsrule[2]);
		break;
	case 171: // stm: SHORT_STORE(INT_2USHORT(r),OTHER_OPERAND(r,r))
	case 169: // stm: SHORT_STORE(INT_2SHORT(r),OTHER_OPERAND(r,r))
	case 165: // stm: BYTE_STORE(INT_2BYTE(r),OTHER_OPERAND(r,r))
		mark(p.child1.child1, ntsrule[0]);
		mark(p.child2.child1, ntsrule[1]);
		mark(p.child2.child2, ntsrule[2]);
		break;
	case 175: // stm: INT_STORE(r,OTHER_OPERAND(REF_ADD(r,INT_CONSTANT),INT_CONSTANT))
		mark(p.child1, ntsrule[0]);
		mark(p.child2.child1.child1, ntsrule[1]);
		break;
	case 253: // stm: INT_IFCMP(REF_OR(r,REF_NOT(r)),INT_CONSTANT)
	case 252: // stm: INT_IFCMP(REF_AND(r,REF_NOT(r)),INT_CONSTANT)
		mark(p.child1.child1, ntsrule[0]);
		mark(p.child1.child2.child1, ntsrule[1]);
		break;
	}
}

public static final byte[] action={0
   ,NOFLAGS
   ,NOFLAGS
   ,NOFLAGS
   ,NOFLAGS
   ,NOFLAGS
   ,NOFLAGS
   ,NOFLAGS
   ,NOFLAGS
   ,NOFLAGS
   ,NOFLAGS
   ,NOFLAGS
   ,NOFLAGS
   ,NOFLAGS
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,NOFLAGS
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,NOFLAGS
   ,EMIT_INSTRUCTION
   ,NOFLAGS
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,NOFLAGS
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
   ,EMIT_INSTRUCTION
};

void code14(BURS_TreeNode p) {
    EMIT(P(p));
}
void code15(BURS_TreeNode p) {
    EMIT(InlineGuard.mutate(P(p), IG_PATCH_POINT, null, null, null, InlineGuard.getTarget(P(p)), InlineGuard.getBranchProfile(P(p))));
}
void code16(BURS_TreeNode p) {
    EMIT(P(p));
}
void code17(BURS_TreeNode p) {
    EMIT(P(p));
}
void code18(BURS_TreeNode p) {
    EMIT(P(p));
}
void code19(BURS_TreeNode p) {
    EMIT(P(p));
}
void code20(BURS_TreeNode p) {
    EMIT(P(p));
}
void code21(BURS_TreeNode p) {
    LOWTABLESWITCH(P(p));
}
void code23(BURS_TreeNode p) {
    EMIT(P(p));
}
void code24(BURS_TreeNode p) {
    EMIT(P(p));
}
void code25(BURS_TreeNode p) {
    EMIT(P(p));
}
void code26(BURS_TreeNode p) {
    GET_EXCEPTION_OBJECT(P(p));
}
void code27(BURS_TreeNode p) {
    SET_EXCEPTION_OBJECT(P(p));
}
void code28(BURS_TreeNode p) {
    EMIT(MIR_Empty.mutate(P(p), PPC_SYNC));
}
void code29(BURS_TreeNode p) {
    EMIT(MIR_Empty.mutate(P(p), PPC_SYNC));
}
void code30(BURS_TreeNode p) {
    EMIT(MIR_Empty.mutate(P(p), PPC_ISYNC));
}
void code31(BURS_TreeNode p) {
    EMIT(MIR_CacheOp.mutate(P(p), PPC_DCBF, I(0), R(CacheOp.getRef(P(p)))));
}
void code32(BURS_TreeNode p) {
    EMIT(MIR_CacheOp.mutate(P(p), PPC_DCBST, I(0), R(CacheOp.getRef(P(p)))));
}
void code33(BURS_TreeNode p) {
    EMIT(MIR_CacheOp.mutate(P(p), PPC_DCBT, I(0), R(CacheOp.getRef(P(p)))));
}
void code34(BURS_TreeNode p) {
    EMIT(MIR_CacheOp.mutate(P(p), PPC_DCBTST, I(0), R(CacheOp.getRef(P(p)))));
}
void code35(BURS_TreeNode p) {
    EMIT(MIR_CacheOp.mutate(P(p), PPC_DCBZ, I(0), R(CacheOp.getRef(P(p)))));
}
void code36(BURS_TreeNode p) {
    EMIT(MIR_CacheOp.mutate(P(p), PPC_DCBZL, I(0), R(CacheOp.getRef(P(p)))));
}
void code37(BURS_TreeNode p) {
    EMIT(MIR_CacheOp.mutate(P(p), PPC_ICBI, I(0), R(CacheOp.getRef(P(p)))));
}
void code38(BURS_TreeNode p) {
    TRAP(P(p));
}
void code39(BURS_TreeNode p) {
    TRAP_IF(P(p));
}
void code40(BURS_TreeNode p) {
    TRAP_IF_IMM(P(p), false);
}
void code41(BURS_TreeNode p) {
    TRAP_IF_IMM(P(p), true);
}
void code42(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_XORI, Unary.getResult(P(p)), R(Unary.getVal(P(p))), IC(1)));
}
void code43(BURS_TreeNode p) {
    BOOLEAN_CMP_INT_IMM(BooleanCmp.getResult(P(p)), BooleanCmp.getCond(P(p)), R(BooleanCmp.getVal1(P(p))), IC(BooleanCmp.getVal2(P(p))));
}
void code44(BURS_TreeNode p) {
    BOOLEAN_CMP_INT(BooleanCmp.getResult(P(p)), BooleanCmp.getCond(P(p)), R(BooleanCmp.getVal1(P(p))), R(BooleanCmp.getVal2(P(p))));
}
void code45(BURS_TreeNode p) {
    PUSH_BOOLCMP(BooleanCmp.getCond(P(p)), BooleanCmp.getVal1(P(p)), BooleanCmp.getVal2(P(p)), false);
}
void code46(BURS_TreeNode p) {
    PUSH_BOOLCMP(BooleanCmp.getCond(P(p)), BooleanCmp.getVal1(P(p)), BooleanCmp.getVal2(P(p)), false);
}
void code47(BURS_TreeNode p) {
    BOOLEAN_CMP_ADDR_IMM(BooleanCmp.getResult(P(p)), BooleanCmp.getCond(P(p)), R(BooleanCmp.getVal1(P(p))), IC(BooleanCmp.getVal2(P(p))));
}
void code48(BURS_TreeNode p) {
    BOOLEAN_CMP_ADDR(BooleanCmp.getResult(P(p)), BooleanCmp.getCond(P(p)), R(BooleanCmp.getVal1(P(p))), R(BooleanCmp.getVal2(P(p))));
}
void code49(BURS_TreeNode p) {
    PUSH_BOOLCMP(BooleanCmp.getCond(P(p)), BooleanCmp.getVal1(P(p)), BooleanCmp.getVal2(P(p)), true);
}
void code50(BURS_TreeNode p) {
    PUSH_BOOLCMP(BooleanCmp.getCond(P(p)), BooleanCmp.getVal1(P(p)), BooleanCmp.getVal2(P(p)), true);
}
void code52(BURS_TreeNode p) {
    FLIP_BOOLCMP(); // invert condition
}
void code54(BURS_TreeNode p) {
    FLIP_BOOLCMP(); // invert condition
}
void code55(BURS_TreeNode p) {
    EMIT_PUSHED_BOOLCMP(BooleanCmp.getResult(P(p)));
}
void code56(BURS_TreeNode p) {
    FLIP_BOOLCMP(); EMIT_PUSHED_BOOLCMP(BooleanCmp.getResult(P(p)));
}
void code57(BURS_TreeNode p) {
    EMIT_PUSHED_BOOLCMP(BooleanCmp.getResult(P(p)));
}
void code58(BURS_TreeNode p) {
    FLIP_BOOLCMP(); EMIT_PUSHED_BOOLCMP(BooleanCmp.getResult(P(p)));
}
void code59(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_ADDI, Binary.getResult(P(p)),                                       R(Binary.getVal1(P(p))), Binary.getVal2(P(p))));
}
void code60(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_ADD, Binary.getResult(P(p)),                                        R(Binary.getVal1(P(p))), Binary.getVal2(P(p))));
}
void code61(BURS_TreeNode p) {
    {                                                                                int val = IV(Move.getVal(PR(p)));                                         EMIT(MIR_Binary.create(PPC_ADDI, Move.getResult(PR(p)),                                              R(Binary.getVal1(P(p))), CAL16(val)));                 EMIT(MIR_Binary.mutate(P(p), PPC_ADDIS, Binary.getResult(P(p)),                                      Move.getResult(PR(p)).copyRO(), CAU16(val)));       }
}
void code62(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_ADDIS, Binary.getResult(P(p)),                                      R(Binary.getVal1(P(p))), CAU16(IV(Move.getVal(PR(p))))));
}
void code63(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_SUBF, Binary.getResult(P(p)),                                       R(Binary.getVal2(P(p))), Binary.getVal1(P(p))));
}
void code64(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_SUBFIC, Binary.getResult(P(p)),                                     R(Binary.getVal2(P(p))), Binary.getVal1(P(p))));
}
void code65(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_MULLI, Binary.getResult(P(p)),                                      R(Binary.getVal1(P(p))), Binary.getVal2(P(p))));
}
void code66(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_MULLW, Binary.getResult(P(p)),                                      R(Binary.getVal1(P(p))), Binary.getVal2(P(p))));
}
void code67(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_DIVW, GuardedBinary.getResult(P(p)),                         R(GuardedBinary.getVal1(P(p))), GuardedBinary.getVal2(P(p))));
}
void code68(BURS_TreeNode p) {
    INT_DIV_IMM(P(p), GuardedBinary.getResult(P(p)), R(GuardedBinary.getVal1(P(p))),                   Move.getResult(PR(p)), IC(Move.getVal(PR(p))));
}
void code69(BURS_TreeNode p) {
    INT_REM(P(p), GuardedBinary.getResult(P(p)), R(GuardedBinary.getVal1(P(p))), R(GuardedBinary.getVal2(P(p))));
}
void code70(BURS_TreeNode p) {
    INT_REM_IMM(P(p), GuardedBinary.getResult(P(p)), R(GuardedBinary.getVal1(P(p))), 	          Move.getResult(PR(p)), IC(Move.getVal(PR(p))));
}
void code71(BURS_TreeNode p) {
    EMIT(MIR_Unary.mutate(P(p), PPC_NEG, Unary.getResult(P(p)), Unary.getVal(P(p))));
}
void code72(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_SLWI, Binary.getResult(P(p)),                                       R(Binary.getVal1(P(p))), Binary.getVal2(P(p))));
}
void code73(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_SLW, Binary.getResult(P(p)),                        R(Binary.getVal1(P(p))), R(Binary.getVal2(P(p)))));
}
void code74(BURS_TreeNode p) {
    USHR_SHL(P(p), Binary.getResult(P(p)), R(Binary.getVal1(PL(p))), IC(Binary.getVal2(PL(p))),                     IC(Binary.getVal2(P(p))));
}
void code75(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_SRAWI, Binary.getResult(P(p)), R(Binary.getVal1(P(p))), Binary.getVal2(P(p))));
}
void code76(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_SRAW, Binary.getResult(P(p)),                        R(Binary.getVal1(P(p))), R(Binary.getVal2(P(p)))));
}
void code77(BURS_TreeNode p) {
    AND_USHR(P(p), Binary.getResult(P(p)),                                           R(Binary.getVal1(PL(p))), IC(Binary.getVal2(PL(p))),                           IC(Binary.getVal2(P(p))));
}
void code78(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_SRWI, Binary.getResult(P(p)),                                       R(Binary.getVal1(P(p))), Binary.getVal2(P(p))));
}
void code79(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_SRW, Binary.getResult(P(p)),                        R(Binary.getVal1(P(p))), R(Binary.getVal2(P(p)))));
}
void code80(BURS_TreeNode p) {
    AND_USHR(P(p), Binary.getResult(P(p)), R(Binary.getVal1(PL(p))), 	       IC(Binary.getVal2(PL(p))), IC(Binary.getVal2(P(p))));
}
void code81(BURS_TreeNode p) {
    AND_USHR(P(p), Binary.getResult(P(p)), R(Binary.getVal1(PL(p))),                IC(Move.getVal(PLR(p))), IC(Binary.getVal2(P(p))));
}
void code82(BURS_TreeNode p) {
    SHL_USHR(P(p), Binary.getResult(P(p)), R(Binary.getVal1(PL(p))), 	       IC(Binary.getVal2(PL(p))), IC(Binary.getVal2(P(p))));
}
void code83(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_AND, Binary.getResult(P(p)), R(Binary.getVal1(P(p))), Binary.getVal2(P(p))));
}
void code84(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_ANDIr, Binary.getResult(P(p)), R(Binary.getVal1(P(p))), IC(Binary.getVal2(P(p)))));
}
void code85(BURS_TreeNode p) {
    {                                                                                int mask = IV(Binary.getVal2(P(p)));                                      EMIT(MIR_RotateAndMask.create(PPC_RLWINM, Binary.getResult(P(p)),                                           R(Binary.getVal1(P(p))), IC(0),                                                IC(MaskBegin(mask)), IC(MaskEnd(mask))));                        }
}
void code86(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_NOR, Binary.getResult(P(p)),                                        R(Unary.getVal(PL(p))), Unary.getVal(PR(p))));
}
void code87(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_ANDC, Binary.getResult(P(p)),                                       R(Binary.getVal1(P(p))), Unary.getVal(PR(p))));
}
void code88(BURS_TreeNode p) {
    USHR_AND(P(p), Binary.getResult(P(p)), R(Binary.getVal1(PL(p))),                IC(Binary.getVal2(P(p))), IC(Binary.getVal2(PL(p))));
}
void code89(BURS_TreeNode p) {
    USHR_AND(P(p), Binary.getResult(P(p)), R(Binary.getVal1(PL(p))),                IC(Move.getVal(PR(p))), IC(Binary.getVal2(PL(p))));
}
void code90(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_OR, Binary.getResult(P(p)), R(Binary.getVal1(P(p))), Binary.getVal2(P(p))));
}
void code91(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_ORI, Binary.getResult(P(p)), R(Binary.getVal1(P(p))), Binary.getVal2(P(p))));
}
void code92(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_NAND, Binary.getResult(P(p)), R(Unary.getVal(PL(p))), Unary.getVal(PR(p))));
}
void code93(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_ORC, Binary.getResult(P(p)), R(Binary.getVal1(P(p))), Unary.getVal(PR(p))));
}
void code94(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_XOR, Binary.getResult(P(p)), R(Binary.getVal1(P(p))), Binary.getVal2(P(p))));
}
void code95(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_XORI, Binary.getResult(P(p)), R(Binary.getVal1(P(p))), Binary.getVal2(P(p))));
}
void code96(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_NOR, Unary.getResult(P(p)), R(Unary.getVal(P(p))), Unary.getVal(P(p))));
}
void code97(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_NOR, Unary.getResult(P(p)), R(Binary.getVal1(PL(p))), Binary.getVal2(PL(p))));
}
void code98(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_NAND, Unary.getResult(P(p)), R(Binary.getVal1(PL(p))), Binary.getVal2(PL(p))));
}
void code99(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_EQV, Unary.getResult(P(p)), R(Binary.getVal1(PL(p))), Binary.getVal2(PL(p))));
}
void code100(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_FADDS, Binary.getResult(P(p)),                        R(Binary.getVal1(P(p))), Binary.getVal2(P(p))));
}
void code101(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_FADD, Binary.getResult(P(p)),                                       R(Binary.getVal1(P(p))), Binary.getVal2(P(p))));
}
void code102(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_FMULS, Binary.getResult(P(p)),                                      R(Binary.getVal1(P(p))), Binary.getVal2(P(p))));
}
void code103(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_FMUL, Binary.getResult(P(p)),                                       R(Binary.getVal1(P(p))), Binary.getVal2(P(p))));
}
void code104(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_FSUBS, Binary.getResult(P(p)),                                      R(Binary.getVal1(P(p))), Binary.getVal2(P(p))));
}
void code105(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_FSUB, Binary.getResult(P(p)),                                       R(Binary.getVal1(P(p))), Binary.getVal2(P(p))));
}
void code106(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_FDIVS, Binary.getResult(P(p)),                                      R(Binary.getVal1(P(p))), Binary.getVal2(P(p))));
}
void code107(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_FDIV, Binary.getResult(P(p)),                                       R(Binary.getVal1(P(p))), Binary.getVal2(P(p))));
}
void code108(BURS_TreeNode p) {
    EMIT(MIR_Unary.mutate(P(p), PPC_FNEG, Unary.getResult(P(p)), Unary.getVal(P(p))));
}
void code109(BURS_TreeNode p) {
    EMIT(MIR_Unary.mutate(P(p), PPC_FNEG, Unary.getResult(P(p)), Unary.getVal(P(p))));
}
void code110(BURS_TreeNode p) {
    EMIT(MIR_Unary.mutate(P(p), PPC_FSQRTS, Unary.getResult(P(p)), Unary.getVal(P(p))));
}
void code111(BURS_TreeNode p) {
    EMIT(MIR_Unary.mutate(P(p), PPC_FSQRT, Unary.getResult(P(p)), Unary.getVal(P(p))));
}
void code112(BURS_TreeNode p) {
    EMIT(MIR_Ternary.mutate(P(p), PPC_FMADDS, Binary.getResult(P(p)),             		        R(Binary.getVal1(PL(p))), R(Binary.getVal2(PL(p))),   		        R(Binary.getVal2(P(p)))));
}
void code113(BURS_TreeNode p) {
    EMIT(MIR_Ternary.mutate(P(p), PPC_FMADD, Binary.getResult(P(p)),              		        R(Binary.getVal1(PL(p))), R(Binary.getVal2(PL(p))),   		        R(Binary.getVal2(P(p)))));
}
void code114(BURS_TreeNode p) {
    EMIT(MIR_Ternary.mutate(P(p), PPC_FMADDS, Binary.getResult(P(p)),             			R(Binary.getVal1(PR(p))), R(Binary.getVal2(PR(p))),   			R(Binary.getVal1(P(p)))));
}
void code115(BURS_TreeNode p) {
    EMIT(MIR_Ternary.mutate(P(p), PPC_FMADD, Binary.getResult(P(p)),              			R(Binary.getVal1(PR(p))), R(Binary.getVal2(PR(p))),   			R(Binary.getVal1(P(p)))));
}
void code116(BURS_TreeNode p) {
    EMIT(MIR_Ternary.mutate(P(p), PPC_FMSUBS, Binary.getResult(P(p)),             		        R(Binary.getVal1(PL(p))), R(Binary.getVal2(PL(p))),   		        R(Binary.getVal2(P(p)))));
}
void code117(BURS_TreeNode p) {
    EMIT(MIR_Ternary.mutate(P(p), PPC_FMSUB, Binary.getResult(P(p)),              		        R(Binary.getVal1(PL(p))), R(Binary.getVal2(PL(p))),   		        R(Binary.getVal2(P(p)))));
}
void code118(BURS_TreeNode p) {
    EMIT(MIR_Ternary.mutate(P(p), PPC_FNMADDS, Unary.getResult(P(p)),             		        R(Binary.getVal1(PLL(p))), R(Binary.getVal2(PLL(p))), 		        R(Binary.getVal2(PL(p)))));
}
void code119(BURS_TreeNode p) {
    EMIT(MIR_Ternary.mutate(P(p), PPC_FNMADD, Unary.getResult(P(p)),              		        R(Binary.getVal1(PLL(p))), R(Binary.getVal2(PLL(p))), 		        R(Binary.getVal2(PL(p)))));
}
void code120(BURS_TreeNode p) {
    EMIT(MIR_Ternary.mutate(P(p), PPC_FNMADDS, Unary.getResult(P(p)),             			R(Binary.getVal1(PLR(p))), R(Binary.getVal2(PLR(p))), 			R(Binary.getVal1(PL(p)))));
}
void code121(BURS_TreeNode p) {
    EMIT(MIR_Ternary.mutate(P(p), PPC_FNMADD, Unary.getResult(P(p)),             			R(Binary.getVal1(PLR(p))), R(Binary.getVal2(PLR(p))),			R(Binary.getVal1(PL(p)))));
}
void code122(BURS_TreeNode p) {
    EMIT(MIR_Ternary.mutate(P(p), PPC_FNMSUBS, Unary.getResult(P(p)),             		        R(Binary.getVal1(PLL(p))), R(Binary.getVal2(PLL(p))), 		        R(Binary.getVal2(PL(p)))));
}
void code123(BURS_TreeNode p) {
    EMIT(MIR_Ternary.mutate(P(p), PPC_FNMSUB, Unary.getResult(P(p)),              		        R(Binary.getVal1(PLL(p))), R(Binary.getVal2(PLL(p))), 		        R(Binary.getVal2(PL(p)))));
}
void code124(BURS_TreeNode p) {
    EMIT(MIR_Unary.mutate(P(p), PPC_EXTSB, Unary.getResult(P(p)), Unary.getVal(P(p))));
}
void code125(BURS_TreeNode p) {
    EMIT(MIR_RotateAndMask.create(PPC_RLWINM, Unary.getResult(P(p)), null,                                      R(Unary.getVal(P(p))), IC(0), IC(16), IC(31)));
}
void code126(BURS_TreeNode p) {
    EMIT(MIR_Unary.mutate(P(p), PPC_EXTSH, Unary.getResult(P(p)), Unary.getVal(P(p))));
}
void code127(BURS_TreeNode p) {
    INT_2DOUBLE(P(p), Unary.getResult(P(p)), R(Unary.getVal(P(p))));
}
void code128(BURS_TreeNode p) {
    INT_2DOUBLE(P(p), Unary.getResult(P(p)), R(Unary.getVal(P(p))));
}
void code129(BURS_TreeNode p) {
    EMIT(P(p));  // Leave for ComplexLIR2MIRExpansion
}
void code130(BURS_TreeNode p) {
    EMIT(MIR_Move.mutate(P(p), PPC_FMR, Unary.getResult(P(p)), R(Unary.getVal(P(p)))));
}
void code131(BURS_TreeNode p) {
    EMIT(P(p));  // Leave for ComplexLIR2MIRExpansionLeave
}
void code132(BURS_TreeNode p) {
    EMIT(MIR_Unary.mutate(P(p), PPC_FRSP, Unary.getResult(P(p)), Unary.getVal(P(p))));
}
void code133(BURS_TreeNode p) {
    FPR2GPR_32(P(p));
}
void code134(BURS_TreeNode p) {
    GPR2FPR_32(P(p));
}
void code135(BURS_TreeNode p) {
    EMIT(MIR_Move.mutate(P(p), PPC_MOVE, Move.getResult(P(p)), R(Move.getVal(P(p)))));
}
void code136(BURS_TreeNode p) {
    EMIT(MIR_Unary.mutate(P(p), PPC_LDI, Move.getResult(P(p)), Move.getVal(P(p))));
}
void code137(BURS_TreeNode p) {
    EMIT(MIR_Unary.mutate(P(p), PPC_LDIS, Move.getResult(P(p)), SRI(IV(Move.getVal(P(p))), 16)));
}
void code138(BURS_TreeNode p) {
    {                                                                                int one = IV(Move.getVal(P(p)));                                          EMIT(MIR_Unary.create(PPC_LDIS, Move.getResult(P(p)), CAU16(one)));           EMIT(MIR_Binary.mutate(P(p), PPC_ADDI, Move.getResult(P(p)).copyRO(),                                Move.getResult(P(p)).copyRO(), CAL16(one)));        }
}
void code139(BURS_TreeNode p) {
    EMIT(MIR_Move.mutate(P(p), PPC_FMR, Move.getResult(P(p)), R(Move.getVal(P(p)))));
}
void code140(BURS_TreeNode p) {
    EMIT(MIR_Move.mutate(P(p), PPC_FMR, Move.getResult(P(p)), R(Move.getVal(P(p)))));
}
void code141(BURS_TreeNode p) {
    BYTE_LOAD(P(p), PPC_LBZ, Load.getResult(P(p)), R(Load.getAddress(P(p))), Load.getOffset(P(p)),           Load.getLocation(P(p)), Load.getGuard(P(p)));
}
void code142(BURS_TreeNode p) {
    BYTE_LOAD(P(p), PPC_LBZX, Load.getResult(P(p)), R(Load.getAddress(P(p))), Load.getOffset(P(p)),           Load.getLocation(P(p)), Load.getGuard(P(p)));
}
void code143(BURS_TreeNode p) {
    EMIT(MIR_Load.mutate(PL(p), PPC_LBZX, Binary.getResult(P(p)), R(Load.getAddress(PL(p))),                      Load.getOffset(PL(p)), Load.getLocation(PL(p)),                      Load.getGuard(PL(p))));
}
void code144(BURS_TreeNode p) {
    EMIT(MIR_Load.mutate(PL(p), PPC_LBZ, Binary.getResult(P(p)), R(Load.getAddress(PL(p))),                      Load.getOffset(PL(p)), Load.getLocation(PL(p)),                      Load.getGuard(PL(p))));
}
void code145(BURS_TreeNode p) {
    EMIT(MIR_Load.mutate(P(p), PPC_LBZ, Load.getResult(P(p)),                                 R(Load.getAddress(P(p))), Load.getOffset(P(p)),                      Load.getLocation(P(p)), Load.getGuard(P(p))));
}
void code146(BURS_TreeNode p) {
    EMIT(MIR_Load.mutate(P(p), PPC_LBZX, Load.getResult(P(p)),                                R(Load.getAddress(P(p))), Load.getOffset(P(p)), 	             Load.getLocation(P(p)), Load.getGuard(P(p))));
}
void code147(BURS_TreeNode p) {
    EMIT(MIR_Load.mutate(P(p), PPC_LHA, Load.getResult(P(p)),                                 R(Load.getAddress(P(p))), Load.getOffset(P(p)),    	             Load.getLocation(P(p)), Load.getGuard(P(p))));
}
void code148(BURS_TreeNode p) {
    EMIT(MIR_Load.mutate(P(p), PPC_LHAX, Load.getResult(P(p)),                                R(Load.getAddress(P(p))), Load.getOffset(P(p)),   	             Load.getLocation(P(p)), Load.getGuard(P(p))));
}
void code149(BURS_TreeNode p) {
    EMIT(MIR_Load.mutate(P(p), PPC_LHZ, Load.getResult(P(p)),                                 R(Load.getAddress(P(p))), Load.getOffset(P(p)), 	             Load.getLocation(P(p)), Load.getGuard(P(p))));
}
void code150(BURS_TreeNode p) {
    EMIT(MIR_Load.mutate(P(p), PPC_LHZX, Load.getResult(P(p)),                                R(Load.getAddress(P(p))), Load.getOffset(P(p)), 		     Load.getLocation(P(p)), Load.getGuard(P(p))));
}
void code151(BURS_TreeNode p) {
    EMIT(MIR_Load.mutate(P(p), PPC_LFS, Load.getResult(P(p)),                                          R(Load.getAddress(P(p))), Load.getOffset(P(p)), 			      Load.getLocation(P(p)), Load.getGuard(P(p))));
}
void code152(BURS_TreeNode p) {
    {                                                                                Address val = AV(Move.getVal(PR(p)));                                         EMIT(MIR_Binary.create(PPC_ADDIS, Move.getResult(PR(p)),                                             R(Load.getAddress(P(p))), CAU16(val)));                EMIT(MIR_Load.mutate(P(p), PPC_LFS, Load.getResult(P(p)),                                          Move.getResult(PR(p)).copyRO(), CAL16(val),  			         Load.getLocation(P(p)),                                                       Load.getGuard(P(p))));                      }
}
void code153(BURS_TreeNode p) {
    EMIT(MIR_Load.mutate(P(p), PPC_LFSX, Load.getResult(P(p)),                                         R(Load.getAddress(P(p))), Load.getOffset(P(p)), 			      Load.getLocation(P(p)), Load.getGuard(P(p))));
}
void code154(BURS_TreeNode p) {
    EMIT(MIR_Load.mutate(P(p), PPC_LFD, Load.getResult(P(p)),                                          R(Load.getAddress(P(p))), Load.getOffset(P(p)), 			      Load.getLocation(P(p)), Load.getGuard(P(p))));
}
void code155(BURS_TreeNode p) {
    {                                                                                Address val = AV(Move.getVal(PR(p)));                                         EMIT(MIR_Binary.create(PPC_ADDIS, Move.getResult(PR(p)),                                             R(Load.getAddress(P(p))), CAU16(val)));                EMIT(MIR_Load.mutate(P(p), PPC_LFD, Load.getResult(P(p)),                                          Move.getResult(PR(p)).copyRO(), CAL16(val),  				 Load.getLocation(P(p)),                                                       Load.getGuard(P(p))));                      }
}
void code156(BURS_TreeNode p) {
    EMIT(MIR_Load.mutate(P(p), PPC_LFDX, Load.getResult(P(p)),                                         R(Load.getAddress(P(p))), Load.getOffset(P(p)), 			      Load.getLocation(P(p)), Load.getGuard(P(p))));
}
void code157(BURS_TreeNode p) {
    EMIT(MIR_Load.mutate(P(p), PPC_LInt, Load.getResult(P(p)),                                 R(Load.getAddress(P(p))), Load.getOffset(P(p)),                      Load.getLocation(P(p)), Load.getGuard(P(p))));
}
void code158(BURS_TreeNode p) {
    {                                                                                Address val = AV(Move.getVal(PR(p)));                                         EMIT(MIR_Binary.create(PPC_ADDIS, Move.getResult(PR(p)),                                             R(Load.getAddress(P(p))), CAU16(val)));                EMIT(MIR_Load.mutate(P(p), PPC_LInt, Load.getResult(P(p)),                                          Move.getResult(PR(p)).copyRO(), CAL16(val),                                            Load.getLocation(P(p)),                                                       Load.getGuard(P(p))));                      }
}
void code159(BURS_TreeNode p) {
    EMIT(MIR_Load.mutate(P(p), PPC_LIntX, Load.getResult(P(p)),                                R(Load.getAddress(P(p))), Load.getOffset(P(p)),                      Load.getLocation(P(p)), Load.getGuard(P(p))));
}
void code160(BURS_TreeNode p) {
    EMIT(MIR_Load.mutate(P(p), PPC_LIntX, Load.getResult(P(p)),                                         R(Binary.getVal1(PL(p))), Binary.getVal2(PL(p)),                               Load.getLocation(P(p)), Load.getGuard(P(p))));
}
void code161(BURS_TreeNode p) {
    EMIT(MIR_Load.mutate(P(p), PPC_LInt, Load.getResult(P(p)),                                          R(Binary.getVal1(PL(p))), IC(VR(p)+VLR(p)),                               Load.getLocation(P(p)), Load.getGuard(P(p))));
}
void code162(BURS_TreeNode p) {
    EMIT(MIR_Store.mutate(P(p), PPC_STB, R(Store.getValue(P(p))),                                       R(Store.getAddress(P(p))),                                                    Store.getOffset(P(p)),                                                        Store.getLocation(P(p)),                                                      Store.getGuard(P(p))));
}
void code163(BURS_TreeNode p) {
    EMIT(MIR_Store.mutate(P(p), PPC_STBX, R(Store.getValue(P(p))),                                      R(Store.getAddress(P(p))),                                                    Store.getOffset(P(p)),                         			       Store.getLocation(P(p)),                                                      Store.getGuard(P(p))));
}
void code164(BURS_TreeNode p) {
    EMIT(MIR_Store.mutate(P(p), PPC_STB, R(Unary.getVal(PL(p))),                                        R(Store.getAddress(P(p))),                                                    Store.getOffset(P(p)),                         			       Store.getLocation(P(p)),                                                      Store.getGuard(P(p))));
}
void code165(BURS_TreeNode p) {
    EMIT(MIR_Store.mutate(P(p), PPC_STBX, R(Unary.getVal(PL(p))),                                       R(Store.getAddress(P(p))),                                                    Store.getOffset(P(p)),                         			       Store.getLocation(P(p)),                                                      Store.getGuard(P(p))));
}
void code166(BURS_TreeNode p) {
    EMIT(MIR_Store.mutate(P(p), PPC_STH, R(Store.getValue(P(p))),                                       R(Store.getAddress(P(p))),                                                    Store.getOffset(P(p)),                         			       Store.getLocation(P(p)),                                                      Store.getGuard(P(p))));
}
void code167(BURS_TreeNode p) {
    EMIT(MIR_Store.mutate(P(p), PPC_STHX, R(Store.getValue(P(p))),                                      R(Store.getAddress(P(p))),                                                    Store.getOffset(P(p)),                         			       Store.getLocation(P(p)),                                                      Store.getGuard(P(p))));
}
void code168(BURS_TreeNode p) {
    EMIT(MIR_Store.mutate(P(p), PPC_STH, R(Unary.getVal(PL(p))),                                        R(Store.getAddress(P(p))),                                                    Store.getOffset(P(p)),                         			       Store.getLocation(P(p)),                                                      Store.getGuard(P(p))));
}
void code169(BURS_TreeNode p) {
    EMIT(MIR_Store.mutate(P(p), PPC_STHX, R(Unary.getVal(PL(p))),                                       R(Store.getAddress(P(p))),                                                    Store.getOffset(P(p)),                         			       Store.getLocation(P(p)),                                                      Store.getGuard(P(p))));
}
void code170(BURS_TreeNode p) {
    EMIT(MIR_Store.mutate(P(p), PPC_STH, R(Unary.getVal(PL(p))),                                        R(Store.getAddress(P(p))),                                                    Store.getOffset(P(p)),                         			       Store.getLocation(P(p)),                                                      Store.getGuard(P(p))));
}
void code171(BURS_TreeNode p) {
    EMIT(MIR_Store.mutate(P(p), PPC_STHX, R(Unary.getVal(PL(p))),                                       R(Store.getAddress(P(p))),                                                    Store.getOffset(P(p)),                         			       Store.getLocation(P(p)),                                                      Store.getGuard(P(p))));
}
void code172(BURS_TreeNode p) {
    EMIT(MIR_Store.mutate(P(p), PPC_STW, R(Store.getValue(P(p))),                                       R(Store.getAddress(P(p))),                                                    Store.getOffset(P(p)),                         			       Store.getLocation(P(p)),                                                      Store.getGuard(P(p))));
}
void code173(BURS_TreeNode p) {
    {                                                                                Address val = AV(Move.getVal(PRR(p)));                                        EMIT(MIR_Binary.create(PPC_ADDIS, Move.getResult(PRR(p)),                                            R(Store.getAddress(P(p))), CAU16(val)));               EMIT(MIR_Store.mutate(P(p), PPC_STW, R(Store.getValue(P(p))),                                       Move.getResult(PRR(p)).copyRO(), CAL16(val),				  Store.getLocation(P(p)),                                                      Store.getGuard(P(p))));                    }
}
void code174(BURS_TreeNode p) {
    EMIT(MIR_Store.mutate(P(p), PPC_STWX, R(Store.getValue(P(p))),                                      R(Store.getAddress(P(p))),                                                    Store.getOffset(P(p)),                         			       Store.getLocation(P(p)),                                                      Store.getGuard(P(p))));
}
void code175(BURS_TreeNode p) {
    EMIT(MIR_Store.mutate(P(p), PPC_STW, R(Store.getValue(P(p))),                                       R(Binary.getVal1(PRL(p))),                                                    IC(VRR(p) + VRLR(p)), 			       Store.getLocation(P(p)),                                                      Store.getGuard(P(p))));
}
void code176(BURS_TreeNode p) {
    EMIT(MIR_Store.mutate(P(p), PPC_STFS, R(Store.getValue(P(p))),                                      R(Store.getAddress(P(p))),                                                    Store.getOffset(P(p)),                         			       Store.getLocation(P(p)),                                                      Store.getGuard(P(p))));
}
void code177(BURS_TreeNode p) {
    {                                                                                Address val = AV(Move.getVal(PRR(p)));                                        EMIT(MIR_Binary.create(PPC_ADDIS, Move.getResult(PRR(p)),                                            R(Store.getAddress(P(p))), CAU16(val)));               EMIT(MIR_Store.mutate(P(p), PPC_STFS, R(Store.getValue(P(p))),                                      Move.getResult(PRR(p)).copyRO(), CAL16(val),				  Store.getLocation(P(p)),                                                      Store.getGuard(P(p))));                    }
}
void code178(BURS_TreeNode p) {
    EMIT(MIR_Store.mutate(P(p), PPC_STFSX, R(Store.getValue(P(p))),                                     R(Store.getAddress(P(p))),                                                    Store.getOffset(P(p)),                         			       Store.getLocation(P(p)),                                                      Store.getGuard(P(p))));
}
void code179(BURS_TreeNode p) {
    EMIT(MIR_Store.mutate(P(p), PPC_STFD, R(Store.getValue(P(p))),                                      R(Store.getAddress(P(p))),                                                    Store.getOffset(P(p)),                         			       Store.getLocation(P(p)),                                                      Store.getGuard(P(p))));
}
void code180(BURS_TreeNode p) {
    {                                                                                Address val = AV(Move.getVal(PRR(p)));                                        EMIT(MIR_Binary.create(PPC_ADDIS, Move.getResult(PRR(p)),                                            R(Store.getAddress(P(p))), CAU16(val)));               EMIT(MIR_Store.mutate(P(p), PPC_STFD, R(Store.getValue(P(p))),                                      Move.getResult(PRR(p)).copyRO(), CAL16(val),				  Store.getLocation(P(p)),                                                      Store.getGuard(P(p))));                    }
}
void code181(BURS_TreeNode p) {
    EMIT(MIR_Store.mutate(P(p), PPC_STFDX, R(Store.getValue(P(p))),                                     R(Store.getAddress(P(p))),                                                    Store.getOffset(P(p)),                         			       Store.getLocation(P(p)),                                                      Store.getGuard(P(p))));
}
void code182(BURS_TreeNode p) {
    EMIT(CPOS(P(p), Move.create(GUARD_MOVE, IfCmp.getGuardResult(P(p)), new TrueGuardOperand()))); CMP(P(p), R(IfCmp.getVal1(P(p))), IfCmp.getVal2(P(p)), IfCmp.getCond(P(p)), false);
}
void code183(BURS_TreeNode p) {
    EMIT(CPOS(P(p), Move.create(GUARD_MOVE, IfCmp.getGuardResult(P(p)), new TrueGuardOperand()))); CMP(P(p), R(IfCmp.getVal1(P(p))), IfCmp.getVal2(P(p)), IfCmp.getCond(P(p)), true);
}
void code184(BURS_TreeNode p) {
    EMIT(CPOS(P(p), Move.create(GUARD_MOVE, IfCmp.getGuardResult(P(p)), new TrueGuardOperand()))); CMP_ZERO(P(p), PPC_EXTSBr, Unary.getResult(PL(p)),                              Unary.getVal(PL(p)), IfCmp.getCond(P(p)));
}
void code185(BURS_TreeNode p) {
    EMIT(CPOS(P(p), Move.create(GUARD_MOVE, IfCmp.getGuardResult(P(p)), new TrueGuardOperand()))); CMP_ZERO(P(p), PPC_EXTSHr, Unary.getResult(PL(p)),                              Unary.getVal(PL(p)), IfCmp.getCond(P(p)));
}
void code186(BURS_TreeNode p) {
    EMIT(CPOS(P(p), Move.create(GUARD_MOVE, IfCmp.getGuardResult(P(p)), new TrueGuardOperand()))); CMP_ZERO(P(p), PPC_SRWr, Binary.getResult(PL(p)),                               R(Binary.getVal1(PL(p))), Binary.getVal2(PL(p)), IfCmp.getCond(P(p)));
}
void code187(BURS_TreeNode p) {
    EMIT(CPOS(P(p), Move.create(GUARD_MOVE, IfCmp.getGuardResult(P(p)), new TrueGuardOperand()))); CMP_ZERO(P(p), PPC_SLWr, Binary.getResult(PL(p)),                               R(Binary.getVal1(PL(p))), Binary.getVal2(PL(p)), IfCmp.getCond(P(p)));
}
void code188(BURS_TreeNode p) {
    EMIT(CPOS(P(p), Move.create(GUARD_MOVE, IfCmp.getGuardResult(P(p)), new TrueGuardOperand()))); CMP_ZERO(P(p), PPC_SRAWr, Binary.getResult(PL(p)),                              R(Binary.getVal1(PL(p))), Binary.getVal2(PL(p)), IfCmp.getCond(P(p)));
}
void code189(BURS_TreeNode p) {
    EMIT(CPOS(P(p), Move.create(GUARD_MOVE, IfCmp.getGuardResult(P(p)), new TrueGuardOperand()))); CMP_ZERO(P(p), PPC_SRWIr, Binary.getResult(PL(p)),                              R(Binary.getVal1(PL(p))), Binary.getVal2(PL(p)), IfCmp.getCond(P(p)));
}
void code190(BURS_TreeNode p) {
    EMIT(CPOS(P(p), Move.create(GUARD_MOVE, IfCmp.getGuardResult(P(p)), new TrueGuardOperand()))); CMP_ZERO(P(p), PPC_SLWIr, Binary.getResult(PL(p)),                              R(Binary.getVal1(PL(p))), Binary.getVal2(PL(p)), IfCmp.getCond(P(p)));
}
void code191(BURS_TreeNode p) {
    EMIT(CPOS(P(p), Move.create(GUARD_MOVE, IfCmp.getGuardResult(P(p)), new TrueGuardOperand()))); CMP_ZERO(P(p), PPC_SRAWIr, Binary.getResult(PL(p)),                             R(Binary.getVal1(PL(p))), Binary.getVal2(PL(p)), IfCmp.getCond(P(p)));
}
void code192(BURS_TreeNode p) {
    EMIT(CPOS(P(p), Move.create(GUARD_MOVE, IfCmp.getGuardResult(P(p)), new TrueGuardOperand()))); CMP_ZERO(P(p), PPC_ANDIr, Binary.getResult(PL(p)),                              R(Binary.getVal1(PL(p))), Binary.getVal2(PL(p)), IfCmp.getCond(P(p)));
}
void code193(BURS_TreeNode p) {
    EMIT(CPOS(P(p), Move.create(GUARD_MOVE, IfCmp.getGuardResult(P(p)), new TrueGuardOperand()))); EMIT_BOOLCMP_BRANCH(IfCmp.getTarget(P(p)), IfCmp.getBranchProfile(P(p)));
}
void code194(BURS_TreeNode p) {
    EMIT(CPOS(P(p), Move.create(GUARD_MOVE, IfCmp.getGuardResult(P(p)), new TrueGuardOperand()))); FLIP_BOOLCMP(); EMIT_BOOLCMP_BRANCH(IfCmp.getTarget(P(p)), IfCmp.getBranchProfile(P(p)));
}
void code195(BURS_TreeNode p) {
    EMIT(CPOS(P(p), Move.create(GUARD_MOVE, IfCmp.getGuardResult(P(p)), new TrueGuardOperand()))); EMIT_BOOLCMP_BRANCH(IfCmp.getTarget(P(p)), IfCmp.getBranchProfile(P(p)));
}
void code196(BURS_TreeNode p) {
    EMIT(CPOS(P(p), Move.create(GUARD_MOVE, IfCmp.getGuardResult(P(p)), new TrueGuardOperand()))); FLIP_BOOLCMP(); EMIT_BOOLCMP_BRANCH(IfCmp.getTarget(P(p)), IfCmp.getBranchProfile(P(p)));
}
void code197(BURS_TreeNode p) {
    EMIT(CPOS(P(p), Move.create(GUARD_MOVE, IfCmp2.getGuardResult(P(p)), new TrueGuardOperand()))); CMP2(P(p), R(IfCmp2.getVal1(P(p))), IfCmp2.getVal2(P(p)), IfCmp2.getCond1(P(p)), IfCmp2.getCond2(P(p)), false);
}
void code198(BURS_TreeNode p) {
    EMIT(CPOS(P(p), Move.create(GUARD_MOVE, IfCmp2.getGuardResult(P(p)), new TrueGuardOperand()))); CMP2(P(p), R(IfCmp2.getVal1(P(p))), IfCmp2.getVal2(P(p)), IfCmp2.getCond1(P(p)), IfCmp2.getCond2(P(p)), true);
}
void code199(BURS_TreeNode p) {
    EMIT(CPOS(P(p), Move.create(GUARD_MOVE, IfCmp.getGuardResult(P(p)), new TrueGuardOperand()))); DOUBLE_IFCMP(P(p), R(IfCmp.getVal1(P(p))), IfCmp.getVal2(P(p)));
}
void code200(BURS_TreeNode p) {
    EMIT(CPOS(P(p), Move.create(GUARD_MOVE, IfCmp.getGuardResult(P(p)), new TrueGuardOperand()))); DOUBLE_IFCMP(P(p), R(IfCmp.getVal1(P(p))), IfCmp.getVal2(P(p)));
}
void code201(BURS_TreeNode p) {
    EMIT (P(p)); //  Leave for ComplexLIR2MIRExpansion
}
void code202(BURS_TreeNode p) {
    EMIT (P(p)); //  Leave for ComplexLIR2MIRExpansion
}
void code203(BURS_TreeNode p) {
    EMIT (P(p)); //  Leave for ComplexLIR2MIRExpansion
}
void code204(BURS_TreeNode p) {
    EMIT (P(p)); //  Leave for ComplexLIR2MIRExpansion
}
void code205(BURS_TreeNode p) {
    EMIT(MIR_Branch.mutate(P(p), PPC_B, Goto.getTarget(P(p))));
}
void code206(BURS_TreeNode p) {
    RETURN(P(p), null);
}
void code207(BURS_TreeNode p) {
    RETURN(P(p), Return.getVal(P(p)));
}
void code208(BURS_TreeNode p) {
    CALL(P(p));
}
void code209(BURS_TreeNode p) {
    CALL(P(p));
}
void code210(BURS_TreeNode p) {
    SYSCALL(P(p));
}
void code211(BURS_TreeNode p) {
    EMIT(P(p));  // Leave for ComplexLIR2MIRExpansion
}
void code213(BURS_TreeNode p) {
    OSR(burs, P(p));
}
void code214(BURS_TreeNode p) {
    EMIT(MIR_Load.mutate(P(p), PPC_LWARX, Prepare.getResult(P(p)),                                 R(Prepare.getAddress(P(p))), Prepare.getOffset(P(p)),                                         Prepare.getLocation(P(p)),                                                Prepare.getGuard(P(p))));
}
void code215(BURS_TreeNode p) {
    EMIT(MIR_Load.mutate(P(p), PPC_LAddrARX, Prepare.getResult(P(p)),                                 R(Prepare.getAddress(P(p))), Prepare.getOffset(P(p)),                                         Prepare.getLocation(P(p)),                                                Prepare.getGuard(P(p))));
}
void code216(BURS_TreeNode p) {
    EMIT(P(p));  // Leave for ComplexLIR2MIRExpansion
}
void code217(BURS_TreeNode p) {
    EMIT(P(p));  // Leave for ComplexLIR2MIRExpansion
}
void code218(BURS_TreeNode p) {
    PROLOGUE(P(p));
}
void code219(BURS_TreeNode p) {
    LONG_ADD(P(p), Binary.getResult(P(p)), R(Binary.getVal1(P(p))), R(Binary.getVal2(P(p))));
}
void code220(BURS_TreeNode p) {
    LONG_SUB(P(p), Binary.getResult(P(p)), R(Binary.getVal1(P(p))), R(Binary.getVal2(P(p))));
}
void code221(BURS_TreeNode p) {
    LONG_MUL(P(p), Binary.getResult(P(p)), R(Binary.getVal1(P(p))), R(Binary.getVal2(P(p))));
}
void code222(BURS_TreeNode p) {
    LONG_NEG(P(p), Unary.getResult(P(p)), R(Unary.getVal(P(p))));
}
void code223(BURS_TreeNode p) {
    LONG_SHL(P(p), Binary.getResult(P(p)), R(Binary.getVal1(P(p))), R(Binary.getVal2(P(p))));
}
void code224(BURS_TreeNode p) {
    LONG_SHL_IMM(P(p), Binary.getResult(P(p)), R(Binary.getVal1(P(p))), IC(Binary.getVal2(P(p))));
}
void code225(BURS_TreeNode p) {
    EMIT(P(p));  // Leave for ComplexLIR2MIRExpansion
}
void code226(BURS_TreeNode p) {
    LONG_SHR_IMM(P(p), Binary.getResult(P(p)), R(Binary.getVal1(P(p))), IC(Binary.getVal2(P(p))));
}
void code227(BURS_TreeNode p) {
    LONG_USHR(P(p), Binary.getResult(P(p)), R(Binary.getVal1(P(p))), R(Binary.getVal2(P(p))));
}
void code228(BURS_TreeNode p) {
    LONG_USHR_IMM(P(p), Binary.getResult(P(p)), R(Binary.getVal1(P(p))), IC(Binary.getVal2(P(p))));
}
void code229(BURS_TreeNode p) {
    LONG_LOG(P(p), PPC_AND, Binary.getResult(P(p)), R(Binary.getVal1(P(p))), R(Binary.getVal2(P(p))));
}
void code230(BURS_TreeNode p) {
    LONG_LOG(P(p), PPC_OR, Binary.getResult(P(p)), R(Binary.getVal1(P(p))), R(Binary.getVal2(P(p))));
}
void code231(BURS_TreeNode p) {
    LONG_LOG(P(p), PPC_XOR, Binary.getResult(P(p)), R(Binary.getVal1(P(p))), R(Binary.getVal2(P(p))));
}
void code232(BURS_TreeNode p) {
    LONG_NOT(P(p), Unary.getResult(P(p)), R(Unary.getVal(P(p))));
}
void code233(BURS_TreeNode p) {
    INT_2LONG(P(p), Unary.getResult(P(p)), R(Unary.getVal(P(p))));
}
void code234(BURS_TreeNode p) {
    LONG_2INT(P(p), Unary.getResult(P(p)), R(Unary.getVal(P(p))));
}
void code235(BURS_TreeNode p) {
    FPR2GPR_64(P(p));
}
void code236(BURS_TreeNode p) {
    GPR2FPR_64(P(p));
}
void code237(BURS_TreeNode p) {
    LONG_CONSTANT(P(p), Move.getResult(P(p)), LC(Move.getVal(P(p))));
}
void code238(BURS_TreeNode p) {
    LONG_MOVE(P(p), Move.getResult(P(p)), R(Move.getVal(P(p))));
}
void code239(BURS_TreeNode p) {
    EMIT (P(p)); //  Leave for ComplexLIR2MIRExpansion
}
void code240(BURS_TreeNode p) {
    EMIT(P(p)); //  Leave for ComplexLIR2MIRExpansion
}
void code241(BURS_TreeNode p) {
    {                                                                                ConditionOperand c = IfCmp.getCond(P(p)).flipCode();                      EMIT(MIR_Store.create(PPC_STWCXr, R(Attempt.getNewValue(PL(p))),                                   R(Attempt.getAddress(PL(p))), Attempt.getOffset(PL(p)),                                             Attempt.getLocation(PL(p)),                                                  Attempt.getGuard(PL(p))));                   EMIT(MIR_CondBranch.mutate(P(p), PPC_BCOND, CR(0),                                                       new PowerPCConditionOperand(c),                                           IfCmp.getTarget(P(p)),                                                       IfCmp.getBranchProfile(P(p))));                 }
}
void code242(BURS_TreeNode p) {
    {                                                                                ConditionOperand c = IfCmp.getCond(P(p)).flipCode();                      EMIT(MIR_Store.create(PPC_STWCXr, R(Attempt.getNewValue(PL(p))),                                   R(Attempt.getAddress(PL(p))), Attempt.getOffset(PL(p)),                                             Attempt.getLocation(PL(p)),                                                  Attempt.getGuard(PL(p))));                   EMIT(MIR_CondBranch.mutate(P(p), PPC_BCOND, CR(0),                                                       new PowerPCConditionOperand(c),                                           IfCmp.getTarget(P(p)),                                                       IfCmp.getBranchProfile(P(p))));                 }
}
void code243(BURS_TreeNode p) {
    CMP_ZERO(P(p), PPC_NEGr, Unary.getResult(PL(p)),                                Unary.getVal(PL(p)), IfCmp.getCond(P(p)));
}
void code244(BURS_TreeNode p) {
    CMP_ZERO(P(p), PPC_XORr, Unary.getResult(PL(p)),                                R(Unary.getVal(PL(p))), Unary.getVal(PL(p)), IfCmp.getCond(P(p)));
}
void code245(BURS_TreeNode p) {
    CMP_ZERO(P(p), PPC_ADDr, Binary.getResult(PL(p)),                               R(Binary.getVal1(PL(p))), Binary.getVal2(PL(p)), IfCmp.getCond(P(p)));
}
void code246(BURS_TreeNode p) {
    CMP_ZERO(P(p), PPC_ANDr, Binary.getResult(PL(p)),                               R(Binary.getVal1(PL(p))), Binary.getVal2(PL(p)), IfCmp.getCond(P(p)));
}
void code247(BURS_TreeNode p) {
    CMP_ZERO(P(p), PPC_ORr, Binary.getResult(PL(p)),                                R(Binary.getVal1(PL(p))), Binary.getVal2(PL(p)), IfCmp.getCond(P(p)));
}
void code248(BURS_TreeNode p) {
    CMP_ZERO(P(p), PPC_XORr, Binary.getResult(PL(p)),                               R(Binary.getVal1(PL(p))), Binary.getVal2(PL(p)), IfCmp.getCond(P(p)));
}
void code249(BURS_TreeNode p) {
    CMP_ZERO(P(p), PPC_ANDISr, Binary.getResult(PL(p)),                             R(Binary.getVal1(PL(p))),                                                    SRI(IV(Move.getVal(PLR(p))), 16), IfCmp.getCond(P(p)));
}
void code250(BURS_TreeNode p) {
    CMP_ZERO_AND_MASK(P(p), Binary.getResult(PL(p)),                                         R(Binary.getVal1(PL(p))),                                                    IC(Move.getVal(PLR(p))), IfCmp.getCond(P(p)));
}
void code251(BURS_TreeNode p) {
    CMP_ZERO(P(p), PPC_ADDICr, Binary.getResult(PL(p)),                             R(Binary.getVal1(PL(p))), Binary.getVal2(PL(p)), IfCmp.getCond(P(p)));
}
void code252(BURS_TreeNode p) {
    CMP_ZERO(P(p), PPC_ANDCr, Binary.getResult(PL(p)),                              R(Binary.getVal1(PL(p))), Unary.getVal(PLR(p)), IfCmp.getCond(P(p)));
}
void code253(BURS_TreeNode p) {
    CMP_ZERO(P(p), PPC_ORCr, Binary.getResult(PL(p)),                               R(Binary.getVal1(PL(p))), Unary.getVal(PLR(p)), IfCmp.getCond(P(p)));
}
void code254(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_ANDISr, Binary.getResult(P(p)),                                     R(Binary.getVal1(P(p))), IC(Bits.PPCMaskUpper16(VRL(p)))));
}
void code255(BURS_TreeNode p) {
    AND_MASK(P(p), Binary.getResult(P(p)), R(Binary.getVal1(P(p))), IC(Move.getVal(PR(p))));
}
void code256(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_ORIS, Binary.getResult(P(p)), R(Binary.getVal1(P(p))), SRI(IV(Move.getVal(PR(p))), 16)));
}
void code257(BURS_TreeNode p) {
    {                                                                                int c = IV(Move.getVal(PR(p)));                                           EMIT(MIR_Binary.create(PPC_ORI, Binary.getResult(P(p)),                                              R(Binary.getVal1(P(p))), ANDI(c, 0xffff)));            EMIT(MIR_Binary.mutate(P(p), PPC_ORIS, Binary.getResult(P(p)).copyRO(),                              Binary.getResult(P(p)).copyRO(), SRI(c, 16)));      }
}
void code258(BURS_TreeNode p) {
    EMIT(MIR_Binary.mutate(P(p), PPC_XORIS, Binary.getResult(P(p)),                        R(Binary.getVal1(P(p))), SRI(IV(Move.getVal(PR(p))), 16)));
}
void code259(BURS_TreeNode p) {
    EMIT(MIR_Unary.mutate(P(p), PPC_LDI, Move.getResult(P(p)), CAL16(AV(Move.getVal(P(p))))));
}
void code260(BURS_TreeNode p) {
    EMIT(MIR_Unary.mutate(P(p), PPC_LDIS, Move.getResult(P(p)), CAU16(AV(Move.getVal(P(p))))));
}
void code261(BURS_TreeNode p) {
    {                                                                                EMIT(MIR_Unary.create(PPC_LDIS, Move.getResult(P(p)), CAU16(AV(Move.getVal(P(p))))));      EMIT(MIR_Binary.mutate(P(p), PPC_ADDI, Move.getResult(P(p)).copyRO(),                                             Move.getResult(P(p)).copyRO(), CAL16(AV(Move.getVal(P(p))))));   }
}
void code262(BURS_TreeNode p) {
    LONG_LOAD_addi(P(p), Load.getResult(P(p)),                                             R(Load.getAddress(P(p))), IC(Load.getOffset(P(p))), 			   Load.getLocation(P(p)), Load.getGuard(P(p)));
}
void code263(BURS_TreeNode p) {
    LONG_LOAD_addis(P(p), Load.getResult(P(p)),                                             R(Load.getAddress(P(p))),                                                     R(Load.getOffset(P(p))), AC(Move.getVal(PR(p))),   		            Load.getLocation(P(p)), Load.getGuard(P(p)));
}
void code264(BURS_TreeNode p) {
    LONG_LOAD_addx(P(p), Load.getResult(P(p)),                                             R(Load.getAddress(P(p))), R(Load.getOffset(P(p))), 			   Load.getLocation(P(p)), Load.getGuard(P(p)));
}
void code265(BURS_TreeNode p) {
    LONG_STORE_addi(P(p), R(Store.getValue(P(p))),                                          R(Store.getAddress(P(p))),                                                    IC(Store.getOffset(P(p))),                         			    Store.getLocation(P(p)),                                                      Store.getGuard(P(p)));
}
void code266(BURS_TreeNode p) {
    LONG_STORE_addis(P(p), R(Store.getValue(P(p))),                                          R(Store.getAddress(P(p))),                                                    R(Store.getOffset(P(p))),                                                     AC(Move.getVal(PRR(p))),                          			     Store.getLocation(P(p)),                                                      Store.getGuard(P(p)));
}
void code267(BURS_TreeNode p) {
    LONG_STORE_addx(P(p), R(Store.getValue(P(p))),                                          R(Store.getAddress(P(p))),                                                    R(Store.getOffset(P(p))),                         			    Store.getLocation(P(p)),                                                      Store.getGuard(P(p)));
}
void code268(BURS_TreeNode p) {
    EMIT(MIR_Load.mutate(P(p), PPC_LWARX, Prepare.getResult(P(p)),                                 R(Prepare.getAddress(P(p))), Prepare.getOffset(P(p)),                                         Prepare.getLocation(P(p)),                                                Prepare.getGuard(P(p))));
}
void code269(BURS_TreeNode p) {
    EMIT(P(p));  // Leave for ComplexLIR2MIRExpansion
}

public void code(BURS_TreeNode p, int  n, int ruleno) {
  switch(ruleno) {
  case 14: code14(p); break;
  case 15: code15(p); break;
  case 16: code16(p); break;
  case 17: code17(p); break;
  case 18: code18(p); break;
  case 19: code19(p); break;
  case 20: code20(p); break;
  case 21: code21(p); break;
  case 23: code23(p); break;
  case 24: code24(p); break;
  case 25: code25(p); break;
  case 26: code26(p); break;
  case 27: code27(p); break;
  case 28: code28(p); break;
  case 29: code29(p); break;
  case 30: code30(p); break;
  case 31: code31(p); break;
  case 32: code32(p); break;
  case 33: code33(p); break;
  case 34: code34(p); break;
  case 35: code35(p); break;
  case 36: code36(p); break;
  case 37: code37(p); break;
  case 38: code38(p); break;
  case 39: code39(p); break;
  case 40: code40(p); break;
  case 41: code41(p); break;
  case 42: code42(p); break;
  case 43: code43(p); break;
  case 44: code44(p); break;
  case 45: code45(p); break;
  case 46: code46(p); break;
  case 47: code47(p); break;
  case 48: code48(p); break;
  case 49: code49(p); break;
  case 50: code50(p); break;
  case 52: code52(p); break;
  case 54: code54(p); break;
  case 55: code55(p); break;
  case 56: code56(p); break;
  case 57: code57(p); break;
  case 58: code58(p); break;
  case 59: code59(p); break;
  case 60: code60(p); break;
  case 61: code61(p); break;
  case 62: code62(p); break;
  case 63: code63(p); break;
  case 64: code64(p); break;
  case 65: code65(p); break;
  case 66: code66(p); break;
  case 67: code67(p); break;
  case 68: code68(p); break;
  case 69: code69(p); break;
  case 70: code70(p); break;
  case 71: code71(p); break;
  case 72: code72(p); break;
  case 73: code73(p); break;
  case 74: code74(p); break;
  case 75: code75(p); break;
  case 76: code76(p); break;
  case 77: code77(p); break;
  case 78: code78(p); break;
  case 79: code79(p); break;
  case 80: code80(p); break;
  case 81: code81(p); break;
  case 82: code82(p); break;
  case 83: code83(p); break;
  case 84: code84(p); break;
  case 85: code85(p); break;
  case 86: code86(p); break;
  case 87: code87(p); break;
  case 88: code88(p); break;
  case 89: code89(p); break;
  case 90: code90(p); break;
  case 91: code91(p); break;
  case 92: code92(p); break;
  case 93: code93(p); break;
  case 94: code94(p); break;
  case 95: code95(p); break;
  case 96: code96(p); break;
  case 97: code97(p); break;
  case 98: code98(p); break;
  case 99: code99(p); break;
  case 100: code100(p); break;
  case 101: code101(p); break;
  case 102: code102(p); break;
  case 103: code103(p); break;
  case 104: code104(p); break;
  case 105: code105(p); break;
  case 106: code106(p); break;
  case 107: code107(p); break;
  case 108: code108(p); break;
  case 109: code109(p); break;
  case 110: code110(p); break;
  case 111: code111(p); break;
  case 112: code112(p); break;
  case 113: code113(p); break;
  case 114: code114(p); break;
  case 115: code115(p); break;
  case 116: code116(p); break;
  case 117: code117(p); break;
  case 118: code118(p); break;
  case 119: code119(p); break;
  case 120: code120(p); break;
  case 121: code121(p); break;
  case 122: code122(p); break;
  case 123: code123(p); break;
  case 124: code124(p); break;
  case 125: code125(p); break;
  case 126: code126(p); break;
  case 127: code127(p); break;
  case 128: code128(p); break;
  case 129: code129(p); break;
  case 130: code130(p); break;
  case 131: code131(p); break;
  case 132: code132(p); break;
  case 133: code133(p); break;
  case 134: code134(p); break;
  case 135: code135(p); break;
  case 136: code136(p); break;
  case 137: code137(p); break;
  case 138: code138(p); break;
  case 139: code139(p); break;
  case 140: code140(p); break;
  case 141: code141(p); break;
  case 142: code142(p); break;
  case 143: code143(p); break;
  case 144: code144(p); break;
  case 145: code145(p); break;
  case 146: code146(p); break;
  case 147: code147(p); break;
  case 148: code148(p); break;
  case 149: code149(p); break;
  case 150: code150(p); break;
  case 151: code151(p); break;
  case 152: code152(p); break;
  case 153: code153(p); break;
  case 154: code154(p); break;
  case 155: code155(p); break;
  case 156: code156(p); break;
  case 157: code157(p); break;
  case 158: code158(p); break;
  case 159: code159(p); break;
  case 160: code160(p); break;
  case 161: code161(p); break;
  case 162: code162(p); break;
  case 163: code163(p); break;
  case 164: code164(p); break;
  case 165: code165(p); break;
  case 166: code166(p); break;
  case 167: code167(p); break;
  case 168: code168(p); break;
  case 169: code169(p); break;
  case 170: code170(p); break;
  case 171: code171(p); break;
  case 172: code172(p); break;
  case 173: code173(p); break;
  case 174: code174(p); break;
  case 175: code175(p); break;
  case 176: code176(p); break;
  case 177: code177(p); break;
  case 178: code178(p); break;
  case 179: code179(p); break;
  case 180: code180(p); break;
  case 181: code181(p); break;
  case 182: code182(p); break;
  case 183: code183(p); break;
  case 184: code184(p); break;
  case 185: code185(p); break;
  case 186: code186(p); break;
  case 187: code187(p); break;
  case 188: code188(p); break;
  case 189: code189(p); break;
  case 190: code190(p); break;
  case 191: code191(p); break;
  case 192: code192(p); break;
  case 193: code193(p); break;
  case 194: code194(p); break;
  case 195: code195(p); break;
  case 196: code196(p); break;
  case 197: code197(p); break;
  case 198: code198(p); break;
  case 199: code199(p); break;
  case 200: code200(p); break;
  case 201: code201(p); break;
  case 202: code202(p); break;
  case 203: code203(p); break;
  case 204: code204(p); break;
  case 205: code205(p); break;
  case 206: code206(p); break;
  case 207: code207(p); break;
  case 208: code208(p); break;
  case 209: code209(p); break;
  case 210: code210(p); break;
  case 211: code211(p); break;
  case 213: code213(p); break;
  case 214: code214(p); break;
  case 215: code215(p); break;
  case 216: code216(p); break;
  case 217: code217(p); break;
  case 218: code218(p); break;
  case 219: code219(p); break;
  case 220: code220(p); break;
  case 221: code221(p); break;
  case 222: code222(p); break;
  case 223: code223(p); break;
  case 224: code224(p); break;
  case 225: code225(p); break;
  case 226: code226(p); break;
  case 227: code227(p); break;
  case 228: code228(p); break;
  case 229: code229(p); break;
  case 230: code230(p); break;
  case 231: code231(p); break;
  case 232: code232(p); break;
  case 233: code233(p); break;
  case 234: code234(p); break;
  case 235: code235(p); break;
  case 236: code236(p); break;
  case 237: code237(p); break;
  case 238: code238(p); break;
  case 239: code239(p); break;
  case 240: code240(p); break;
  case 241: code241(p); break;
  case 242: code242(p); break;
  case 243: code243(p); break;
  case 244: code244(p); break;
  case 245: code245(p); break;
  case 246: code246(p); break;
  case 247: code247(p); break;
  case 248: code248(p); break;
  case 249: code249(p); break;
  case 250: code250(p); break;
  case 251: code251(p); break;
  case 252: code252(p); break;
  case 253: code253(p); break;
  case 254: code254(p); break;
  case 255: code255(p); break;
  case 256: code256(p); break;
  case 257: code257(p); break;
  case 258: code258(p); break;
  case 259: code259(p); break;
  case 260: code260(p); break;
  case 261: code261(p); break;
  case 262: code262(p); break;
  case 263: code263(p); break;
  case 264: code264(p); break;
  case 265: code265(p); break;
  case 266: code266(p); break;
  case 267: code267(p); break;
  case 268: code268(p); break;
  case 269: code269(p); break;
  default:
    throw new OptimizingCompilerException("BURS","rule without emit code:",BURS_Debug.string[ruleno]);
  }
}
}
