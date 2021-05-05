import java.util.concurrent.atomic.AtomicLong;

public class Bench0 {
    private static Thread[] threadPool = null;

    public static void task(int numCoroutines) throws InterruptedException {
        var c = new AtomicLong();

        for (int i = 0; i < numCoroutines; i++) {
            threadPool[i] = Thread.startVirtualThread(c::incrementAndGet);
        }

		for (Thread th: threadPool) {
			th.join();
		}
    }

    public static void main(String[] args) throws InterruptedException {
        int numCoroutines = Integer.parseInt(args[0]);
		threadPool = new Thread[5_000_000];
		
		task(5_000_000);
        
		threadPool = new Thread[numCoroutines];
        long startTime = System.nanoTime();
        
        task(numCoroutines);
        long endTime = System.nanoTime();

        long duration = (endTime - startTime) / 1000000;
        
        System.out.println("Execution time: " + duration + " ms.");
    }
}
