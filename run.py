#!/usr/bin/env python

import sys
import time
import os
import subprocess


fl = open("run.log", "w")
#OPTIONS:

#AUTO GENERATED
arch = "ia32"
host = ""
if os.uname()[0] == "Darwin":
  host = "osx"
elif os.uname()[0] == "Linux":
  host = "linux"
  if os.uname()[4] == "x86_64":
    arch = "x86_64"

#MANUAL
iterations = "1"
plan = "MarkSweep"
verbose = "2"
verboseBoot = "0"
noFinalisation = "true"
noReferenceTypes = "true"
sanityCheck = None
printmc = None
heap = "500M"
stressFactor = None
opt = True
fast = True
compileOnly = False
noCompile = False
advice = True
forceOneCPU = None
haltOnFailure = False
startInv = 1
recompileAfterRetries = 1000
#Following two options are only used for custom benchmark
bthreads = "1"
brounds = "400"

pagesBetweenTIGC = 3 * 256 #Each page is 4KB. Divide number by 256 (1024*1024/4096) to get MB value.
reclamationThreshold = 0 #in KB
# How many extra levels deep to globalise.
globaliseLevel = 0
dacapoThreadLimit = " -t 48 "
jgfThreads = "24"

fhgcCap = "16"

cpuBindings = ["balancedBinding", "allOnOneNode", "allOnOneSocket", "oneProcPerNode", "allOneHop", "oppositeConsecutive"]

#Abort timeout
timeout = "200" 
#Number of execution retries until we give up
retries = 10

#wba, rba, glo, tgc, per, bnd

#Stats mode:
#invocations = "1"
#gcthreads = "1"
#numberCPUs = [None]
#modes = [("-stats", [True, True, True, True, False, False, "balancedBinding"])]

#Performance Test:
#invocations = "1"
#gcthreads = None
#numberCPUs = [None]
#modes = [("-perftest", [True, True, True, True, True, False, "balancedBinding"])]

#Performance mode:
#invocations = "10"
#gcthreads = "1"
#numberCPUs = [None]
#modes = [("-allOff", [False, False, False, False, True, False, "balancedBinding"]), ("-barriers", [True, True, False, False, True, False, "balancedBinding"]), ("-globaliser", [True, True, True, False, True, False, "balancedBinding"]), ("-TIGC", [True, True, True, True, True, False, "balancedBinding"])]

#Scaling mode:
invocations = "1"
gcthreads=None
numberCPUs = ["1", "2", "4", "8", "12", "16", "20", "24"]#, "28", "32", "36", "40", "44", "48"]
modes = [
    ("-scaleBalancedSTW", [False, False, False, False, True, True, "balancedBinding"]), ("-scaleBalancedTIGC", [True, True, True, True, True, True, "balancedBinding"]),
    ("-scaleOneNodeSTW", [False, False, False, False, True, True, "allOnOneNode"]), ("-scaleOneNodeTIGC", [True, True, True, True, True, True, "allOnOneNode"]),
    ("-scaleOneSocketSTW", [False, False, False, False, True, True, "allOnOneSocket"]), ("-scaleOneSocketTIGC", [True, True, True, True, True, True, "allOnOneSocket"]),
    ("-scaleOneProcessorPerNodeSTW", [False, False, False, False, True, True, "oneProcPerNode"]), ("-scaleOneProcessorPerNodeTIGC", [True, True, True, True, True, True, "oneProcPerNode"]),
    ("-scaleOneHopSTW", [False, False, False, False, True, True, "allOneHop"]), ("-scaleOneHopTIGC", [True, True, True, True, True, True, "allOneHop"]),
    ("-scaleOppositeConsecutiveSTW", [False, False, False, False, True, True, "oppositeConsecutive"]), ("-scaleOppositeConsecutiveTIGC", [True, True, True, True, True, True, "oppositeConsecutive"])
]

benchmarks = [
#  ("antlr", "2006"),
#  ("avrora", "2009"),
#  ("bloat", "2006"),
#  ("fop", "2006"),
#  ("hsqldb", "2006"),
#  ("jython", "2009"),
#  ("luindex", "2009"),
#  ("lusearch", "2009"),
#  ("pmd", "2006"),
  ("sunflow", "2009")
#  ("xalan", "2009"),
#  ("pseudojbb", "pseudojbb")
#  ("HarnessJGFAllSizeA", "jgf3")
]
#END

if len(sys.argv) > 2:
  benchmarks = [(sys.argv[2], sys.argv[3])]
  startInv = int(sys.argv[1])

# Advice File Generation
def generatePreSharing(mode, arch, host, plan, opt, fast):
    print "Executing cd dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_"+arch+"-"+host+mode+"/ && ln -s ../../AdviceFileGeneration/jarExtractor.py jarExtractor.py && ./jarExtractor.py && find . -name \"*.class\" > classList.jikes && java -classpath ../../AdviceFileGeneration/.:../../AdviceFileGeneration/bcel.jar Main ../../AdviceFileGeneration/sharedTypes.dat classList.jikes > classAdvice.dat && cat ../../AdviceFileGeneration/dacapoAdvice.dat classAdvice.dat > totalAdvice.dat"
    os.system("cd dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_"+arch+"-"+host+mode+"/ && ln -s ../../AdviceFileGeneration/jarExtractor.py jarExtractor.py && ./jarExtractor.py && find . -name \"*.class\" > classList.jikes && java -classpath ../../AdviceFileGeneration/.:../../AdviceFileGeneration/bcel.jar Main ../../AdviceFileGeneration/sharedTypes.dat classList.jikes > classAdvice.dat && cat ../../AdviceFileGeneration/dacapoAdvice.dat classAdvice.dat > totalAdvice.dat")

# Set flags in Plan.java that enable/disable parts of the thread-independent garbage collector.
def transform(wba, rba, glo, tgc, per, bnd, cpuBinding="balancedBinding"):
   e = ""
   if host == "osx":
      e = " ''"
   if (wba):
      os.system('sed -i'+e+' "s/WRITE_BARRIERS_ON = false/WRITE_BARRIERS_ON = true/g" MMTk/src/org/mmtk/plan/Plan.java')
   else:
      os.system('sed -i'+e+' "s/WRITE_BARRIERS_ON = true/WRITE_BARRIERS_ON = false/g" MMTk/src/org/mmtk/plan/Plan.java')

   if (rba):
      os.system('sed -i'+e+' "s/READ_BARRIERS_ON = false/READ_BARRIERS_ON = true/g" MMTk/src/org/mmtk/plan/Plan.java')
   else:
      os.system('sed -i'+e+' "s/READ_BARRIERS_ON = true/READ_BARRIERS_ON = false/g" MMTk/src/org/mmtk/plan/Plan.java')

   if (glo):
      os.system('sed -i'+e+' "s/GLOBALISE_ON = false/GLOBALISE_ON = true/g" MMTk/src/org/mmtk/plan/Plan.java')
      os.system('sed -i'+e+' "s/REMSET_ON = false/REMSET_ON = true/g" MMTk/src/org/mmtk/plan/Plan.java')
   else:
      os.system('sed -i'+e+' "s/GLOBALISE_ON = true/GLOBALISE_ON = false/g" MMTk/src/org/mmtk/plan/Plan.java')
      os.system('sed -i'+e+' "s/REMSET_ON = true/REMSET_ON = false/g" MMTk/src/org/mmtk/plan/Plan.java')

   if (tgc):
      os.system('sed -i'+e+' "s/TIGC_ON = false/TIGC_ON = true/g" MMTk/src/org/mmtk/plan/Plan.java')
   else:
      os.system('sed -i'+e+' "s/TIGC_ON = true/TIGC_ON = false/g" MMTk/src/org/mmtk/plan/Plan.java')

   if per:
      os.system('sed -i'+e+' "s/PERFORMANCE_RUN = false/PERFORMANCE_RUN = true/g" MMTk/src/org/mmtk/plan/Plan.java')
   else:
      os.system('sed -i'+e+' "s/PERFORMANCE_RUN = true/PERFORMANCE_RUN = false/g" MMTk/src/org/mmtk/plan/Plan.java')
      
   if bnd:
      os.system('sed -i'+e+' "s/SETBIND = false/SETBIND = true/g" MMTk/src/org/mmtk/plan/Plan.java')
   else:
      os.system('sed -i'+e+' "s/SETBIND = true/SETBIND = false/g" MMTk/src/org/mmtk/plan/Plan.java')

   os.system("head -n 69 MMTk/src/org/mmtk/plan/Plan.java > ~/.tmp; tail -n 8 ~/.tmp;")
   
   for binding in cpuBindings:
       os.system('sed -i'+e+' "s/private static final int\[\] cpuBinding = '+binding+';/private static final int\[\] cpuBinding = null;/g" rvm/src/org/jikesrvm/scheduler/RVMThread.java')
   
   os.system('sed -i'+e+' "s/private static final int\[\] cpuBinding = null;/private static final int\[\] cpuBinding = '+cpuBinding+';/g" rvm/src/org/jikesrvm/scheduler/RVMThread.java')
   
   os.system("head -n 179 rvm/src/org/jikesrvm/scheduler/RVMThread.java > ~/.tmp; tail -n 1 ~/.tmp;")

# For every mode, transform GC flags in Plan and compile
def compileModes():
    for (name, params) in modes:
        transform(params[0], params[1], params[2], params[3], params[4], params[5], params[6])
        compileMode(name, params)

# Perform compilation for a specific mode
def compileMode(mode, params):
    execution = "until ant -Dcomponents.cache.dir=../../cache -Dconfig.name="+("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) +plan+" -Dhost.name="+arch+"-"+host+" -Dcp.enable.gtk-peer=\"--disable-gtk-peer\" > compile.log 2>&1; do echo Retrying...; sleep 3; done; javac -source 6 -target 6 -classpath target/mmtk/mmtk.jar:../dacapo-2006-10-MR2.jar dc2006CB.java; javac -source 6 -target 6 -classpath target/mmtk/mmtk.jar:../dacapo-9.12-bach.jar dc2009CB.java; javac -source 6 -target 6 -classpath ./pseudojbb/jbb.jar:./pseudojbb/jbb_no_precompile.jar:./pseudojbb/check.jar:./pseudojbb/reporter.jar:./target/mmtk/mmtk.jar specHarness.java; javac -source 6 -target 6 -classpath ./target/mmtk/mmtk.jar:./jgf/threadv1.0/:./jgf/threadv1.0/section3 HarnessJGFAllSizeA.java; ./makeBenchmark.sh; mv ./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host + " ./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host + mode + "; mv dc2006CB.class " + "./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host+mode+"/dc2006CB.class; mv dc2009CB.class " + "./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host+mode+"/dc2009CB.class; mv Benchmark/Benchmark.jar ./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host+mode+"/Benchmark.jar; mv specHarness.class " + "./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host+mode+"/specHarness.class; mv HarnessJGFAllSizeA.class " + "./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host+mode+"/HarnessJGFAllSizeA.class;"
    print "Executing " + execution
    sys.stdout.flush()
    
    os.system(execution);
    generatePreSharing(mode, arch, host, plan, opt, fast)
    if opt:
        preCompile(mode, params)

def run(string):
  process = subprocess.Popen(string, shell=True, stdout=subprocess.PIPE)
  return process.communicate()
  
# Perform optimising compiler precompilation
def preCompile(mode, params):
    for i in range(0, len(benchmarks)):
       for k in range(0, retries):
          if ((k % recompileAfterRetries) == (recompileAfterRetries - 1)):
             transform(params[0], params[1], params[2], params[3], params[4], params[5])
             print "Recompiling "
             sys.stdout.flush()
    
             execution = "until ant -Dconfig.name="+("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) +plan+" -Dhost.name="+arch+"-"+host+" -Dcp.enable.gtk-peer=\"--disable-gtk-peer\" > compile.log 2>&1; do echo Retrying...; sleep 3; done; javac -source 6 -target 6 -classpath target/mmtk/mmtk.jar:../dacapo-2006-10-MR2.jar dc2006CB.java; javac -source 6 -target 6 -classpath target/mmtk/mmtk.jar:../dacapo-9.12-bach.jar dc2009CB.java; javac -source 6 -target 6 -classpath ./pseudojbb/jbb.jar:./pseudojbb/jbb_no_precompile.jar:./pseudojbb/check.jar:./pseudojbb/reporter.jar:./target/mmtk/mmtk.jar specHarness.java; javac -source 6 -target 6 -classpath ./target/mmtk/mmtk.jar:./jgf/threadv1.0/:./jgf/threadv1.0/section3 HarnessJGFAllSizeA.java; ./makeBenchmark.sh; mv ./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host + " ./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host + mode + "; mv dc2006CB.class " + "./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host+mode+"/dc2006CB.class; mv dc2009CB.class " + "./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host+mode+"/dc2009CB.class; mv Benchmark/Benchmark.jar ./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host+mode+"/Benchmark.jar; mv specHarness.class " + "./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host+mode+"/specHarness.class; mv HarnessJGFAllSizeA.class " + "./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host+mode+"/HarnessJGFAllSizeA.class;"
             os.system(execution);
             generatePreSharing(mode, arch, host, plan, opt, fast)
               
          (benchmark,suite) = benchmarks[i]
          os.system("mkdir -p results/"+mode[1:])
          outputFilename = "results/"+mode[1:]+ "/" + benchmark+suite+mode
          os.system("cp ./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host+mode+"/dc2006CB.class .")
          os.system("cp ./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host+mode+"/dc2009CB.class .")
          prefix = ("timeout " + timeout + " " if timeout != "0" else "") + "./dist/" + ("FastAdaptive" if fast else "FullAdaptive") + plan + "_"+arch+"-"+host+mode+"/rvm"
          precompileDir = "./dist/" + ("FastAdaptive" if fast else "FullAdaptive") + plan + "_"+arch+"-"+host+mode+"/"
          prepStringInfix = " -X:aos:enable_advice_generation=true -X:aos:cafo=" + precompileDir+benchmark+suite+mode + ".ca -X:base:profile_edge_counters=true -X:base:profile_edge_counter_file=" + precompileDir+benchmark+suite+mode + ".ec -X:aos:dcfo=" + precompileDir+benchmark+suite+mode + ".dc -X:aos:final_report_level=2"
          gcthreadsDash = gcthreads
          if (gcthreadsDash == None):
              gcthreadsDash = "1"
          options = (" -X:gc:stressFactor="+stressFactor if (stressFactor!=None) else "") \
                 + " -X:gc:verbose="+verbose + " -X:verboseBoot="+verboseBoot \
                 + (" -Xms"+heap + " -Xmx"+heap if (heap!=None) else "") \
                 + " -showfullversion" \
                 + (" -X:gc:threads="+gcthreadsDash if (gcthreadsDash!=None) else "") \
                 + (" -X:gc:sanityCheck="+sanityCheck if (sanityCheck!=None) else "") \
                 + (" -X:gc:noReferenceTypes="+noReferenceTypes if (noReferenceTypes!=None) else "") \
                 + (" -X:opt:mc="+printmc if (printmc!=None) else "") \
                 + (" -X:gc:noFinalizer="+noFinalisation if (noFinalisation!=None) else "") \
                 + (" -X:vm:forceOneCPU="+forceOneCPU if (forceOneCPU!=None) else "") #\
                 #+ (" -X:vm:numberCPUs="+numberCPUs if numberCPUs else "")
          prepAppString = ""
          if (suite == "Benchmark"):
              prepAppString = " -classpath .:./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host+mode+"/Benchmark.jar Main "+bthreads+" "+brounds
          elif (suite == "pseudojbb"):
              prepAppString = " -classpath ./pseudojbb/jbb.jar:./pseudojbb/jbb_no_precompile.jar:./pseudojbb/check.jar:./pseudojbb/reporter.jar:.:./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host+mode+" specHarness"
          elif (suite == "jgf1"):
              prepAppString = " -classpath .:./jgf/threadv1.0:./jgf/threadv1.0/section1:./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host+mode+" "+benchmark+" "+jgfThreads
          elif (suite == "jgf2"):
              prepAppString = " -classpath .:./jgf/threadv1.0:./jgf/threadv1.0/section2:./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host+mode+" "+benchmark+" "+jgfThreads
          elif (suite == "jgf3"):
              prepAppString = " -classpath .:./jgf/threadv1.0:./jgf/threadv1.0/section3:./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host+mode+" "+benchmark+" "+jgfThreads
          else:
              prepAppString = " -classpath .:../" + ("dacapo-2006-10-MR2.jar Harness -c dc2006CB" if suite=="2006" else "dacapo-9.12-bach.jar Harness -c dc2009CB") + " -n " + iterations + " " + benchmark + ((" " +dacapoThreadLimit) if suite=="2009" else "")
          prepSuffix = " > " + outputFilename + ".prep 2>&1"
          print "Executing " + prefix + prepStringInfix + options + prepAppString + prepSuffix
          sys.stdout.flush()
          ts = time.time()
          run(prefix + prepStringInfix + options + prepAppString + prepSuffix)
          te = time.time()
          print ">>>>>> Time taken: "+str(te-ts)+"s"
          os.system("cp " + outputFilename + ".prep " + outputFilename + ".prep" + str(k))
          o = run("grep PASSED " + outputFilename + ".prep")[0]
          print o.strip(), o.strip() != ""
          sys.stdout.flush()
          for z in range(0, 1024):
            run('rm statistics.' + str(z))
          if (o.strip() != ""):
             break

def runExperiment(mode="", suite="2009", benchmark="xalan", invocation="1", restrictCPUs=None, params=[]):
  outputFilename = "results/" + mode[1:]+"/"+str(invocation)+ "/"+str(restrictCPUs) + "cpus/" + benchmark+suite+mode+"-"+invocation+".log"
  precompileDir = "./dist/" + ("FastAdaptive" if fast else "FullAdaptive") + plan + "_"+arch+"-"+host+mode+"/"
  os.system("cp ./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host+mode+"/dc2006CB.class .")
  os.system("cp ./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host+mode+"/dc2009CB.class .")
  os.system("cp ./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host+mode+"/specHarness.class .")
  os.system("cp ./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host+mode+"/HarnessJGFAllSizeA.class .")
  
  if opt:
    execStringInfix = " -X:aos:initial_compiler=base -X:aos:enable_precompile=true -X:aos:enable_recompilation=false -X:aos:cafi=" + precompileDir+benchmark+suite+mode + ".ca -X:vm:edgeCounterFile=" + precompileDir+benchmark+suite+mode + ".ec -X:aos:dcfi=" + precompileDir+benchmark+suite+mode+ ".dc"
    prefix = ("timeout " + timeout + " " if timeout != "0" else "") + "./dist/" + ("FastAdaptive" if fast else "FullAdaptive") + plan + "_"+arch+"-"+host+mode+"/rvm"
    gcthreadsDash = gcthreads
    if (gcthreadsDash == None):
        gcthreadsDash = restrictCPUs
    if (params[3] and (restrictCPUs == None or int(gcthreadsDash) > int(fhgcCap))):
        gcthreadsDash = fhgcCap
    options = (" -X:gc:stressFactor="+stressFactor if (stressFactor!=None) else "") \
              + " -X:gc:verbose="+verbose + " -X:verboseBoot="+verboseBoot \
              + (" -Xms"+heap + " -Xmx"+heap if (heap!=None) else "") \
              + " -showfullversion" \
              + (" -X:gc:threads="+gcthreadsDash if (gcthreadsDash!=None) else "") \
              + (" -X:gc:sanityCheck="+sanityCheck if (sanityCheck!=None) else "") \
              + (" -X:gc:noReferenceTypes="+noReferenceTypes if (noReferenceTypes!=None) else "") \
              + (" -X:opt:mc="+printmc if (printmc!=None) else "") \
              + (" -X:gc:noFinalizer="+noFinalisation if (noFinalisation!=None) else "") \
              + (" -X:vm:forceOneCPU="+forceOneCPU if (forceOneCPU!=None) else "") \
              + (" -X:gc:pagesBetweenTIGC="+str(pagesBetweenTIGC) if (pagesBetweenTIGC!=None) else "") \
              + (" -X:gc:reclamationThreshold="+str(reclamationThreshold) if (reclamationThreshold!=None) else "") \
              + (" -X:gc:globaliseLevel="+str(globaliseLevel) if (globaliseLevel!=None) else "") \
              + (" -X:gc:allocAdviceFile="+precompileDir+"totalAdvice.dat" if (advice!=None) else "") \
              + (" -X:vm:numberCPUs="+restrictCPUs if (restrictCPUs!=None) else "")
    appString = ""
    if (suite == "Benchmark"):
        appString = " -classpath .:./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host+mode+"/Benchmark.jar Main "+bthreads+" "+brounds
    elif (suite == "pseudojbb"):
        appString = " -classpath ./pseudojbb/jbb.jar:./pseudojbb/jbb_no_precompile.jar:./pseudojbb/check.jar:./pseudojbb/reporter.jar:.:./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host+mode+" specHarness"
    elif (suite == "jgf1"):
        appString = " -classpath .:./jgf/threadv1.0:./jgf/threadv1.0/section1:./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host+mode+" "+benchmark+" "+jgfThreads
    elif (suite == "jgf2"):
        appString = " -classpath .:./jgf/threadv1.0:./jgf/threadv1.0/section2:./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host+mode+" "+benchmark+" "+jgfThreads
    elif (suite == "jgf3"):
        appString = " -classpath .:./jgf/threadv1.0:./jgf/threadv1.0/section3:./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host+mode+" "+benchmark+" "+jgfThreads
    else:
        appString = " -classpath .:../" + ("dacapo-2006-10-MR2.jar Harness -c dc2006CB" if suite=="2006" else "dacapo-9.12-bach.jar Harness -c dc2009CB") + " -n " + iterations + " " + benchmark + ((" "+dacapoThreadLimit) if suite=="2009" else "")
    execSuffix = " > " + outputFilename + " 2>&1"
  
    print "Executing " + prefix + execStringInfix + options + appString + execSuffix
    sys.stdout.flush()
    run(prefix + execStringInfix + options + appString + execSuffix)
  else:
    prefix = ("timeout " + timeout + " " if timeout != "0" else "") + "./dist/BaseBase" + plan + "_"+arch+"-"+host+mode+"/rvm"
    gcthreadsDash = gcthreads
    if (gcthreadsDash == None):
        gcthreadsDash = restrictCPUs    
    if (params[3] and (restrictCPUs == None or int(gcthreadsDash) > int(fhgcCap))):
        gcthreadsDash = fhgcCap
    options = (" -X:gc:stressFactor="+stressFactor if (stressFactor!=None) else "") \
              + " -X:gc:verbose="+verbose + " -X:verboseBoot="+verboseBoot \
              + (" -Xms"+heap + " -Xmx"+heap if (heap!=None) else "") \
              + " -showfullversion" \
              + (" -X:gc:threads="+gcthreadsDash if (gcthreadsDash!=None) else "") \
              + (" -X:gc:sanityCheck="+sanityCheck if (sanityCheck!=None) else "") \
              + (" -X:gc:noReferenceTypes="+noReferenceTypes if (noReferenceTypes!=None) else "") \
              + (" -X:opt:mc="+printmc if (printmc!=None) else "") \
              + (" -X:gc:noFinalizer="+noFinalisation if (noFinalisation!=None) else "") \
              + (" -X:vm:forceOneCPU="+forceOneCPU if (forceOneCPU!=None) else "") \
              + (" -X:gc:pagesBetweenTIGC="+str(pagesBetweenTIGC) if (pagesBetweenTIGC!=None) else "") \
              + (" -X:gc:reclamationThreshold="+str(reclamationThreshold) if (reclamationThreshold!=None) else "") \
              + (" -X:gc:globaliseLevel="+str(globaliseLevel) if (globaliseLevel!=None) else "") \
              + (" -X:gc:allocAdviceFile=./dist/BaseBase"+ plan + "_"+arch+"-"+host+mode+"/totalAdvice.dat" if (advice!=None) else "") \
              + (" -X:vm:numberCPUs="+restrictCPUs if (restrictCPUs!=None) else "")
    appString = ""
    if (suite == "Benchmark"):
        appString = " -classpath .:./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host+mode+"/Benchmark.jar Main "+bthreads+" "+brounds
    elif (suite == "pseudojbb"):
        prepAppString = " -classpath ./pseudojbb/jbb.jar:./pseudojbb/jbb_no_precompile.jar:./pseudojbb/check.jar:./pseudojbb/reporter.jar:.:./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host+mode+" specHarness"
    elif (suite == "jgf1"):
        appString = " -classpath .:./jgf/threadv1.0:./jgf/threadv1.0/section1:./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host+mode+" "+benchmark+" "+jgfThreads
    elif (suite == "jgf2"):
        appString = " -classpath .:./jgf/threadv1.0:./jgf/threadv1.0/section2:./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host+mode+" "+benchmark+" "+jgfThreads
    elif (suite == "jgf3"):
        appString = " -classpath .:./jgf/threadv1.0:./jgf/threadv1.0/section3:./dist/" + ("BaseBase" if not opt else ("FastAdaptive" if fast else "FullAdaptive")) + plan + "_" + arch+"-"+host+mode+" "+benchmark+" "+jgfThreads    
    else:
        appString = " -classpath .:../" + ("dacapo-2006-10-MR2.jar Harness -c dc2006CB" if suite=="2006" else "dacapo-9.12-bach.jar Harness -c dc2009CB") + " -n " + iterations + " " + benchmark + ((" "+dacapoThreadLimit) if suite=="2009" else "")
    suffix = " > " + outputFilename + " 2>&1"
  
    print "Executing " + prefix + options + appString + suffix
    sys.stdout.flush()
    run(prefix + options + appString + suffix)

  o = run("grep PASSED " + outputFilename)[0]
  print o
  sys.stdout.flush()
  d = run('grep "Lorg/jikesrvm/VM; sysFail(Ljava/lang/String;)V at line" ' + outputFilename)[0]
  run("touch " + outputFilename + ".stats")
  for z in range(0, 1024):
    run("cat " + outputFilename + ".stats " + "statistics." + str(z) + " > " + outputFilename + ".stats.tmp && mv " + outputFilename + ".stats.tmp " + outputFilename + ".stats && rm statistics." + str(z))
  return (o.strip() != "", d.strip() != "")

# -1: Benchmark Failed. Will not retry
# -2: Benchmark Success
# -3: Timed out and found SysFail. Will not retry
# retries: Hit the limit of retries
# 0 - (retries - 1): Timed out but did not SysFail. Perhaps got stuck. Will try.
def allBenchmarksDidNotTimeout(trylist):
  for item in trylist:
    if (item == -3 or item == -1 or item == -2 or item > retries):
      continue
    else:
      return False
  return True

os.system("mv results resultsbak"+time.strftime("%b%d%H%M").lower())
os.system("mkdir results")

if not noCompile:
   os.system("ant real-clean; rm -rf scratch")
   os.system("cd AdviceFileGeneration/ && javac -source 6 -target 6 -classpath bcel.jar *.java")
   compileModes()
              
if compileOnly:
    sys.exit(0)
              
for mode in modes:
   os.system("mkdir -p results/"+mode[0][1:])
   for invocation in range(startInv, int(invocations)+1):
      for nC in numberCPUs:
          os.system("mkdir -p results/"+mode[0][1:]+"/"+str(invocation)+"/"+str(nC)+"cpus")
          print (mode[0], invocation, nC)
          sys.stdout.flush()
          tries = [0] * len(benchmarks)
          while (not allBenchmarksDidNotTimeout(tries)):
            for i in range(0, len(benchmarks)):
              if (tries[i] == -2 or tries[i] == -1 or tries[i] == -3):
                continue
              tries[i] += 1
              if (tries[i] > retries):
                continue
              (benchmark,suite) = benchmarks[i]
              print ">>>>>>>>>> RUN (" + str(i+1) + " of " + str(len(benchmarks)) + "): " + str((benchmark,suite)) + " Try " + str(tries[i])
              sys.stdout.flush()
              ts = time.time()
              passed, sysFailed = runExperiment(mode=mode[0],
                      suite=suite, 
                      benchmark=benchmark,
                      invocation=str(invocation),
                      restrictCPUs=nC,
                      params=mode[1]
                      )
              te = time.time()
              print ">>>>>> Time taken: "+str(te-ts)+"s"
              sys.stdout.flush()
              if (timeout != "0") and (te-ts) > (int(timeout)):
                if haltOnFailure and sysFailed:
                  print "sysFailed with timeout. Will not retry"
                  sys.stdout.flush()
                  tries[i] = -3
                  fl.write(str(mode) + " " + str(invocation) + " " + str(nC) + " " + benchmark + " " + suite + " = Failed & Halted (timeouts)\n")
                else:
                  print "Timed out. Will retry."
                  sys.stdout.flush()
                  fl.write(str(mode) + " " + str(invocation) + " " + str(nC) + " " + benchmark + " " + suite + " = Failed (timeout)\n")
                  continue
              elif passed:
                print "OK"
                sys.stdout.flush()
                tries[i] = -2
                fl.write(str(mode) + " " + str(invocation) + " " + str(nC) + " " + benchmark + " " + suite + " = OK\n")
              else:
                if not haltOnFailure:
                    print "Failed. Will retry"
                    sys.stdout.flush()
                    fl.write(str(mode) + " " + str(invocation) + " " + str(nC) + " " + benchmark + " " + suite + " = Failed (Error)\n")
                    continue
                else:
                    print "Failed. Will not retry"
                    sys.stdout.flush()
                    tries[i] = -1
                    fl.write(str(mode) + " " + str(invocation) + " " + str(nC) + " " + benchmark + " " + suite + " = Failed & Halted (Error)\n")

          print str(zip(benchmarks, tries))
          sys.stdout.flush()
  #os.system("mkdir results"+mode+fn)
  #os.system("mv results/* results"+mode+fn+"/")
  #os.system("rm -rf results")
  #os.system("mkdir results")

fl.close()
