#!/bin/bash

source run.sh
source build.sh

build_go go/benchSwitch.go

echo -e "$GREEN ...RUN Switch test GO... $RESET"
./benchSwitch.exe 100 
./benchSwitch.exe 1000
./benchSwitch.exe 5000
./benchSwitch.exe 10000
./benchSwitch.exe 100000
./benchSwitch.exe 1000000
rm benchSwitch.exe

echo -e "$GREEN ...RUN LOOM... $RESET"
run_loom loom/BenchSwitch.java "100"
run_loom loom/BenchSwitch.java "1000"
run_loom loom/BenchSwitch.java "5000"
run_loom loom/BenchSwitch.java "10000"
run_loom loom/BenchSwitch.java "100000"
run_loom loom/BenchSwitch.java "1000000"

echo -e "$GREEN ...RUN JAVA Threads... $RESET"
run_loom loom/BenchSwitchThread.java "100"
run_loom loom/BenchSwitchThread.java "1000"
run_loom loom/BenchSwitchThread.java "5000"
run_loom loom/BenchSwitchThread.java "10000"
run_loom loom/BenchSwitchThread.java "100000"
run_loom loom/BenchSwitchThread.java "1000000"