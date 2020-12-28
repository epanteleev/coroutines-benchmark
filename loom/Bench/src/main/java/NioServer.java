import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;

public class NioServer implements Runnable {

    // The selector we'll be monitoring
    private final Selector selector;

    public NioServer(InetAddress hostAddress, int port) throws IOException {
        this.selector = SelectorProvider.provider().openSelector();

        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);

        InetSocketAddress isa = new InetSocketAddress(hostAddress, port);
        serverChannel.socket().bind(isa);

        serverChannel.register(this.selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public void run() {
        TaskPool pool = new TaskPool(5);
        pool.start();

        while (true) {
            try {
                // Wait for an event one of the registered channels
                this.selector.select();
                // Iterate over the set of keys for which events are available
                Iterator<SelectionKey> sKey = this.selector.selectedKeys().iterator();
                while (sKey.hasNext()) {
                    SelectionKey key = sKey.next();
                    sKey.remove();
                    if (!key.isValid()) {
                        continue;
                    }

                    if (key.isAcceptable()) {
                        pool.dispatch().accept(key);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            new Thread(new NioServer(null, 8888))
                    .start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}