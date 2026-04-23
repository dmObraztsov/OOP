package com.example.task_2_3_1.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameModelTest {
    private GameModel model;

    @BeforeEach
    void setUp() {
        model = new GameModel(10, 10, 10, 3);
    }

    @Test
    void testDirect180TurnBlocked() {
        model.setDirection(Direction.LEFT);
        model.update();

        assertEquals(Direction.RIGHT, model.getSnake().getDirection(), "Прямой разворот на 180 должен быть заблокирован");
        assertEquals(new Point(6, 5), model.getSnake().getHead());
    }

    @Test
    void testQuickDoubleTurnBlocked() {
        model.setDirection(Direction.UP);
        model.setDirection(Direction.LEFT); // Должно быть заблокировано флагом directionChangedThisTick

        model.update();

        assertEquals(Direction.UP, model.getSnake().getDirection(), "Второй поворот за один тик должен игнорироваться");
        assertEquals(new Point(5, 4), model.getSnake().getHead());
    }

    @Test
    void testFullCycleWallCollision() {
        for (int i = 0; i < 4; i++) {
            model.update();
        }
        assertFalse(model.isGameOver());

        model.update();
        assertTrue(model.isGameOver(), "Игра должна закончиться при столкновении со стеной");
    }

    @Test
    void testWinCondition() {
        GameModel smallGoalModel = new GameModel(10, 10, 2, 0);

        Point head = smallGoalModel.getSnake().getHead();
        Point foodPos = new Point(head.x() + 1, head.y());
        smallGoalModel.getFood().add(foodPos);

        smallGoalModel.update(); // Съедаем еду
        assertTrue(smallGoalModel.isGameWon(), "Игра должна быть выиграна при достижении целевой длины");
    }
}