package com.example.task_2_3_1.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SnakeTest {
    private Snake snake;

    @BeforeEach
    void setUp() {
        snake = new Snake(new Point(5, 5));
    }

    @Test
    void testInitialState() {
        assertEquals(1, snake.getBody().size(), "Змейка должна начинаться с 1 сегмента");
        assertEquals(new Point(5, 5), snake.getHead());
    }

    @Test
    void testMovement() {
        snake.setDirection(Direction.RIGHT);
        snake.move();
        assertEquals(new Point(6, 5), snake.getHead(), "Змейка должна переместиться вправо");
        assertEquals(1, snake.getBody().size(), "Размер при обычном движении не меняется");
    }

    @Test
    void testGrowth() {
        snake.setDirection(Direction.DOWN);
        snake.grow();
        assertEquals(2, snake.getBody().size(), "Размер должен увеличиться после grow()");
        assertEquals(new Point(5, 6), snake.getHead());
        assertEquals(new Point(5, 5), snake.getBody().getLast(), "Старая голова должна стать хвостом");
    }
}