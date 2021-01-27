package org.bench;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Bench3Coroutine {

    private final BufferedImage image;

    public Bench3Coroutine(BufferedImage image) {
        this.image = image;
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

    private void handle(SocketChannel socketChannel) {
        var buf = ByteBuffer.allocate(100);

        try {
            while (socketChannel.finishConnect()) {
                int n = socketChannel.read(buf);
                if (n == 0) {
                    continue;
                }
                buf.flip();
                int opcode = buf.get();
                if (opcode == 48) { // symbol '0'
                    var img = ByteBuffer.wrap(padLeftOnes("", 100).getBytes(StandardCharsets.UTF_8));
                    while (img.hasRemaining()) {
                        socketChannel.write(img);
                    }
                } else {
                    var str = ByteBuffer.wrap("nop".getBytes());
                    while (str.hasRemaining()) {
                        socketChannel.write(str);
                    }
                }
                buf.clear();
            }
        } catch (IOException e) {
            try {
                socketChannel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } finally {

        }
    }

    private void handle(ServerSocketChannel listener) {
        try (listener) {
            while (listener.isOpen()) {
                var client = listener.accept();
                Thread.startVirtualThread(() -> handle(client));
            }
        } catch (ClosedChannelException e) {
            System.out.println("listener closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void task(int port) throws IOException, InterruptedException {
        var backlog = 256;
        var listener = ServerSocketChannel.open()
                .bind(new InetSocketAddress(port), backlog);
        var th = Thread.startVirtualThread(() -> handle(listener));
        th.join();
    }

    public static void main(String[] args) throws IOException {
        var port = Integer.parseInt(args[0]);
        BufferedImage img = ImageIO.read(new File(args[1]));

        try {
            new Bench3Coroutine(img).task(port);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
