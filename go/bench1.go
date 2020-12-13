package main

import (
	"fmt"
	"os"
	"strconv"
	"sync"
	"sync/atomic"
	"time"
)

const NumHello = 1_000_000
const message = "hello "

func task(numThreads int, f *os.File) {
	var inc int32 = 0
	var wg sync.WaitGroup
	for i := 0; i < numThreads; i++ {
		wg.Add(1)
		go func() {
			for atomic.LoadInt32(&inc) < NumHello {
				atomic.AddInt32(&inc, 1)
				_, err := f.Write([]byte(message)) // BUG: blocking write
				check(err)
			}
			wg.Done()
		}()
	}
	defer wg.Wait()
}

func check(e error) {
	if e != nil {
		panic(e)
	}
}

func main() {
	numCoroutines, _ := strconv.ParseInt(os.Args[1], 10, 32)

	file := os.Args[2]
	f, err := os.Create(file)
	check(err)
	defer f.Close()

	start := time.Now()
	task(int(numCoroutines), f)
	delta := time.Since(start)
	fmt.Println("Execution time: ", delta)
}
