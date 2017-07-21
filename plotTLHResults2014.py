#!/usr/bin/env python

import sys
from processTLHResults2014 import BenchmarkData
import pickle
import os
import operator
import os.path

# Write a CSV file based on the data given as parameters
#
# filename: the file to write (without the .csv extension)
# xs: a list of x axis values
# ys: a list of y axis values
# data: a list of lists representing data, in the format: [x [y/value]]
def generateCSV(benchmarkData, filename, xs, ys, data):
    if len(xs) != len(data):
        print len(xs)
        print len(data)
        print ("Data length mismatch: " + filename)
        sys.exit(1)
     
    f = open(getCSVFilename(benchmarkData, filename), "w")
    f.write("x")
    for y in ys:
        f.write("" + "," + str(y))
    f.write("\n")

    for i in range(0, len(data)):
        f.write(str(xs[i]))
        for j in range(0, len(data[i])):
            f.write(", " + str(data[i][j]))
        f.write("\n")
        
    f.close()

def getCSVFilename(benchmarkData, filename):
    return benchmarkData.benchmark + "-" + benchmarkData.mode + "-" + benchmarkData.invocationNumber + "-" + filename + ".csv"
def getScriptFilename(benchmarkData, filename):
    return benchmarkData.benchmark + "-" + benchmarkData.mode + "-" + benchmarkData.invocationNumber + "-" + filename + ".R"

def singlePlot(filename, R):
    return """
    pdf('%s.pdf')
    %s
    dev.off()
    """ % (filename, R)

# Graphs:
def graph_maximumRemsetOverhead(benchmarkData):
    data = []
    xs = []
    ys = ["Grey Objects in remset", "Black Objects in remset"]
    for threadID in sorted(benchmarkData.maxRemsetOverhead):
        (rGrey, rBlack, rTotal) = benchmarkData.maxRemsetOverhead[threadID]
        xs.append(threadID)
        data.append([rGrey, rBlack])
    generateCSV(benchmarkData, "maximumRemsetOverhead", xs, ys, data)
    
    return """
    dat <- read.csv("%s", sep=",", header=TRUE, row.names=1)
    par(mar = c(5,5,4,2) + 0.1)
    barplot(t(dat), main="%s", xlab="%s", ylab="%s", cex.axis = 1.5, cex.lab = 2, xaxt="n", ann=FALSE, las=0, border = NA, col=c("grey", "black"))
    """ % (getCSVFilename(benchmarkData, "maximumRemsetOverhead"), "", "Threads", "Remset Entries")
    
def graph_remsetSizeProgression(benchmarkData):
    toTransform = {}
    data = []
    xs = []
    ys = [""]
    
    script = """
    colors <- rainbow(%d)
    """ % (len(benchmarkData.tigc))
    
    #What are the maximum number of TIGCs with remset data
    nTIGCs = 0
    for threadID in sorted(benchmarkData.tigc):
        for tigcID in sorted(benchmarkData.tigc[threadID]):
            (_, _, _, _, rGrey, rBlack, _) = benchmarkData.tigc[threadID][tigcID]
            if (tigcID > nTIGCs):
                nTIGCs = tigcID
    ylim = 0
    #Add data into an intermediate structure: {tigc: {threadid: remsetSize}}
    for threadID in sorted(benchmarkData.tigc):
        for tigcID in range(1, nTIGCs+1):
            try:
                (_, _, _, _, rGrey, rBlack, _) = benchmarkData.tigc[threadID][tigcID]
            except:
                continue
            try:
                toTransform[tigcID]
            except:
                toTransform[tigcID] = {}
            toTransform[tigcID][threadID] = rGrey+rBlack
            if (rGrey+rBlack > ylim):
                ylim = rGrey + rBlack
    #Transform the intermediate structure into a 2D list
    for tigcID in sorted(toTransform):
        data.append([toTransform[tigcID][threadID] for threadID in toTransform[tigcID]])
    ys = [threadID for threadID in benchmark.tigc]
    xs = range(1,nTIGCs+1)
    generateCSV(benchmarkData, "remsetSizeProgression", xs, ys, data)
    
    script += """
    dat <- read.csv("%s", sep=",")
    par(mar = c(5,5,4,2) + 0.1)
    plot(dat[,1], dat[,2], main="%s", ylim=range(c(0, %d)), cex.axis = 1.5, cex.lab = 2, xaxt="n", xlab="%s", ylab="%s", type="b", col=colors[1])
    for (i in 3:dim(dat)[2]) {
       par(mar = c(5,5,4,2) + 0.1)
       lines(dat[,1], dat[,i], type="b", col=colors[i-1])
    }
    """ % (getCSVFilename(benchmarkData, "remsetSizeProgression"), "", ylim, "Thread-independent GC", "Remset entries")
    return script

def nanosToMillis(v):
    return v / (1000*1000.0)

def graph_tigcMemReclaimed(benchmarkData):
    n = 1
    
    script = """
    colors <- rainbow(%d)
    """ % (len(benchmarkData.tigc))
    
    xlim = 0
    ylim = 0
    for threadID in sorted(benchmarkData.tigc):
        for tigcID in sorted(benchmarkData.tigc[threadID]):
            (s, p, _, _, _, _, _) = benchmarkData.tigc[threadID][tigcID]
            if nanosToMillis(s) > xlim and nanosToMillis(s) <= 100*1000:
                xlim = nanosToMillis(s)
            if (p > ylim):
                ylim = p
    
    for threadID in sorted(benchmarkData.tigc):
        xs = []
        ys = []
        for tigcID in sorted(benchmarkData.tigc[threadID]):
            (startTimeNanos, pagesFreed, nanosElapsed, liveWhite, rGrey, rBlack, reclaimedCells) = benchmarkData.tigc[threadID][tigcID]
            startTimeMillis = nanosToMillis(startTimeNanos)
            if (startTimeMillis > 100*1000):
                print (threadID, benchmarkData.threads[threadID])
                continue
            xs.append(str(startTimeMillis))
            ys.append(str(pagesFreed))
        xstr = ", ".join(xs)
        ystr = ", ".join(ys)
        
        script += """
        xs = c(%s)
        ys = c(%s)
        """ % (xstr, ystr)
        if n == 1:
            script += """
        par(mar = c(5,5,4,2) + 0.1)
        plot(xs, ys, type="b", col=colors[%d],
            xlim=range(c(0, %d)),
            cex.axis = 1.5, cex.lab = 2, 
            ylim=range(c(0, %d)),
            pch=19, xlab="Time (milliseconds)", ylab="Pages reclaimed",
            main=""
        )
        """ % (n,xlim,ylim)
        else:
            script += """
        par(mar = c(5,5,4,2) + 0.1)
        lines(xs, ys, type="b", pch=19, col=colors[%d])
        """ % (n,)
        
        n += 1
    return script

def graph_tigcMemReclaimedOverTime(benchmarkData):
    n = 1
    
    script = """
    colors <- rainbow(%d)
    """ % (len(benchmarkData.tigc))
    
    xlim = 0
    ylim = 0
    for threadID in sorted(benchmarkData.tigc):
        for tigcID in sorted(benchmarkData.tigc[threadID]):
            (_, p, s, _, _, _, _) = benchmarkData.tigc[threadID][tigcID]
            if nanosToMillis(s) > xlim and nanosToMillis(s) <= 100*1000:
                xlim = nanosToMillis(s)
            if (p > ylim):
                ylim = p
    
    for threadID in sorted(benchmarkData.tigc):
        xs = []
        ys = []
        for tigcID in sorted(benchmarkData.tigc[threadID]):
            (startTime, pagesFreed, nanosElapsed, liveWhite, rGrey, rBlack, reclaimedCells) = benchmarkData.tigc[threadID][tigcID]
            millisElapsed = nanosToMillis(nanosElapsed)
            if millisElapsed > 100*1000:
                print (threadID, benchmarkData.threads[threadID])
                continue
            xs.append(str(millisElapsed))
            ys.append(str(pagesFreed))
        xstr = ", ".join(xs)
        ystr = ", ".join(ys)
        
        script += """
        xs = c(%s)
        ys = c(%s)
        """ % (xstr, ystr)
        if n == 1:
            script += """
        par(mar = c(5,5,4,2) + 0.1)
        plot(xs, ys, type="p", col=colors[%d],
            xlim=range(c(0, %d)),
            ylim=range(c(0, %d)),
            cex.axis = 1.5, cex.lab = 2, 
            pch=19, xlab="Time (milliseconds)", ylab="Pages reclaimed",
            main=""
        )
        """ % (n,xlim,ylim)
        else:
            script += """
        par(mar = c(5,5,4,2) + 0.1)
        lines(xs, ys, type="p", pch=19, col=colors[%d])
        """ % (n,)
        
        n += 1
    return script
    
def graph_barrierActions(benchmarkData):
    data = []
    xs = []
    lines = ["Barriers that have no action (on white/grey objects)", "Barriers that have no action (on black objects)", "Barriers resulting in a shade", "Barriers resulting in a globalise"]
    for threadID in sorted(benchmarkData.threadCounters):
        no = 0
        noB = 0
        noBRVM = 0
        shade = 0
        globalise = 0
        ttl = 0
        for counter in benchmarkData.threadCounters[threadID]:
            if counter in benchmarkData.counterShade:
                shade += benchmarkData.threadCounters[threadID][counter]
            if counter in benchmarkData.counterGlobalise:
                globalise += benchmarkData.threadCounters[threadID][counter]
            if counter in benchmarkData.counterNoAction:
                no += benchmarkData.threadCounters[threadID][counter]
            if counter in benchmarkData.counterBlackNoAction:
                noB += benchmarkData.threadCounters[threadID][counter]
            if counter in benchmarkData.counterBlackNoActionRVM:
                noBRVM += benchmarkData.threadCounters[threadID][counter]
            ttl += benchmarkData.threadCounters[threadID][counter]
        if ttl > 0:
            xs.append(threadID)
            data.append([no*100.0/ttl, noB*100.0/ttl, noBRVM*100.0/ttl, shade*100.0/ttl, globalise*100.0/ttl])
    generateCSV(benchmarkData, "barrierActions", xs, lines, data)
    
    return """
    dat <- read.csv("%s", sep=",", header=TRUE, row.names=1)
    par(mar = c(5,5,4,2) + 0.1)
    barplot(t(dat), main="%s", xlab="%s", cex.axis = 1.5, xaxt="n", ylab="%s", cex.lab = 2, ann=FALSE, las=0, border = NA, col=rainbow(5))
    """ % (getCSVFilename(benchmarkData, "barrierActions"), "", "Threads", "% of thread barrier invocations")


benchmarks = []
for benchmark in sys.argv[1:]:
    print "Unpickling " + benchmark
    
    b = pickle.load(open(benchmark, "r"))
    for t in b.threadCounters:
        if b.benchmark != "sunflow2009":
            continue
        s = 0
        for tc in b.threadCounters[t]:
            s += b.threadCounters[t][tc]
        if (s == 0):
            continue
        print t,
        print str(s) + ": ",
        for tc in b.threadCounters[t]:
            if int(tc) > 0:
                print str(tc) + ":" + str(b.threadCounters[t][tc]) + " " + str(int(b.threadCounters[t][tc] * 100.0 / s)) + "% ",
        print ""
            
    benchmarks.append(b)

for benchmark in benchmarks:
    #maximumRemsetOverhead
    f = open(getScriptFilename(benchmark, "maximumRemsetOverhead"), "w")
    f.write(singlePlot(getScriptFilename(benchmark, "maximumRemsetOverhead")[:-2], graph_maximumRemsetOverhead(benchmark)))
    f.close()
    #remsetSizeProgression
    f = open(getScriptFilename(benchmark, "remsetSizeProgression"), "w")
    f.write(singlePlot(getScriptFilename(benchmark, "remsetSizeProgression")[:-2], graph_remsetSizeProgression(benchmark)))
    f.close()
    #tigcMemReclaimed
    f = open(getScriptFilename(benchmark, "tigcMemReclaimed"), "w")
    f.write(singlePlot(getScriptFilename(benchmark, "tigcMemReclaimed")[:-2], graph_tigcMemReclaimed(benchmark)))
    f.close()
    #tigcMemReclaimedOverTime
    f = open(getScriptFilename(benchmark, "tigcMemReclaimedOverTime"), "w")
    f.write(singlePlot(getScriptFilename(benchmark, "tigcMemReclaimedOverTime")[:-2], graph_tigcMemReclaimedOverTime(benchmark)))
    f.close()
    #barrierActions
    f = open(getScriptFilename(benchmark, "barrierActions"), "w")
    f.write(singlePlot(getScriptFilename(benchmark, "barrierActions")[:-2], graph_barrierActions(benchmark)))
    f.close()
