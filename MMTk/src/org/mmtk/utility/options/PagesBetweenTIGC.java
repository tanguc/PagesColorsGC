package org.mmtk.utility.options;

import org.vmutil.options.IntOption;

public class PagesBetweenTIGC extends IntOption {
  
  /**
   * Create the option.
   */
  public PagesBetweenTIGC() {
    super(Options.set, "Pages Between TIGC",
          "How many pages must a thread allocate/reserve before another thread-independent collection is triggered. Dividing this value by 256 converts pages into MB.",
          6400); //25MB
  }
}
