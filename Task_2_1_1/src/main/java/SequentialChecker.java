public class SequentialChecker implements PrimeChecker {
    @Override
    public boolean hasNonPrime(int[] array) {
        validate(array);
        for (int num : array) {
            if (isNotPrime(num)) return true;
        }
        return false;
    }
}
