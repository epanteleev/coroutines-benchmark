package main

import (
	"log"
	"net"
	"os"
	"sync"
)

func handleConn(conn net.Conn, wg *sync.WaitGroup) {
	defer wg.Done()
	var buf = make([]byte, 13)
	for {
		n, err := conn.Read(buf)
		if err != nil {
			return
		}
		_, err = conn.Write(buf)
		if err != nil {
			return
		}
		buf = buf[0: n]
	}
}

func main() {
	address := "localhost:" + os.Args[1]
	ln, err := net.Listen("tcp", address)
	if err != nil {
		panic(err)
	}

	defer func() {
		if err := ln.Close(); err != nil {
			panic(err)
		}
	}()

	var wg sync.WaitGroup
	for {
		conn, e := ln.Accept()
		if e != nil {
			if ne, ok := e.(net.Error); ok && ne.Temporary() {
				log.Printf("accept temp err: %v", ne)
			} else {
				log.Printf("accept err: %v", e)
				break
			}
		} else {
			wg.Add(1)
			go handleConn(conn, &wg)
		}
	}
}