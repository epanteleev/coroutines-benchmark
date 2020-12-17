#!/bin/bash

source run.sh

echo -e "$GREEN ...RUN GO Server... $RESET"
run_go go/bench2.go 8888 
