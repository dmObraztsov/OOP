package core.logic;

import core.model.Task;

import java.time.LocalDate;

public interface GradingStrategy {
    double calculateTaskScore(Task task, LocalDate softSubmitDate, LocalDate hardApproveDate, boolean bonusCondition);

    String mapTotalToGrade(double totalScore, double activityScore, boolean specialCondition);
}