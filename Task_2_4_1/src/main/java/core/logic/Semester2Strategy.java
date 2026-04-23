package core.logic;

import core.model.Task;

import java.time.LocalDate;

public class Semester2Strategy implements GradingStrategy {
    @Override
    public double calculateTaskScore(Task task, LocalDate softSubmit, LocalDate hardApprove, boolean hasBonus) {
        if (softSubmit == null || hardApprove == null) return 0.0;

        double score = task.maxScore();
        boolean metSoft = !softSubmit.isAfter(task.softDeadline());
        boolean metHard = !hardApprove.isAfter(task.hardDeadline());

        if (!metSoft && !metHard) return 0.0;
        if (!metSoft || !metHard) score -= 0.5;

        return Math.max(0, score);
    }

    @Override
    public String mapTotalToGrade(double total, boolean isTask1_2_Done) {
        if (total >= 5 && isTask1_2_Done) return "отлично";
        if (total >= 4) return "хорошо";
        if (total >= 3) return "удовлетворительно";
        return "неудовлетворительно";
    }
}