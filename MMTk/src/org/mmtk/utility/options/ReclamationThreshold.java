package org.mmtk.utility.options;

import org.vmutil.options.IntOption;

public class ReclamationThreshold extends IntOption {
  /**
   * Create the option.
   */
  public ReclamationThreshold() {
    super(Options.set, "Reclamation Threshold",
          "If a TIGC does not free at least this threshold of objects (in KB), do not perform any TIGCs until a full heap GC has occured",
          50);
  }
}
