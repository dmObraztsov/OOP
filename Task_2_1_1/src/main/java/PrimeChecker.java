public interface PrimeChecker {
    boolean hasNonPrime(int[] array);

    default boolean isNotPrime(int n) {
        if (n < 2) return true;
        if (n == 2) return false;
        if (n % 2 == 0) return true;
        for (int i = 3; i <= Math.sqrt(n); i += 2) {
            if (n % i == 0) return true;
        }
        return false;
    }

    default void validate(int[] array) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("Array must not be empty");
        }
    }
}