package org.jikesrvm.mm.mmtk;

import org.mmtk.utility.Log;
import org.mmtk.utility.options.Options;
import org.mmtk.vm.VM;
import org.vmmagic.pragma.Interruptible;
import org.vmmagic.pragma.LogicallyUninterruptible;
import org.vmmagic.pragma.Uninterruptible;
import org.jikesrvm.classloader.Atom;
import org.jikesrvm.classloader.RVMClass;
import org.jikesrvm.classloader.TypeReference;
import org.jikesrvm.mm.mmtk.AllocAdviceMap;

import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.lang.Character;

@Uninterruptible
public class AllocationSite {
  private static AllocAdviceMap adviceMap;

  /* Static initialiser responsible for loading the advice */
  static {
    adviceMap = new AllocAdviceMap();
  }

  /**
   * Read allocation advice from a file
   *
   * @param file the path to the allocation file
   */
  @LogicallyUninterruptible
  public static void readAdviceFile(String file) {
    //File adviceFile = new File(file);
    FileReader in;
    StreamTokenizer st;
    String klass;
    String method;
    String descriptor;
    int biOffset;
    int advice;

    //if (adviceFile == null) return;
    try {
      in = new FileReader(file); //adviceFile);
      st = new StreamTokenizer(new BufferedReader(in));

      // Setup the tokenizer
      st.eolIsSignificant(false);
      st.parseNumbers();
      st.lowerCaseMode(false);
      // Accept any character. Comments are preceeded by '#'
      // fields are seperated by spaces.
      st.wordChars(Character.MIN_VALUE,Character.MAX_VALUE);
      st.commentChar('#');
      st.whitespaceChars(' ',' ');
      st.whitespaceChars(':',':');
      st.whitespaceChars('\n','\n');
      st.ordinaryChar('(');
      
      int line = 0;

      while (st.nextToken() != StreamTokenizer.TT_EOF) {
        line++;
        /* Word, the class */
        if(st.ttype != StreamTokenizer.TT_WORD) {
          VM.assertions.fail("Error reading advice file. Expected class name.");
        }
        klass = st.sval;

        /* Word, the method (not including type signature) */
        st.nextToken();
        if(st.ttype != StreamTokenizer.TT_WORD) {
          VM.assertions.fail("Error reading advice file. Expected method name.");
        }
        method = st.sval;

        /* Look for the '(', which is the start of the type signature */
        st.nextToken();
        if(st.ttype != '(') {
          VM.assertions.fail("Error reading advice file. Expected method signature.");
        }

        /* Read the rest of the type signature */
        st.nextToken();
        if(st.ttype != StreamTokenizer.TT_WORD) {
          VM.assertions.fail("Error reading advice file. Expected method signature.");
        }
        /* Add the '(' bracket that was gobbled as the pervious token */
        descriptor = "(" + st.sval;

        /* Get the byte code offset */
        st.nextToken();
        if(st.ttype != StreamTokenizer.TT_NUMBER) {
          VM.assertions.fail("Error reading advice file. Expected bytecode offset.");
        }
        biOffset = (int)st.nval;

        /* Get the allocation advice */
        st.nextToken();
        if(st.ttype != StreamTokenizer.TT_NUMBER) {
          VM.assertions.fail("Error reading advice file. Expected advice number.");
        }
        advice = (int)st.nval;
        
        if (Options.verbose.getValue() >= 8) {
          Log.write("Allocating ");
          Log.write(line);
          Log.write(": ");
          Log.write(klass);
          Log.write(".");
          Log.write(method);
          Log.write(descriptor);
          Log.write(":");
          Log.write(biOffset);
          Log.write(" as ");
          Log.writeln(advice);
        }
        
        adviceMap.putAdvice(klass, method, descriptor, biOffset, advice);
      }

      in.close();
    } catch (FileNotFoundException e) {
      VM.assertions.fail("Could not open advice file");
    } catch (IOException e) {
      VM.assertions.fail("Error reading or closing advice file");
    }
  }

  /**
   * @param klass the class of the allocating method
   * @param name the name of the allocating method
   * @param descriptor the descriptor of the allocating method
   * @param offset the index of the newXXX bytecode
   * @return a unique id representing this allocation site
   */
  @Interruptible
  public static int getAdvice (RVMClass klass, 
                               Atom name, 
                               Atom descriptor,
                               int offset) {
    if (VM.VERIFY_ASSERTIONS) { VM.assertions._assert(adviceMap != null); }
    return adviceMap.getAdvice(klass.getTypeRef().getName(), name, descriptor, offset);
  }
  
  @Interruptible
  public static int getAdvice (TypeReference typeRef, 
                               Atom name, 
                               Atom descriptor,
                               int offset) {
    if (VM.VERIFY_ASSERTIONS) { VM.assertions._assert(adviceMap != null); }
    return adviceMap.getAdvice(typeRef.getName(), name, descriptor, offset);
  }
}
