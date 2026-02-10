import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadChecker implements PrimeChecker {
    private final int threadCount;

    public ThreadChecker(int threadCount) {
        if (threadCount < 1) {
            throw new IllegalArgumentException("Number of threads must be > 0");
        }
        this.threadCount = threadCount;
    }

    @Override
    public boolean hasNonPrime(int[] array) {
        validate(array);
        AtomicBoolean result = new AtomicBoolean(false);
        Thread[] threads = new Thread[Math.min(threadCount, array.length)];
        int chunkSize = (array.length + threads.length - 1) / threads.length;

        for (int i = 0; i < threads.length; i++) {
            final int start = i * chunkSize;
            final int end = Math.min(start + chunkSize, array.length);

            threads[i] = new Thread(() -> {
                for (int j = start; j < end && !result.get(); j++) {
                    if (isNotPrime(array[j])) {
                        result.set(true);
                    }
                }
            });
            threads[i].start();
        }

        try {
            for (Thread t : threads) t.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return result.get();
    }
}