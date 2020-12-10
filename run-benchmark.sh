#!/bin/bash

GO_HOME='D:\Go'
LOOM_HOME='D:\Java\jdk-16'
MAVEN_HOME='D:\Java\apache-maven-3.6.3'

RED=`tput setaf 1`
GREEN=`tput setaf 2`
RESET=`tput sgr0`

ROOT=$(pwd)

function run_loom() {
  echo -e "$RED Coroutines: $2 $RESET"
  $LOOM_HOME/bin/java $1 $2
}

function run_go() {
  echo -e "$RED Coroutines: $1 $RESET"
  $GO_HOME/bin/go run $1 $2 
}

function build_kotlin() {
  echo -e "$RED Build $1 $RESET"
  cd $1
  $MAVEN_HOME/bin/mvn package > log.txt
  cd $ROOT
}

function run_kotlin() {
  echo -e "$RED Coroutines: $3 $RESET"
  JAR_FILE="$1/target/$2-1.0-SNAPSHOT-jar-with-dependencies.jar"
  $JAVA_HOME/bin/java -jar $JAR_FILE $3
}

echo -e "$GREEN ...RUN LOOM... $RESET"
run_loom loom/Bench0.java 1000
run_loom loom/Bench0.java 10000
run_loom loom/Bench0.java 100000
run_loom loom/Bench0.java 1000000

echo -e "$GREEN ...RUN GO... $RESET"
run_go go/bench0.go 1000
run_go go/bench0.go 10000
run_go go/bench0.go 100000
run_go go/bench0.go 1000000


build_kotlin kotlin/bench0

echo -e "$GREEN ...RUN KOTLIN... $RESET"
run_kotlin kotlin/bench0 bench0 1000                                          
run_kotlin kotlin/bench0 bench0 10000                                          
run_kotlin kotlin/bench0 bench0 100000                                          
run_kotlin kotlin/bench0 bench0 1000000                                          
