#!/bin/bash

GO_HOME='D:\Go'
LOOM_HOME='D:\Java\jdk-16'
MAVEN_HOME='D:\Java\apache-maven-3.6.3'

RED=`tput setaf 1`
GREEN=`tput setaf 2`
YELLOW=`tput setaf 3`
RESET=`tput sgr0`

ROOT=$(pwd)

function run_loom() {
  $LOOM_HOME/bin/java $1 $2
}

function run_go() {
  $GO_HOME/bin/go run $1 $2 
}

function run_kotlin() {
  JAR_FILE="$1/target/$2-1.0-SNAPSHOT-jar-with-dependencies.jar"
  $JAVA_HOME/bin/java -jar $JAR_FILE $3
}

function run_loom0() {
  numCoroutines=$(echo $2 | awk '{print $1}')
  echo -e "$RED Coroutines: $numCoroutines $RESET"
  run_loom $1 "$2"
}

function run_go0() {
  numCoroutines=$(echo $2 | awk '{print $1}')
  echo -e "$RED Coroutines: $numCoroutines $RESET"
  run_go $1 "$2" 
}

function run_kotlin0() {
  numCoroutines=$(echo $3 | awk '{print $1}')
  echo -e "$RED Coroutines: $numCoroutines $RESET"
  run_kotlin $1 "$2" "$3"
}

