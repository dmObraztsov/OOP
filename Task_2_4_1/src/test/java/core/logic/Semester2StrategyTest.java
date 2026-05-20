package core.logic;

import core.model.Task;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class Semester2StrategyTest {

    @Test
    void testCalculateTaskScore_S2_OnTime() {
        // given
        Semester2Strategy strategy = new Semester2Strategy();
        Task task = new Task(
                "Task_2_1",
                "Advanced Java",
                10.0,
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 3, 15)
        );

        LocalDate firstCommit = LocalDate.of(2026, 2, 20);
        LocalDate lastCommit = LocalDate.of(2026, 2, 25);
        boolean hasBonus = false;

        // when
        double score = strategy.calculateTaskScore(task, firstCommit, lastCommit, hasBonus);

        // then
        assertEquals(10.0, score, "Во втором семестре за сдачу вовремя должен быть полный балл");
    }

    @Test
    void testCalculateTaskScore_S2_AfterHardDeadline() {
        // given
        Semester2Strategy strategy = new Semester2Strategy();
        Task task = new Task(
                "Task_2_1",
                "Advanced Java",
                10.0,
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 3, 15)
        );

        LocalDate firstCommit = LocalDate.of(2026, 3, 10);
        LocalDate lastCommit = LocalDate.of(2026, 3, 20);
        boolean hasBonus = false;

        // when
        double score = strategy.calculateTaskScore(task, firstCommit, lastCommit, hasBonus);

        // then
        assertEquals(0.0, score, "После жесткого дедлайна во втором семестре баллы не начисляются");
    }

    @Test
    void testMapTotalToGrade_S2_Excellence() {
        // given
        Semester2Strategy strategy = new Semester2Strategy();
        double totalScore = 18.0;
        double activity = 1.0;
        boolean examPassed = true;

        // when
        String grade = strategy.mapTotalToGrade(totalScore, activity, examPassed);

        // then
        assertEquals("отлично", grade.toLowerCase(), "18 баллов при полной активности должны давать 'отлично'");
    }
}