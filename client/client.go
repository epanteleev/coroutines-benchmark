package main

import (
	"fmt"
	"net"
	"os"
	"strconv"
	"sync"
	"sync/atomic"
	"time"
)

const message = "Hello_Server!"

func Connect(address string, numReq *int64, wg *sync.WaitGroup) {
	ln, err := net.Dial("tcp", address)
	if err != nil {
		panic(err)
	}
	defer func() {
		ln.Close()
		wg.Done()
	}()

	buf := make([]byte, 13)
	for {
		m := []byte(message)
		_, err := ln.Write(m)
		if err != nil {
			break
		}

		n, err := ln.Read(buf)
		if err != nil {
			break
		}

		if string(buf) != message {
			panic(string(buf))
		}
		atomic.AddInt64(numReq, 1)

		buf = buf[0: n]
	}
}

func main() {
	address := "localhost:" + os.Args[1]
	numCoroutines, _ := strconv.ParseInt(os.Args[2], 10, 32)

	var numReq int64 = 0
	var wg sync.WaitGroup
	for i := int64(0); i < numCoroutines; i++ {
		var err error
		go Connect(address, &numReq, &wg)
		wg.Add(1)
		if err != nil {
			panic(err)
		}
	}

	go func() {
		timer := time.NewTimer(time.Second * 60)
		select {
		case <-timer.C:
			fmt.Printf("Connection_request_size: %d\n", numReq)
			os.Exit(0)
		}
	}()

	wg.Wait()
}