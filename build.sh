#!/bin/bash

source run.sh

function build_mvn() {
  echo -e "$YELLOW Build $1 $RESET"
  cd $1
  $MAVEN_HOME/bin/mvn package > log.txt
  cd $ROOT
}

function build_go() {
  echo -e "$YELLOW Build $1 $RESET"
  $GO_HOME/bin/go build $1 
}