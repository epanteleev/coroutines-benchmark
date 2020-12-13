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
  numCoroutines=$(echo $2 | awk '{print $1}')
  echo -e "$RED Coroutines: $numCoroutines $RESET"
  $LOOM_HOME/bin/java $1 $2
}

function run_go() {
  numCoroutines=$(echo $2 | awk '{print $1}')
  echo -e "$RED Coroutines: $numCoroutines $RESET"
  $GO_HOME/bin/go run $1 $2 
}

function run_kotlin() {
  numCoroutines=$(echo $3 | awk '{print $1}')
  echo -e "$RED Coroutines: $numCoroutines $RESET"
  JAR_FILE="$1/target/$2-1.0-SNAPSHOT-jar-with-dependencies.jar"
  $JAVA_HOME/bin/java -jar $JAR_FILE $3
}
