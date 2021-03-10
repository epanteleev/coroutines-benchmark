package main

import (
	"fmt"
	"os"
	"runtime"
	"strconv"
	"time"
)

const COUNT = 100000000

func switchTask(count uint64) {
	var i uint64 = 0
	for {
		i += 1
		if i > count {
			break
		}
		runtime.Gosched()
	}
}

func main() {

	runtime.GOMAXPROCS(1)

	coroCount, _ := strconv.ParseUint(os.Args[1], 10, 64)

	var duration = time.Now().UnixNano()
	
	for j := uint64(0); j < coroCount; j++ {
		go switchTask(COUNT / coroCount)
	}

	switchTask(COUNT / coroCount)

	duration = (time.Now().UnixNano() - duration) / 1000000

	fmt.Printf("%d coroutines: %d switches in %d ms, %d switches per second\n", coroCount, COUNT, duration, (1000 * COUNT) / duration)
}
