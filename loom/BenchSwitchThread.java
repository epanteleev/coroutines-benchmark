import java.util.concurrent.atomic.AtomicLong;

public class BenchSwitch {

    private static final long COUNT = 100_000_000;

    private static Thread[] threadPool = null;

    public static void task(long count) {
        long i = 0;
        while (true) {
            i += 1;
            if (i > count) {
                break;
            }
            Thread.yield();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final int numCoroutines = Integer.parseInt(args[0]);
        final long countPerCoro = COUNT / numCoroutines;

        threadPool = new Thread[numCoroutines];
        long startTime = System.nanoTime();

        for (int i = 0; i < numCoroutines; i++) {
            threadPool[i] = new Thread(() -> task(countPerCoro));
        }

        for (Thread th: threadPool) {
            th.start();
        }

        task(countPerCoro);

        for (Thread th: threadPool) {
            th.join();
        }

        long endTime = System.nanoTime();

        long duration = (endTime - startTime) / 1000000;
        System.out.println( numCoroutines + " coroutines: " +  COUNT + " switches in " + duration + " ms," + (1000 * COUNT) / duration +" switches per second\n");
    }
}
