#!/bin/bash

source ./run.sh
source ./build.sh

echo -e "$YELLOW Bench0 $RESET"

echo -e "$GREEN ...RUN LOOM... $RESET"
run_loom0 loom/Bench0.java 1000
run_loom0 loom/Bench0.java 10000
run_loom0 loom/Bench0.java 100000
run_loom0 loom/Bench0.java 1000000

echo -e "$GREEN ...RUN GO... $RESET"
run_go0 go/bench0.go 1000
run_go0 go/bench0.go 10000
run_go0 go/bench0.go 100000
run_go0 go/bench0.go 1000000

build_mvn kotlin/bench0

echo -e "$GREEN ...RUN KOTLIN... $RESET"
run_kotlin kotlin/bench0 bench0 1000                                          
run_kotlin kotlin/bench0 bench0 10000                                          
run_kotlin kotlin/bench0 bench0 100000                                          
run_kotlin kotlin/bench0 bench0 1000000                                          

echo -e "$YELLOW Bench1 $RESET"

echo -e "$GREEN ...RUN LOOM... $RESET"

mkdir tmp

for ((i=10; i <= 1000000; i=i*10)) do 
  run_loom0 loom/Bench1.java "$i tmp/outLoom.txt"
done


echo -e "$GREEN ...RUN GO... $RESET"

for ((i=10; i <= 1000000; i=i*10)) do
  run_go0 go/bench1.go "$i tmp/outGo.txt"
done

build_mvn kotlin/bench1

echo -e "$GREEN ...RUN KOTLIN... $RESET"

for ((i=10; i <= 1000000; i=i*10)) do 
  run_kotlin kotlin/bench1 bench1 "$i tmp/outKotlin.txt"
done

rm -rf tmp