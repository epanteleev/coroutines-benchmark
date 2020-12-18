import javax.imageio.metadata.IIOMetadataNode;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Bench2Threads {

    private void handle(SocketChannel channel) {
        var buf = ByteBuffer.allocate(100);
        try {
            while (channel.finishConnect()) {
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
        }
		catch (SocketException ignore) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handle(ServerSocketChannel listener) {
        try (listener) {
            while (listener.isOpen()) {
                var client = listener.accept();
                new Thread(() -> handle(client)).start();
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
        var th = new Thread(() -> handle(listener));
        th.start();
        th.join();
    }

    public static void main(String[] args) {
        var port = Integer.parseInt(args[0]);
        try {
            new Bench2Threads().task(port);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
