import java.util.Arrays;

public class StreamChecker implements PrimeChecker {
    @Override
    public boolean hasNonPrime(int[] array) {
        validate(array);
        return Arrays.stream(array).parallel().anyMatch(this::isNotPrime);
    }
}