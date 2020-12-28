#!/bin/bash

source run.sh
source build.sh

build_go go/bench2.go

echo -e "$GREEN ...RUN GO Server... $RESET"
./bench2.exe 8888 

rm bench2.exe