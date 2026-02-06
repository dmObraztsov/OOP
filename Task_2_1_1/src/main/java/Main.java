import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        PrimeChecker checker = new PrimeChecker();

        // 5000 больших простых чисел
        int[] largePrimes = new int[5000];
        Arrays.fill(largePrimes, 20319251);

        System.out.println("--- Start ---");

        // 1. Последовательное
        long start = System.currentTimeMillis();
        checker.sequentialCheck(largePrimes);
        System.out.println("Sequential: " + (System.currentTimeMillis() - start) + " ms");

        // 2. java.lang.Thread
        int cores = Runtime.getRuntime().availableProcessors();
        for (int i = 1; i <= cores; i++) {
            start = System.currentTimeMillis();
            checker.threadedCheck(largePrimes, i);
            System.out.println("Threads (" + i + "): " + (System.currentTimeMillis() - start) + " ms");
        }

        // 3. parallelStream()
        start = System.currentTimeMillis();
        checker.parallelStreamCheck(largePrimes);
        System.out.println("Parallel Stream: " + (System.currentTimeMillis() - start) + " ms");
    }
}