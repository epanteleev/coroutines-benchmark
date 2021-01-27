package org.bench;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.StandardCharsets;

public class Worker implements Runnable {
    private Selector selector;
    public Worker() {
        try {
            this.selector = SelectorProvider.provider().openSelector();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        key.cancel();

        selector.wakeup();
        socketChannel.register(this.selector, SelectionKey.OP_READ);
    }

    private String padLeftOnes(String inputString, int length) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - inputString.length()) {
            sb.append("1234567890");
        }
        sb.append(inputString);

        return sb.toString();
    }

    private void handle(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer readBuffer = ByteBuffer.allocate(13);
        try {
            int n = socketChannel.read(readBuffer);
            if (n == -1) {
                socketChannel.close();
                return;
            }
            if (n == 0) {
                return;
            }
            readBuffer.flip();
            int opcode = readBuffer.get();
            if (opcode == 48) { // symbol '0'
                ByteBuffer img = ByteBuffer.wrap(padLeftOnes("", 10).getBytes(StandardCharsets.UTF_8));
                while (img.hasRemaining()) {
                    socketChannel.write(img);
                }
            } else {
                ByteBuffer str = ByteBuffer.wrap("nop".getBytes());
                while (str.hasRemaining()) {
                    socketChannel.write(str);
                }
            }
        } catch (IOException e) {
            key.cancel();
            socketChannel.close();
        } finally {
            readBuffer.clear();
        }
    }

    @Override
    public void run() {
        while (selector.isOpen()) {
            try {
                selector.select();
                for (SelectionKey key: selector.keys()) {
                    if (!key.isValid()) {
                        continue;
                    }

                    if (key.isReadable()) {
                        this.handle(key);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
