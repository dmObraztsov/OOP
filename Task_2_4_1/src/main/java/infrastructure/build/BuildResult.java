package infrastructure.build;

public record BuildResult(
        boolean compileSuccess,
        boolean styleSuccess,
        int testsPassed,
        int testsFailed,
        int testsSkipped
) {
}