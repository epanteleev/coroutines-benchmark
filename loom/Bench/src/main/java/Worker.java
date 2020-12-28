import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

public class Worker implements Runnable {
    private Selector selector;
    private final ByteBuffer readBuffer = ByteBuffer.allocate(13);

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

        selector.wakeup();
        socketChannel.register(this.selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        this.readBuffer.clear();

        try {
            socketChannel.read(this.readBuffer);
            readBuffer.flip();
            socketChannel.write(this.readBuffer);

        } catch (IOException e) {
            // The remote forcibly closed the connection, cancel
            // the selection key and close the channel.
            key.cancel();
            socketChannel.close();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                this.selector.select();
                // Iterate over the set of keys for which events are available
                Iterator selectedKeys = this.selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    SelectionKey key = (SelectionKey) selectedKeys.next();
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
