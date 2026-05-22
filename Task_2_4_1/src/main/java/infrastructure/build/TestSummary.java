package infrastructure.build;

public record TestSummary(int total, int failed, int skipped) {
    public int passed() {
        return Math.max(0, total - failed - skipped);
    }

    @Override
    public String toString() {
        return String.format("Tests: %d/%d passed (failed: %d, skipped: %d)",
                passed(), total, failed, skipped);
    }
}