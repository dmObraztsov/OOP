package core.model;

import infrastructure.build.TestSummary;

public record TaskResult(
        boolean compileSuccess,
        boolean styleSuccess,
        TestSummary tests,
        double coverage,
        double score
) {
    public static TaskResult failed() {
        return new TaskResult(false, false, new TestSummary(0, 0, 0), 0.0, 0.0);
    }
}