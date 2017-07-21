public class dc2006CB extends dacapo.Callback{

  public void start(String benchmark) {
    org.mmtk.plan.Plan.startTiming();
    System.err.println("Benchmark ON");
    System.err.flush();
    org.mmtk.plan.Plan.harnessWarmupFinished = true;
  };

  public void complete(String benchmark, boolean valid) {
    //Complete last GC with all live objects to be reported
    System.err.println("Benchmark OFF");
    org.mmtk.plan.Plan.endTiming();
    System.err.flush();
    super.complete(benchmark, valid);
    System.err.println("MeasurementRun: " + (valid ? "PASSED " : "FAILED ") + benchmark + "2006");
    System.err.flush();
  };
}

