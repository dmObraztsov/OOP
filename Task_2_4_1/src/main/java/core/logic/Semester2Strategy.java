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
    public String mapTotalToGrade(double total, double activityScore, boolean isTask1_2_Done) {
        double limitScore = total;
        if (activityScore < 0.4) {
            limitScore = Math.min(total, 3.5);
        }

        if (limitScore >= 5 && isTask1_2_Done) return "отлично";
        if (limitScore >= 4) return "хорошо";
        if (limitScore >= 3) return "удовлетворительно";
        return "неудовлетворительно";
    }
}