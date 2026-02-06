import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class PrimeCheckerTest {

    private final PrimeChecker checker = new PrimeChecker();

    @Test
    @DisplayName("Последовательная: массив с составным числом")
    void testSequentialWithNonPrime() {
        int[] arr = {2, 3, 5, 7, 11, 12, 13};
        assertTrue(checker.sequentialCheck(arr), "12 не простое");
    }

    @Test
    @DisplayName("Последовательная: только простые числа")
    void testSequentialAllPrimes() {
        int[] arr = {2, 3, 5, 7, 11, 13, 17, 19};
        assertFalse(checker.sequentialCheck(arr), "все числа простые");
    }

    @Test
    @DisplayName("java.lang.Thread: массив с составным числом")
    void testThreadedWithNonPrime() throws InterruptedException {
        int[] arr = {6, 8, 7, 13, 5, 9, 4};
        assertTrue(checker.threadedCheck(arr, 4), "6 не простое");
    }

    @Test
    @DisplayName("java.lang.Thread: только большие простые числа")
    void testThreadedAllPrimes() throws InterruptedException {
        int[] arr = {20319251, 6997901, 6997927, 6997937, 17858849};
        assertFalse(checker.threadedCheck(arr, 4), "все числа простые");
    }

    @Test
    @DisplayName("parallelStream(): массив с составным числом")
    void testParallelStreamWithNonPrime() {
        int[] arr = {11, 13, 17, 18, 19};
        assertTrue(checker.parallelStreamCheck(arr), "18 не простое");
    }

    @Test
    @DisplayName("Последовательная: проверка краевых случаев (0, 1, отрицательные)")
    void testEdgeCases() {
        int[] arr = {0, 1, -5};
        // isNotPrime(n) < 2 возвращает true (считаем не простыми)
        assertTrue(checker.sequentialCheck(arr));
    }
}