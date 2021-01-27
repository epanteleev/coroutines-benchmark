package org.bench;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;

public class NioServer {

    // The selector we'll be monitoring
    private final Selector selector;
    private final TaskPool pool;
    ServerSocketChannel serverChannel;

    public NioServer(InetAddress hostAddress, int port, BufferedImage img) throws IOException {
        this.pool = new TaskPool(300);
        this.selector = SelectorProvider.provider().openSelector();

        this.serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);

        serverChannel.socket().bind(new InetSocketAddress(hostAddress, port), 256);
        serverChannel.register(this.selector, SelectionKey.OP_ACCEPT);
    }

    public void run() throws IOException {
        try {
            while (selector.isOpen()) {
                selector.select();
                for (SelectionKey key: this.selector.keys()) {
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isAcceptable()) {
                        pool.dispatch().accept(key);
                    }
                }
                selector.selectNow();
                serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            selector.close();
        }
    }

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(args[0]);

        BufferedImage img = ImageIO.read(new File(args[1]));

        new NioServer(null, port, img).run();
    }
}