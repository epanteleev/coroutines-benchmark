package main

import (
	"fmt"
	"os"
	"strconv"
	"time"
)

func channel(numThreads uint64) {

	ch := make(chan uint64)

	for i := uint64(0); i < numThreads; i++  {
		go func(i uint64) {
			ch <- i
		}(i)
	}
}

func main() {
	numCoroutines, _ := strconv.ParseUint(os.Args[1], 10, 64)

	start := time.Now()
	channel(numCoroutines)
	delta := time.Since(start)
	fmt.Println("Execution time: ", delta)
}