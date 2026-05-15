package core.logic;

import core.model.Task;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class Semester2StrategyTest {

    @Test
    void testCalculateTaskScore_S2_OnTime() {
        Semester2Strategy strategy = new Semester2Strategy();

        Task task = new Task(
                "Task_2_1",
                "Advanced Java",
                10.0,
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 3, 15)
        );

        double score = strategy.calculateTaskScore(
                task,
                LocalDate.of(2026, 2, 20),
                LocalDate.of(2026, 2, 25),
                false
        );

        assertEquals(10.0, score, "Во втором семестре за сдачу вовремя должен быть полный балл");
    }

    @Test
    void testCalculateTaskScore_S2_AfterHardDeadline() {
        Semester2Strategy strategy = new Semester2Strategy();

        Task task = new Task(
                "Task_2_1",
                "Advanced Java",
                10.0,
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 3, 15)
        );

        double score = strategy.calculateTaskScore(
                task,
                LocalDate.of(2026, 3, 10),
                LocalDate.of(2026, 3, 20),
                false
        );

        assertEquals(0.0, score, "После жесткого дедлайна во втором семестре баллы не начисляются");
    }

    @Test
    void testMapTotalToGrade_S2_Excellence() {
        Semester2Strategy strategy = new Semester2Strategy();

        String grade = strategy.mapTotalToGrade(18.0, 1.0, true);

        assertEquals("отлично", grade.toLowerCase(), "18 баллов должны давать 'отлично'");
    }
}