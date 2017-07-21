package org.jikesrvm.mm.mmtk;

import org.jikesrvm.classloader.Atom;
import org.jikesrvm.util.HashMapRVM;
import org.mmtk.utility.Log;
import org.mmtk.utility.options.Options;
import org.mmtk.plan.Plan;

public class AllocAdviceMap {
  private static HashMapRVM<String, Integer> adviceMap;
  
  public AllocAdviceMap() {
    adviceMap = new HashMapRVM<String, Integer>();
  }

  /**
   * Store alloc advice for a call site.
   *
   * @param klass the class of the allocating method
   * @param name the name of the allocating method
   * @param descriptor the descriptor of the allocating method
   * @param biOffset the index of the newXXX bytecode
   * @param advice the advice to store
   */
  public void putAdvice (Atom klass, 
                        Atom name, 
                        Atom descriptor,
                        int biOffset,
                        int advice) {
    String key = klass + ":" + name+descriptor+":"+biOffset;
    Integer oldAdvice = adviceMap.put(key, new Integer(advice));
    if (Options.verbose.getValue() >= 8 && oldAdvice != null) {
      Log.write("WARNING: Added ");
      if(oldAdvice.intValue() != advice) {
        Log.write("conflicting");
      } else {
        Log.write("duplicate");
      }
      Log.writeln(" advice.");
    }
  }

  /**
   * Store alloc advice for a call site.
   *
   * @param klass the class of the allocating method
   * @param name the name of the allocating method
   * @param descriptor the descriptor of the allocating method
   * @param biOffset the index of the newXXX bytecode
   * @param advice the advice to store
   */
  public void putAdvice (String klass, 
                         String name, 
                         String desc,
                         int biOffset,
                         int advice) {
    putAdvice(Atom.findOrCreateUnicodeAtom(klass),
              Atom.findOrCreateUnicodeAtom(name),
              Atom.findOrCreateUnicodeAtom(desc),
              biOffset,
              advice);
  }

  /**
   * Retrieve alloc advice for a call site.
   *
   * @param klass the class of the allocating method
   * @param name the name of the allocating method
   * @param descriptor the descriptor of the allocating method
   * @param biOffset the index of the newXXX bytecode
   * @return the advice for this site
   */
  public int getAdvice (Atom klass, 
                        Atom name, 
                        Atom descriptor,
                        int biOffset) {
    Integer advice;
    String key = klass + ":" + name+descriptor+":"+(biOffset-3);
    advice = adviceMap.get(key);
    if (advice == null) {
      return Plan.DEFAULT_ADVICE;
    } else {
      return advice.intValue();
    }
  }

  /**
   * Retrieve alloc advice for a call site.
   *
   * @param klass the class of the allocating method
   * @param name the name of the allocating method
   * @param descriptor the descriptor of the allocating method
   * @param biOffset the index of the newXXX bytecode
   * @return the advice for this site
   */
  public int getAdvice (byte[] klass, 
                        byte[] name, 
                        byte[] desc,
                        int biOffset) {
    return getAdvice(Atom.findOrCreateUtf8Atom(klass),
                     Atom.findOrCreateUtf8Atom(name),
                     Atom.findOrCreateUtf8Atom(desc),
                     biOffset);
  }
}
