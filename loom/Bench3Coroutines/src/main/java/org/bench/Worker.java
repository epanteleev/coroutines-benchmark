package org.bench;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;

public class Worker implements Runnable {
    private Selector selector;
    private final ByteBuffer readBuffer = ByteBuffer.allocate(13);
    private BufferedImage img;

    public Worker(BufferedImage img) {
        this.img = img;
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

        selector.wakeup();
        socketChannel.register(this.selector, SelectionKey.OP_READ);
    }

    private static ByteBuffer convertImageData(BufferedImage bi) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(bi, "png", out);
            return ByteBuffer.wrap(out.toByteArray());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        try {
            int n = socketChannel.read(this.readBuffer);
            readBuffer.flip();
            int opcode = readBuffer.get();
            //System.out.println(opcode + "|");
            if (opcode == 48) {
                socketChannel.write(convertImageData(this.img));
            } else {
                socketChannel.write(ByteBuffer.wrap("nop".getBytes()));
            }
            //socketChannel.write(readBuffer);
        } catch (IOException e) {
            // The remote forcibly closed the connection, cancel
            // the selection key and close the channel.
            key.cancel();
            socketChannel.close();
        } finally {
            this.readBuffer.clear();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                this.selector.select();
                // Iterate over the set of keys for which events are available
                Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    SelectionKey key = selectedKeys.next();
                    selectedKeys.remove();

                    if (!key.isValid()) {
                        continue;
                    }

                    if (key.isReadable()) {
                        this.read(key);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
