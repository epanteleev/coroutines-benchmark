#!/bin/bash

source run.sh

function build_kotlin() {
  echo -e "$YELLOW Build $1 $RESET"
  cd $1
  $MAVEN_HOME/bin/mvn package > log.txt
  cd $ROOT
}