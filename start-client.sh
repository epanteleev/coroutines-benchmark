#!/bin/bash

source ./run.sh
source ./build.sh

echo -e "$GREEN ...Start Client... $RESET"
numReq=0
numTest=10

for ((i = 0; i < $numTest; i++)) do
  res=$(run_go client/client.go "8888 100")
  nReq=$(echo $res | awk '{print $2}')
  numReq=$(( $numReq + $nReq ))
  echo -e $res
done

echo -e "$GREEN Mean: $(( $numReq / $numTest )) $RESET"


