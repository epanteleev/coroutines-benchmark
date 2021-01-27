#!/bin/bash

source build.sh

build_mvn loom/Bench Bench

echo -e "$GREEN ...RUN Server... $RESET"

run_jar loom/Bench Bench "8888 data/image.jpg"

