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
    
    private static void warmUp(int numCoro) {
        threadPool = new Thread[numCoro];

        for (int i = 0; i < numCoro; i++) {
            threadPool[i] = Thread.startVirtualThread(() -> task(countPerCoro));
        }

        task(countPerCoro);

        for (Thread th: threadPool) {
            th.join();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final int numCoroutines = Integer.parseInt(args[0]);
        final long countPerCoro = COUNT / numCoroutines;
        System.setProperty("jdk.defaultScheduler.parallelism", "1");
        
        warmUp(5_000_000);

        threadPool = new Thread[numCoroutines];
        System.gc();

        long startTime = System.nanoTime();

        for (int i = 0; i < numCoroutines; i++) {
            threadPool[i] = Thread.startVirtualThread(() -> task(countPerCoro));
        }

        task(countPerCoro);

        for (Thread th: threadPool) {
            th.join();
        }

        long endTime = System.nanoTime();

        long duration = (endTime - startTime) / 1000000;
        System.out.println( numCoroutines + " coroutines: " +  COUNT + " switches in " + duration + " ms, " + (1000 * COUNT) / duration +" switches per second\n");
    }
}
