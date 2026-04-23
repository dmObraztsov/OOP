package core.model;

import java.util.Map;

public record StudentResult(
        Student student,
        Map<String, Double> taskScores, // taskId -> score
        double totalScore,
        String finalGrade
) {}