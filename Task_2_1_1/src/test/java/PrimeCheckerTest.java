import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PrimeCheckerTest {

    static Stream<Arguments> checkerProvider() {
        return Stream.of(
                Arguments.of(new SequentialChecker(), "Sequential"),
                Arguments.of(new ThreadChecker(1), "Threads (1)"),
                Arguments.of(new ThreadChecker(4), "Threads (4)"),
                Arguments.of(new StreamChecker(), "Parallel Stream")
        );
    }

    @ParameterizedTest()
    @MethodSource("checkerProvider")
    void testHasNonPrimeTrue(PrimeChecker checker, String name) {
        int[] input = {6, 8, 7, 13, 5, 9, 4};
        assertTrue(checker.hasNonPrime(input), "Должен найти составное число в " + name);
    }

    @ParameterizedTest()
    @MethodSource("checkerProvider")
    void testHasNonPrimeFalse(PrimeChecker checker, String name) {
        int[] input = {20319251, 6997901, 6997927, 6997937, 17858849};
        assertFalse(checker.hasNonPrime(input), "Не должен найти составных чисел в " + name);
    }

    @ParameterizedTest()
    @MethodSource("checkerProvider")
    void testEmptyArrayValidation(PrimeChecker checker, String name) {
        int[] empty = {};
        assertThrows(IllegalArgumentException.class, () -> checker.hasNonPrime(empty),
                "Должен выбросить исключение на пустой массив в " + name);
    }

    @ParameterizedTest()
    @MethodSource("checkerProvider")
    void testNullValidation(PrimeChecker checker, String name) {
        assertThrows(IllegalArgumentException.class, () -> checker.hasNonPrime(null),
                "Должен выбросить исключение на null в " + name);
    }

    @ParameterizedTest()
    @MethodSource("checkerProvider")
    void testEdgeCases(PrimeChecker checker, String name) {
        int[] input = {2, 3, 1}; // 1 не является простым
        assertTrue(checker.hasNonPrime(input), "1 считается составным/не простым числом в " + name);

        int[] negative = {-5, 0};
        assertTrue(checker.hasNonPrime(negative), "Числа <= 1 не простые в " + name);
    }
}