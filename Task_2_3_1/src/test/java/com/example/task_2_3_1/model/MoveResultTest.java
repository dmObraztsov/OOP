package com.example.task_2_3_1.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MoveResultTest {
    @Test
    void testMoveResultData() {
        GameModel model = new GameModel(10, 10, 10, 0);
        Point tailBefore = model.getSnake().getBody().getLast();

        MoveResult result = model.update();

        assertNotNull(result);
        assertEquals(tailBefore, result.oldTail(), "Должен содержать старую позицию хвоста для его затирания");
        assertEquals(model.getSnake().getHead(), result.newHead(), "Должен содержать новую позицию головы");
    }
}