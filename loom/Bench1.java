import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Bench1 {
    static private Thread[] threadPool = null;
    static private final int NUM_HELLO = 1000000;
    static private final String message = "hello ";

    public static void task(int numCoroutines, AsynchronousFileChannel channel) throws InterruptedException {
        AtomicInteger inc = new AtomicInteger();
        AtomicLong startPos = new AtomicLong(0L);
        for (int i = 0; i < numCoroutines; i++) {
            threadPool[i] = Thread.startVirtualThread(() -> {
                while (inc.get() < NUM_HELLO) {
                    inc.addAndGet(1);
                    var pos = startPos.getAndAdd(message.length());
                    write(channel, pos, ByteBuffer.wrap(message.getBytes()));
                }
            });
        }

        for (Thread th : threadPool) {
            th.join();
        }
    }

    private static void write(AsynchronousFileChannel ch, long pos, ByteBuffer buf) {
        ch.write(buf, pos, "nothing", new CompletionHandler<>() {
            @Override
            public void completed(Integer result, String attachment) {
                //Todo
            }

            @Override
            public void failed(Throwable e, String attachment) {
                assert false;
            }
        });
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        var numCoroutines = Integer.parseInt(args[0]);
        var path = Path.of(args[1]);

        var ch = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        threadPool = new Thread[numCoroutines];

        long startTime = System.currentTimeMillis();
        task(numCoroutines, ch);
        long endTime = System.currentTimeMillis();

        System.out.println("Execution time: " + (endTime - startTime) + "ms.");
    }
}