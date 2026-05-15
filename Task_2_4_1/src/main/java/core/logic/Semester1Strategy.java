package core.logic;

import core.model.Task;

import java.time.LocalDate;

public class Semester1Strategy implements GradingStrategy {
    @Override
    public double calculateTaskScore(Task task, LocalDate softSubmit, LocalDate hardApprove, boolean hasBonus) {
        if (softSubmit == null || hardApprove == null) return 0.0;

        double score = task.maxScore();
        boolean metSoft = !softSubmit.isAfter(task.softDeadline());
        boolean metHard = !hardApprove.isAfter(task.hardDeadline());

        if (metSoft && metHard) {
        } else if (metSoft || metHard) {
            score -= 0.5;
        } else {
            return 0.0;
        }

        if (hasBonus) score += 1.0;
        return Math.max(0, score);
    }

    @Override
    public String mapTotalToGrade(double total, double activityScore, boolean ignored) {
        double finalScore = (activityScore < 0.5) ? total * 0.8 : total;

        if (finalScore >= 13) return "отлично";
        if (finalScore >= 10) return "хорошо";
        if (finalScore >= 7) return "удовлетворительно";
        return "неудовлетворительно";
    }
}