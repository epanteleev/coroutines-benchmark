package main

import (
	"fmt"
	"os"
	"strconv"
	"time"
    "sync/atomic"
)

func channel(numThreads uint64) {
	var count uint64 = 0

	for i := uint64(0); i < numThreads; i++  {
		go func() {
			atomic.AddUint64(&count, 1)
		}()
	}
}

func main() {
	numCoroutines, _ := strconv.ParseUint(os.Args[1], 10, 64)

	var duration = time.Now().UnixNano()
	channel(numCoroutines)
	duration = (time.Now().UnixNano() - duration) / 1000000
	fmt.Println("Execution time: ", duration, " ms.")
}
