#!/usr/bin/env python

import sys
import os.path
import time

iter = 1

def execute(command):
    #print "Executing: " + command
    os.system(command)

#Perform one iteration. Walk through the results
def walk(iter, path):
    found = False
    execute("mkdir iter"+str(iter))
    i = 0

    for root, dirs, files in os.walk(path, topdown=True):
        i += 1
        if root.startswith("./iter") and not root.startswith("./iter"+str(iter-1)):
            continue
        for name in files:
            if (name.endswith(".jar")):
                found = True
                print os.path.join(root,name)
                execute("mkdir -p ./iter" + str(iter) + "/" + str(i) + "; cd ./iter" + str(iter) + "/" + str(i) + " && mkdir " + name[:-4] + " && cd " + name[:-4] + " && jar xf ../../../" + os.path.join(root,name)[2:])
    return found

#Recursively extract all jar files. A jar file may contain other jar files, so multiple walks may be needed.
foundJar = True
walk(iter, ".")
while foundJar:
    iter += 1
    foundJar = walk(iter, "./iter"+str(iter-1))