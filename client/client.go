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

const message = "Hello Server!"

func connect(ln *net.Conn, numReq *int64, wg *sync.WaitGroup) {
	defer wg.Done()

	buf := make([]byte, 100)
	for {
		_, err := (*ln).Write([]byte(message))
		if err != nil {
			return
		}
		n, err := (*ln).Read(buf)
		if err != nil {
			return
		}

		atomic.AddInt64(numReq, 1)
		buf = buf[0: n]
	}
}

func main() {
	address := "localhost:" + os.Args[1]
	numCoroutines, _ := strconv.ParseInt(os.Args[2], 10, 32)

	var connections = make([]net.Conn, numCoroutines)
	for i := 0; i < len(connections); i++ {
		var err error
		connections[i], err = net.Dial("tcp", address)
		if err != nil {
			panic(err)
		}
	}

	var numReq int64 = 0
	go func() {
		timer := time.NewTimer(time.Second * 30)
		select {
		case <-timer.C:
			fmt.Printf("Connection_request_size: %d\n", numReq)
			os.Exit(0)
		}
	}()

	var wg sync.WaitGroup
	for _, conn := range connections {
		go connect(&conn, &numReq, &wg)
		wg.Add(1)
	}
	wg.Wait()
}