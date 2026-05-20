package core.logic;

import core.model.Task;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class Semester1StrategyTest {

    @Test
    void testCalculateTaskScore_OnTime() {
        // given
        Semester1Strategy strategy = new Semester1Strategy();
        Task task = new Task(
                "Task_1_1",
                "Intro",
                10.0,
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 15)
        );

        LocalDate firstCommit = LocalDate.of(2026, 9, 20);
        LocalDate lastCommit = LocalDate.of(2026, 9, 25);
        boolean hasBonus = false;

        // when
        double score = strategy.calculateTaskScore(task, firstCommit, lastCommit, hasBonus);

        // then
        assertEquals(10.0, score);
    }

    @Test
    void testCalculateTaskScore_LateSubmission() {
        // given
        Semester1Strategy strategy = new Semester1Strategy();
        Task task = new Task(
                "Task_1_1",
                "Intro",
                10.0,
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 15)
        );

        LocalDate firstCommit = LocalDate.of(2026, 10, 5);
        LocalDate lastCommit = LocalDate.of(2026, 10, 10);
        boolean hasBonus = false;

        // when
        double score = strategy.calculateTaskScore(task, firstCommit, lastCommit, hasBonus);

        // then
        assertTrue(score < 10.0);
        assertEquals(9.5, score);
    }
}