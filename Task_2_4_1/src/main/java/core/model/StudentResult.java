package core.model;

import java.util.Map;

public record StudentResult(
        Student student,
        Map<String, TaskResult> part1Results,
        Map<String, TaskResult> part2Results,
        double totalPart1,
        double totalPart2,
        String gradePart1,
        String gradePart2,
        double activityPart1,
        double activityPart2
) {
}