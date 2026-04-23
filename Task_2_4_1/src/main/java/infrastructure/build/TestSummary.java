package infrastructure.build;

public record TestSummary(int total, int failed, int skipped) {
    public int passed() {
        return total - failed - skipped;
    }
}