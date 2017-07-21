import org.dacapo.harness.Callback;
import org.dacapo.harness.CommandLineArgs;

public class dc2009CB extends Callback {

  public dc2009CB(CommandLineArgs args) {
      super(args);
  }

  public void start(String benchmark) {
    org.mmtk.plan.Plan.startTiming();
    System.err.println("Benchmark ON");
    System.err.flush();
    org.mmtk.plan.Plan.harnessWarmupFinished = true;
  };
  
  public void start(String benchmark, boolean valid) {
    start(benchmark);
  }
  
  public void complete(String benchmark, boolean valid) {
    System.err.println("Benchmark OFF");
    org.mmtk.plan.Plan.endTiming();
    System.err.flush();
    super.complete(benchmark, valid);
    System.err.println("MeasurementRun: " + (valid ? "PASSED " : "FAILED ") + benchmark + "2009");
    System.err.flush();
  };
}

