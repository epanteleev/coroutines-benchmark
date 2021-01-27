#!/bin/bash
#set -x
source ./run.sh
source ./build.sh

numTest=10
EXE=./client-bench3.exe

build_go client/client-bench3.go

echo -e "$GREEN ...Start Client... $RESET"

function start() {
  echo -e "Connections: $1"
  array=()
  for ((i = 0; i < $numTest; i++)) do
    res=$($EXE 8888 $1)
    nReq=$(echo $res | awk '{print $2}')
    array+=( $nReq )
    echo -e $res
  done

  mean=0
  for i in ${array[@]};
  do
    mean=$(( $mean + $i ))
  done

  mean=$(( $mean / $numTest ))

  std=0
  for j in ${array[@]}; 
  do
    val=$(( $j - $mean ))
    std=$(( $std + $val * $val ))
  done

  std=$(( $std / $numTest ))
  std=$(echo -e $std | awk '{print sqrt($1)}')

  echo -e "$GREEN Mean: $mean (-/+ $std) $RESET"

}

start 100
start 1000
start 2000
start 3000
start 4000
start 5000

rm $EXE
