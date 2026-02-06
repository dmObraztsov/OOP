import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class PrimeChecker {

    public static boolean isNotPrime(int n) {
        if (n < 2) return true;
        if (n == 2) return false;
        if (n % 2 == 0) return true;
        for (int i = 3; i <= Math.sqrt(n); i += 2) {
            if (n % i == 0) return true;
        }
        return false;
    }

    // 1. Последовательное
    public boolean sequentialCheck(int[] array) {
        for (int num : array) {
            if (isNotPrime(num)) return true;
        }
        return false;
    }

    // 2. java.lang.Thread
    public boolean threadedCheck(int[] array, int threadsCount) throws InterruptedException {
        AtomicBoolean hasNonPrime = new AtomicBoolean(false);
        Thread[] threads = new Thread[threadsCount];

        int chunkSize = (array.length + threadsCount - 1) / threadsCount;

        for (int i = 0; i < threadsCount; i++) {
            final int start = i * chunkSize;
            final int end = Math.min(start + chunkSize, array.length);

            threads[i] = new Thread(() -> {
                for (int j = start; j < end; j++) {
                    if (hasNonPrime.get()) return; // Досрочный выход
                    if (isNotPrime(array[j])) {
                        hasNonPrime.set(true);
                        return;
                    }
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
        return hasNonPrime.get();
    }

    // 3. parallelStream()
    public boolean parallelStreamCheck(int[] array) {
        return Arrays.stream(array).parallel().anyMatch(PrimeChecker::isNotPrime);
    }
}