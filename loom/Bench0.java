import java.util.concurrent.atomic.AtomicLong;

public class Main {

    public static void task(int numCoroutines) {
        var c = new AtomicLong();

        for (int i = 0; i < numCoroutines; i++) {
            Thread.startVirtualThread(c::incrementAndGet);
        }
    }

    public static void main(String[] args) {
        int numCoroutines = Integer.parseInt(args[0]);

        long startTime = System.currentTimeMillis();
        task(numCoroutines);
        long endTime = System.currentTimeMillis();

        System.out.println("Execution time: " + (endTime - startTime) + "ms.");
    }
}