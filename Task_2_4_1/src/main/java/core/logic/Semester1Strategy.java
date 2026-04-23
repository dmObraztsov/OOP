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
            // Оставляем полный балл
        } else if (metSoft || metHard) {
            score -= 0.5;
        } else {
            return 0.0;
        }

        if (hasBonus) score += 1.0;
        return Math.max(0, score);
    }

    @Override
    public String mapTotalToGrade(double total, boolean ignored) {
        if (total >= 13) return "отлично";
        if (total >= 10) return "хорошо";
        if (total >= 7) return "удовлетворительно";
        return "неудовлетворительно";
    }
}