#!/bin/bash

source build.sh

echo -e "$GREEN ...RUN Server... $RESET"

run_loom loom/Bench3Coroutine.java "8888 data/image.jpg"
