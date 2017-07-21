#!/usr/bin/env python

import sys, re, os, time
import os.path
from struct import unpack
import pickle

class BenchmarkData:
    
    def __init__(self):
        self.benchmark = ""
        self.mode = ""
        self.invocationNumber = ""
            
        #gc = {id: (startTime, pagesFreed, nanosElapsed)}
        self.gc = {}
        #tigc = {threadID: {id: (startTime, pagesFreed, nanosElapsed, remsetGreyEntries, remsetBlackEntries, reclaimedCells)}}
        self.tigc = {}
        #threads = {id: (category, name)}
        self.threads = {1 : (1, "BootThread")}
        #types = {id : (isArray, isPrimitive, hasFinaliser, nPrimFields, nRefFields, nStaticPrimFields, nStaticRefFields, name)}
        self.types = {}
        #spaces = {id: name}
        self.spaces = {}
        #threadCounters = {threadID: {counterID: value}}
        self.threadCounters = {}
        
        self.counterShade = [1, 4, 12]
        self.counterGlobalise = [0, 5, 10]
        self.counterNoAction = [2, 3, 7, 8, 13]
        self.counterBlackNoAction = [9, 14, 6, 11]
        self.counterBlackNoActionRVM = [15]
        
        
        self.counterReadBlackNoActionx = [9, 6, 15]
        self.counterNoActionx = [2, 7, 9, 6, 15, 3, 8, 13, 11, 14]
        self.counterWhiteNoActionx = [2, 7]
        
        #generated:
        
        #maxRemsetOverhead = {threadID: (remsetGreyEntries, remsetBlackEntries, totalRemsetEntries)}
        self.maxRemsetOverhead = {}
        
    def logGC2(self, gcID, startTime, pagesFreed, nanosElapsed):
        #print gcID, startTime, pagesFreed, nanosElapsed
        self.gc[gcID] = (startTime, pagesFreed, nanosElapsed)
    
    def logTIGC(self, threadID, tigcNumber, startTime, pagesFreed, nanosElapsed, liveWhite, rGrey, rBlack, reclaimedCells):
        #print threadID, tigcNumber, startTime, pagesFreed, nanosElapsed, liveWhite, rGrey, rBlack, reclaimedCells
        try:
            self.tigc[threadID]
        except:
            self.tigc[threadID] = {}
        try:
            self.maxRemsetOverhead[threadID]
        except:
            self.maxRemsetOverhead[threadID] = (0,0,0)
        if (self.maxRemsetOverhead[threadID][2] < (rGrey + rBlack)):
            self.maxRemsetOverhead[threadID] = (rGrey, rBlack, rGrey+rBlack)
        self.tigc[threadID][tigcNumber] = (startTime, pagesFreed, nanosElapsed, liveWhite, rGrey, rBlack, reclaimedCells)

    def logThread(self, threadID, category, name):
        self.threads[threadID] = (category, name)
        
    def logSpace(self, spaceID, name):
        self.spaces[spaceID] = name
        
    def logType(self, typeID, isArray, isPrimitive, hasFinaliser, nPrimFields, nRefFields, nStaticPrimFields, nStaticRefFields, name):
        self.types[typeID] = (isArray, isPrimitive, hasFinaliser, nPrimFields, nRefFields, nStaticPrimFields, nStaticRefFields, name)
       
    def logThreadCounter(self, threadID, counterID, counterValue):
       try:
           self.threadCounters[threadID]
       except:
           self.threadCounters[threadID] = {}
       try:
           self.threadCounters[threadID][counterID]
       except:
           self.threadCounters[threadID][counterID] = 0
       self.threadCounters[threadID][counterID] += counterValue
       
    def done(self):
        ttl = 0
        noAct = 0
        noActReadBlack = 0
        noActLocalWhite = 0
        for t in self.threadCounters:
            for counterID in self.threadCounters[t]:
                ttl += self.threadCounters[t][counterID]
                if counterID in self.counterNoActionx:
                    noAct += self.threadCounters[t][counterID]
                if counterID in self.counterReadBlackNoActionx:
                    noActReadBlack += self.threadCounters[t][counterID]
                if counterID in self.counterWhiteNoActionx:
                    noActLocalWhite += self.threadCounters[t][counterID]
        if ttl > 0:
            print "%15s : & %5dm & %5dm (%5.2f%%) & %5dm (%5.2f%%) & %5dm (%5.2f%%) \\\\" % (self.benchmark, ttl / 1000000, noAct / 1000000, noAct*100.0/ttl, noActReadBlack / 1000000, noActReadBlack * 100.0 / ttl, noActLocalWhite / 1000000, noActLocalWhite * 100.0 / ttl)
                

def readIn(filename, data):
    ttl_bytes = 0
    rf = open(filename, "rb")
    bytes_read = rf.read(4)
    ttl_bytes += 4
    while bytes_read:
        align, = unpack("i", bytes_read)
            
        if align == 2147483641: #logGC
            extra_read = rf.read(28)
            ttl_bytes += 28
            gcNumber, startTime, pagesFreed, nanosElapsed, alignEnd = unpack("=iqiqi", extra_read)
            if (alignEnd != 2147483647):
                print "Alignment end error"
                print alignEnd
                print ttl_bytes
                sys.exit(1)
            data.logGC2(gcNumber, startTime, pagesFreed, nanosElapsed)
        
        elif align == 2147483642: #logThread
            extra_read = rf.read(16)
            ttl_bytes += 16
            threadID, category, namelen, alignEnd = unpack("=iiii", extra_read)
            name = ""
            for i in range(0, int(namelen)):
                extra_read = rf.read(1)
                ttl_bytes += 1
                (char, ) = unpack("=c", extra_read)
                name = name + char
            if (namelen % 4 != 0):
                for i in range(0, 4 - (namelen % 4)):
                    extra_read = rf.read(1)
                    ttl_bytes += 1
                    (char, ) = unpack("=c", extra_read)
            if (alignEnd != 2147483647):
                print "Alignment end error"
                print alignEnd
                print ttl_bytes
                sys.exit(1)
            data.logThread(threadID, category, name)
            
        elif align == 2147483643: #logSpace
            extra_read = rf.read(12)
            ttl_bytes += 12
            spaceID, namelen, alignEnd = unpack("=iii", extra_read)
            name = ""
            for i in range(0, int(namelen)):
                extra_read = rf.read(1)
                ttl_bytes += 1
                (char, ) = unpack("=c", extra_read)
                name = name + char
            if (namelen % 4 != 0):
                for i in range(0, 4 - (namelen % 4)):
                    extra_read = rf.read(1)
                    ttl_bytes += 1
                    (char, ) = unpack("=c", extra_read)
            if (alignEnd != 2147483647):
                print "Alignment end error"
                print alignEnd
                print ttl_bytes
                sys.exit(1)
            data.logSpace(spaceID, name)
        
        elif align == 2147483644: #logTIGC
            extra_read = rf.read(60)
            ttl_bytes += 60
            threadID, tigcNumber, startTime, pagesFreed, nanosElapsed, liveWhite, rGrey, rBlack, reclaimedCells, alignEnd = unpack("=iiqiqiqqqi", extra_read)
            if (alignEnd != 2147483647):
                print "Alignment end error"
                print alignEnd
                print ttl_bytes
                sys.exit(1)
            data.logTIGC(threadID, tigcNumber, startTime, pagesFreed, nanosElapsed, liveWhite, rGrey, rBlack, reclaimedCells)
                
        elif align == 2147483645: #logType
            extra_read = rf.read(32)
            ttl_bytes += 32
            typeID, isArray, isPrimitive, hasFinaliser, x, nPrimFields, nRefFields, nStaticPrimFields, nStaticRefFields, namelen, alignEnd = unpack("=i???ciiiiii", extra_read)
            name = ""
            for i in range(0, int(namelen)):
                extra_read = rf.read(1)
                ttl_bytes += 1
                (char, ) = unpack("=c", extra_read)
                name = name + char
            if (namelen % 4 != 0):
                for i in range(0, 4 - (namelen % 4)):
                    extra_read = rf.read(1)
                    ttl_bytes += 1
                    (char, ) = unpack("=c", extra_read)
            if (alignEnd != 2147483647):
                print "Alignment end error"
                print alignEnd
                print ttl_bytes
                sys.exit(1)
            data.logType(typeID, isArray, isPrimitive, hasFinaliser, nPrimFields, nRefFields, nStaticPrimFields, nStaticRefFields, name)
            
        elif align == 2147483646: #logThreadCounter
            extra_read = rf.read(16)
            ttl_bytes += 16
            threadID, counterID, counterValue, alignEnd = unpack("=iiii", extra_read)
            if (alignEnd != 2147483647):
                print "Alignment end error"
                print alignEnd
                print ttl_bytes
                sys.exit(1)
            data.logThreadCounter(threadID, counterID, counterValue)
            
        elif align == 2147483640: #logThreadCounterOF
            extra_read = rf.read(16)
            ttl_bytes += 16
            threadID, counterID, counterValue, alignEnd = unpack("=iiii", extra_read)
            if (alignEnd != 2147483647):
                print "Alignment end error"
                print alignEnd
                print ttl_bytes
                sys.exit(1)
            data.logThreadCounter(threadID, counterID, counterValue)
            
        else:
            print "Alignment error"
            print align
            print ttl_bytes
            sys.exit(1)
        bytes_read = rf.read(4)
        ttl_bytes += 4
        
    rf.close()

def main():
    directories = sys.argv[1:]
    datas = {}
    
    for directory in directories:
        print "Walking through " + directory
        for fullRoot, dirs, files in os.walk(directory):
            for file in files:
                if ".log.stats" not in file:
                    continue
                benchmark = os.path.basename(file).split(".")[0].split("-")[0]
                mode = os.path.basename(file).split(".")[0].split("-")[1]
                invocationNumber = os.path.basename(file).split(".")[0].split("-")[2]
                
                item = benchmark + "-" + mode + "-" + invocationNumber
    
                if item not in datas:
                    datas[item] = BenchmarkData()
                    datas[item].benchmark = benchmark
                    datas[item].mode = mode
                    datas[item].invocationNumber = invocationNumber
                print fullRoot + "/" + file
                readIn(fullRoot + "/" + file, datas[item])
        
    for item in datas:
        outputFile = datas[item].benchmark+"-"+datas[item].mode+"-"+datas[item].invocationNumber+".proc"
        x = open(outputFile, "w")
        datas[item].done()
        pickle.dump(datas[item], x)
        x.close()

if __name__ == "__main__":
    main()
    