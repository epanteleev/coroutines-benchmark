import javax.imageio.metadata.IIOMetadataNode;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Bench2 {

    void handle(SocketChannel channel) {
        try {
            while (channel.finishConnect()) {
                var buf = ByteBuffer.allocate(100);
                var n = channel.read(buf);
                if (n != -1) {
                    buf.flip();
                    while (buf.hasRemaining()) {
                        channel.write(buf);
                    }
                    buf.clear();
                } else {
                    return;
                }
            }
        } catch (IOException ignored) {

        }
    }

    void handle(ServerSocketChannel listener) {
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

    private void exchangeGreeting(SocketAddress address)  {
        try {
            var sock = SocketChannel.open();
            sock.connect(address);
            sock.write(ByteBuffer.wrap("Hello".getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void task(int port) throws IOException, InterruptedException {
        var backlog = 256;
        var listener = ServerSocketChannel.open()
                .bind(new InetSocketAddress(port), backlog);
        var th = Thread.startVirtualThread(() -> handle(listener));
        th.join();
    }

    public static void main(String[] args) {
        var port = Integer.parseInt(args[0]);
        try {
            new Bench2().task(port);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
