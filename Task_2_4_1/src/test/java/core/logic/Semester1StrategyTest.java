package core.logic;

import core.model.Task;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class Semester1StrategyTest {

    @Test
    void testCalculateTaskScore_OnTime() {
        Semester1Strategy strategy = new Semester1Strategy();

        Task task = new Task(
                "Task_1_1",
                "Intro",
                10.0,
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 15)
        );

        double score = strategy.calculateTaskScore(
                task,
                LocalDate.of(2026, 9, 20),
                LocalDate.of(2026, 9, 25),
                false
        );

        assertEquals(10.0, score, "Если сдали до дедлайна, должен быть полный балл");
    }

    @Test
    void testCalculateTaskScore_LateSubmission() {
        Semester1Strategy strategy = new Semester1Strategy();

        Task task = new Task(
                "Task_1_1",
                "Intro",
                10.0,
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 15)
        );

        double score = strategy.calculateTaskScore(
                task,
                LocalDate.of(2026, 10, 5),
                LocalDate.of(2026, 10, 10),
                false
        );

        assertTrue(score < 10.0, "За позднюю сдачу должен быть штраф");
    }
}