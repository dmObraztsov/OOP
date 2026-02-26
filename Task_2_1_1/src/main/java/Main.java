import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        int[] largePrimes = new int[5000];
        Arrays.fill(largePrimes, 20319251);

        System.out.println("--- Start ---");

        PrimeChecker sequential = new SequentialChecker();
        long start = System.currentTimeMillis();
        boolean res1 = sequential.hasNonPrime(largePrimes);
        System.out.println("Sequential: " + (System.currentTimeMillis() - start) + " ms, " + res1);

        int cores = Runtime.getRuntime().availableProcessors();
        for (int i = 1; i <= cores; i++) {
            PrimeChecker threaded = new ThreadChecker(i);
            start = System.currentTimeMillis();
            boolean res2 = threaded.hasNonPrime(largePrimes);
            System.out.println("Threads (" + i + "): " + (System.currentTimeMillis() - start) + " ms, " + res2);
        }

        PrimeChecker stream = new StreamChecker();
        start = System.currentTimeMillis();
        boolean res3 = stream.hasNonPrime(largePrimes);
        System.out.println("Parallel Stream: " + (System.currentTimeMillis() - start) + " ms, " + res3);
    }
}
