package org.mmtk.utility.options;

import org.vmutil.options.StringOption;

public class AllocAdviceFile extends StringOption {
  /**
   * Create the option.
   */
  public AllocAdviceFile() {
    super(Options.set, "Alloc Advice File",
          "File to read runtime allocation advice from",
          null);
  }

  /**
   * Ensure the port is valid.
   */
  protected void validate() {
    //failIf(this.value != null, "No allocation advice specified");
  }
}
