package org.mmtk.utility.options;

import org.vmutil.options.IntOption;

public class GlobaliseLevel extends IntOption {
  
  /**
   * Create the option.
   */
  public GlobaliseLevel() {
    super(Options.set, "Globalise Level",
          "When an object is globalised, how many generations of children should be globalised. 0 means only the relevant object is globalised. 1 means children are also globalised. 2 means the grandchildren are globalised.",
          0);
  }
}
