import java.lang.*;
import java.security.*;

public class specHarness {

  private static class ExitTrappedException extends SecurityException { }

    private static void forbidSystemExitCall() {
      final SecurityManager securityManager = new SecurityManager() {
        public void checkPermission( Permission permission ) {
          System.out.println(permission);
          if(permission != null && (permission.getName().equals("exitVM") || permission.getName().equals("exitVM.0"))) {
            throw new ExitTrappedException() ;
          }
        }
      };
      System.setSecurityManager( securityManager ) ;
    }

    private static void enableSystemExitCall() {
      System.setSecurityManager( null ) ;
    }

public static void main(String[] args) {

  forbidSystemExitCall();

  // Start
  //org.mmtk.plan.Plan.startTiming();
  System.err.println("Benchmark ON");
  System.err.flush();
  //org.mmtk.plan.Plan.harnessWarmupFinished = true;
  
  // Run
  try {
    spec.jbb.JBBmain.main(new String[]{"-propfile", "SPECjbb.props"});
  }
  catch( ExitTrappedException e ) {
    
  }
  finally {
    enableSystemExitCall() ;
  }
  
  // Complete
  //Complete last GC with all live objects to be reported
  System.err.println("Benchmark OFF");
  //org.mmtk.plan.Plan.endTiming();
  System.err.flush();
  System.err.println("MeasurementRun: PASSED pseudojbb");
  System.err.flush();
}

}