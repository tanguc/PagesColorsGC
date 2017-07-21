#!/usr/bin/env python

import sys
import os
import operator
import os.path
import subprocess
import math

#Benchmark OFF
#Assigned core for thread 321: 25
#Assigned core for thread 322: 41

# STEP 1: Read all files into a structure:

#scaleData = {benchmark: {mode: {ncpus: [inv1ns, inv2ns, inv3ns]}}}
scaleData = {}

#perfData = {benchmark: {mode: [inv1ns, inv2ns, inv3ns]}}
perfData = {}

def processFile(filename):
    benchmark = os.path.basename(filename).split("-")[0]
    ncpus = os.path.basename(os.path.dirname(filename))[:-4]
    invocation = os.path.basename(os.path.dirname(os.path.dirname(filename)))
    mode = os.path.basename(os.path.dirname(os.path.dirname(os.path.dirname(filename))))
    if (".prep" in os.path.basename(filename)[-10:] or ".stats" in os.path.basename(filename)[-10:]):
        return (benchmark, ncpus, invocation, mode, "-1", "-1")
    try:
        secondsElapsed = subprocess.Popen("grep \"Seconds Elapsed:\" " + filename, shell=True, stdout=subprocess.PIPE).communicate()[0].strip().split()[2]
        nanosElapsed = subprocess.Popen("grep \"Nanos Elapsed:\" " + filename, shell=True, stdout=subprocess.PIPE).communicate()[0].strip().split()[2]
    except:
        return (benchmark, ncpus, invocation, mode, "-1", "-1")
    return (benchmark, ncpus, invocation, mode, secondsElapsed, nanosElapsed)

if len(sys.argv) != 2:
    print "Usage: " + sys.argv[0] + " pathToResultsTLD"
    sys.exit(1)
path = os.getcwd() + "/" + sys.argv[1]
if not os.path.isdir(path):
    print "Usage: " + sys.argv[0] + " pathToResultsTLD"
    sys.exit(1)
    
def nanosToSeconds(v):
    return int(v) / (1000*1000*1000.0)

for fullRoot, dirs, files in os.walk(path):
    if (fullRoot == path):
        continue
    root = fullRoot[len(path)+1:]
    if len(root.split("/")) == 3:
        for file in files:
            (benchmark, ncpus, invocation, mode, secs, nanos) = processFile(fullRoot + "/" + file)
            if (nanos != "-1"):
                print (benchmark, ncpus, invocation, mode, secs, nanos)
                if ncpus == "None":
                    if benchmark not in perfData:
                        perfData[benchmark] = {}
                    if mode not in perfData[benchmark]:
                        perfData[benchmark][mode] = []
                    perfData[benchmark][mode].append(nanosToSeconds(nanos))
                else:
                    if benchmark not in scaleData:
                        scaleData[benchmark] = {}
                    if mode not in scaleData[benchmark]:
                        scaleData[benchmark][mode] = {}
                    if ncpus not in scaleData[benchmark][mode]:
                        scaleData[benchmark][mode][ncpus] = []
                    scaleData[benchmark][mode][ncpus].append(nanosToSeconds(nanos))

# STEP 2: Transform the data

#avgSData = {benchmark: {mode: {ncpus: avg}}}
avgSData = {}
#stddevSData = {benchmark: {mode: {ncpus: stddev}}}
stddevSData = {}

#avgPData = {benchmark: {mode: avg}}
avgPData = {}
#stddevPData = {benchmark: {mode: {ncpus: stddev}}}
stddevPData = {}

def calcAverage(pointList):
    sumV = 0
    for item in pointList:
        sumV += item
    n = len(pointList)
    return (sumV / float(n))
    
def calcStdDev(pointList, average):
    sumSq = 0
    for item in pointList:
        diff = item - average
        sq = diff * diff
        sumSq += sq
    n = len(pointList)
    z = math.sqrt(sumSq / (n)) 
    return (1 if z == 0 else z)
    
for benchmark in scaleData:
    for mode in scaleData[benchmark]:
        for ncpus in scaleData[benchmark][mode]:
            if benchmark not in avgSData:
                avgSData[benchmark] = {}
                stddevSData[benchmark] = {}
            if mode not in avgSData[benchmark]:
                avgSData[benchmark][mode] = {}
                stddevSData[benchmark][mode] = {}
            if ncpus not in avgSData[benchmark][mode]:
                avgSData[benchmark][mode][ncpus] = 0
                stddevSData[benchmark][mode][ncpus] = 0
            avg = calcAverage(scaleData[benchmark][mode][ncpus])
            stddev = calcStdDev(scaleData[benchmark][mode][ncpus], avg)
            avgSData[benchmark][mode][ncpus] = avg
            stddevSData[benchmark][mode][ncpus] = stddev

for benchmark in perfData:
    for mode in perfData[benchmark]:
        if benchmark not in avgPData:
            avgPData[benchmark] = {}
            stddevPData[benchmark] = {}
        if mode not in avgPData[benchmark]:
            avgPData[benchmark][mode] = 0
            stddevPData[benchmark][mode] = 0
        avg = calcAverage(perfData[benchmark][mode])
        stddev = calcStdDev(perfData[benchmark][mode], avg)
        avgPData[benchmark][mode] = avg
        stddevPData[benchmark][mode] = stddev
        
print perfData
print avgPData
print stddevPData

# STEP 3: Write R scripts

#avg = c(94, 60, 42, 36, 30, 23)
#sdev = c(49.1799417107, 1, 0.5, 0.6, 0.2, 2)

#x <- 1:6

#plot(x, avg,
#    ylim=range(c(avg-sdev, avg+sdev)),
#    pch=19, xlab="Measurements", ylab="Mean +/- SD",
#    main="Scatter plot with std.dev error bars"
#)
# hack: we draw arrows but with very special "arrowheads"
#arrows(x, avg-sdev, x, avg+sdev, length=0.05, angle=90, code=3)

def genSingleScalabilityGraph(benchmark, mode, avgData, stddevData):
    aList = [str(avgData[k]) for k in sorted(avgData, key=int)]
    sList = [str(stddevData[k]) for k in sorted(stddevData, key=int)]
    kList = [str(k) for k in sorted(avgData, key=int)]
    averages = ", ".join(aList)
    stddevs = ", ".join(sList)
    cpus = ", ".join(kList)
    
    script = """
    pdf('%s.pdf')
    avg = c(%s)
    sdev = c(%s)
    x = c(%s)
    par(mar = c(5,5,4,2) + 0.1)
    plot(x, avg, type="b",
        ylim=range(c(avg-sdev, avg+sdev)),
        pch=19, xlab="Available CPUs", cex.axis = 1.5, cex.lab = 1.3, ylab="Execution time in seconds (Arith. Mean +/- StdDev)",
        main="%s"
    )
    arrows(x, avg-sdev, x, avg+sdev, length=0.05, angle=90, code=3)
    #legend("topright", pch=19, legend=c("%s"), col=c(1))
    dev.off()
    """ % (benchmark + "-" + mode, averages, stddevs, cpus, "", mode)
    
    f = open(benchmark + "-" + mode + "-scale.R", "w")
    f.write(script)
    f.close()
    
def genSingleScalabilityPlot(mode, n, avgData, stddevData, ylim=0):
    aList = [str(avgData[k]) for k in sorted(avgData, key=int)]
    sList = [str(stddevData[k]) for k in sorted(stddevData, key=int)]
    kList = [str(k) for k in sorted(avgData, key=int)]
    averages = ", ".join(aList)
    stddevs = ", ".join(sList)
    cpus = ", ".join(kList)
    
    colours = ["blue", "red"]
    script = """
    #Mode: %s
    avg = c(%s)
    sdev = c(%s)
    x = c(%s)
    """ % (mode, averages, stddevs, cpus)
    if n == 0:
        script += """
    par(mar = c(5,5,4,2) + 0.1)
    plot(x, avg, type="b", col=colors[1], #"red",
        ylim=range(c(0, %d)), #avg+sdev)),
        pch=19, cex.axis = 1.5, cex.lab = 1.3, xlab="Available CPUs", ylab="Execution time in seconds (Arith. Mean +/- StdDev)",
        main=""
    )
    arrows(x, avg-sdev, x, avg+sdev, length=0.05, angle=90, code=3)
    """ % ylim
    else:
        script += """
    par(mar = c(5,5,4,2) + 0.1)
    lines(x, avg, type="b", pch=19, col=colors[%d]) #col = "%s")
    arrows(x, avg-sdev, x, avg+sdev, length=0.05, angle=90, code=3)
    """ % (n+1 ,"")#(colours[n])
    return script

def genScalabilityGraphs(avgData, stddevData):
    for benchmark in avgData:
        n = 0
        script = """
        pdf('%s.pdf')
        colors <- rainbow(%d)
        #colors <- c("blue", "red")
        """ % (benchmark + "-scale", len(avgData[benchmark]))
        ylim = 0
        for mode in sorted(avgData[benchmark]):
            for ncpu in avgData[benchmark][mode]:
                if (avgData[benchmark][mode][ncpu] + stddevData[benchmark][mode][ncpu])> ylim:
                    ylim = avgData[benchmark][mode][ncpu] + stddevData[benchmark][mode][ncpu]
        for mode in sorted(avgData[benchmark]):
            genSingleScalabilityGraph(benchmark, mode, avgData[benchmark][mode], stddevData[benchmark][mode])
            script += genSingleScalabilityPlot(mode, n, avgData[benchmark][mode], stddevData[benchmark][mode], ylim)
            n += 1
        script += """
        #legend("topright", legend=c(%s), pch=19, col=colors) #%d
        dev.off()
        """ % (", ".join(['"' + str(mode) + '"' for mode in sorted(avgData[benchmark])]), len(avgData[benchmark]))
        f = open(benchmark + "-scale.R", "w")
        f.write(script)
        f.close()
            
def genPerformanceGraph(benchmark, avgData, stddevData):
    aList = [str(avgData[k]) for k in sorted(avgData, key=lambda s: s.lower())]
    sList = [str(stddevData[k]) for k in sorted(stddevData, key=lambda s: s.lower())]
    kList = ["\"" + str(k) + "\"" for k in sorted(avgData, key=lambda s: s.lower())]
    averages = ", ".join(aList)
    stddevs = ", ".join(sList)
    modes = ", ".join(kList)
    
    script = """
    pdf('%s.pdf')
    avg = c(%s)
    sdev = c(%s)
    x = c(%s)
    x2 = 1:c(%s)
    #idx <- order(c(seq_along(avg), seq_along(x)))
    #p = (c(avg,sdev))[idx]
    d = matrix(avg, nrow=length(x), ncol=1, dimnames=list(x))         
    # Create a matrix Deathrate with the data  
    d2 = t(d)
    par(mar = c(5,5,4,2) + 0.1)
    barplot(d2,
        #ylim=range(c(avg-sdev, avg+sdev)),
        pch=19, cex.axis = 1.5, cex.lab = 1.3, cex.names = 1.5, xlab="Configuration", ylab="Execution time in seconds (Arith. Mean +/- StdDev)",
        main="%s"
    )
    arrows(x2, avg-sdev, x2, avg+sdev, length=0.05, angle=90, code=3)
    dev.off()
    """ % (benchmark, averages, stddevs, modes, len(kList), "")
    
    f = open(benchmark + "-" + mode + "-perf.R", "w")
    f.write(script)
    f.close()
    
def genPerformanceGraphs(avgData, stddevData):
    print avgData
    for benchmark in avgData:
        genPerformanceGraph(benchmark, avgData[benchmark], stddevData[benchmark])

genScalabilityGraphs(avgSData, stddevSData)
genPerformanceGraphs(avgPData, stddevPData)
