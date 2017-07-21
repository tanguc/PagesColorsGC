#!/bin/bash

cd Benchmark && javac -source 6 -target 6 -classpath .:../target/mmtk/mmtk.jar *.java && jar cvfm Benchmark.jar Manifest.txt *.class && cd ..
