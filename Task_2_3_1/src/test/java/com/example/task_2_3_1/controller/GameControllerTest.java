package com.example.task_2_3_1.controller;

import com.example.task_2_3_1.model.*;
import com.example.task_2_3_1.view.GameRenderer;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class GameControllerTest {
    private GameController controller;
    private GameModel model;

    @BeforeAll
    static void initJavaFX() {
        try { Platform.startup(() -> {}); } catch (Exception ignored) {}
    }

    @BeforeEach
    void setUp() throws Exception {
        controller = new GameController();
        model = new GameModel(10, 10, 10, 1);
        Canvas canvas = new Canvas(200, 200);
        GameRenderer renderer = new GameRenderer(canvas.getGraphicsContext2D());

        setField(controller, "canvas", canvas);
        setField(controller, "model", model);
        setField(controller, "renderer", renderer);
    }

    private void setField(Object obj, String name, Object value) throws Exception {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(obj, value);
    }

    private void pressKey(KeyCode code) {
        KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", code, false, false, false, false);
        controller.handleKeyPress(event);
    }

    @Test
    void testDirectionChangesAfterUpdate() {
        assertFalse(model.isGameOver(), "Игра должна быть активна");
        pressKey(KeyCode.DOWN);
        model.update();
        assertEquals(Direction.DOWN, model.getSnake().getDirection(),
                "Змейка должна изменить направление на DOWN после нажатия и шага");
    }

    @Test
    void testDirection180FromRightNotChangesAfterUpdate() { // нельзя поворачивать на 180 right->left
        assertFalse(model.isGameOver(), "Игра должна быть активна");
        pressKey(KeyCode.LEFT);
        model.update();
        assertEquals(Direction.RIGHT, model.getSnake().getDirection(),
                "Змейка не должна изменить направление на LEFT после нажатия при движении RIGHT");
    }

    @Test
    void testDirection180FromUpNotChangesAfterUpdate() { // нельзя поворачивать на 180 up->down
        assertFalse(model.isGameOver(), "Игра должна быть активна");
        pressKey(KeyCode.UP);
        model.update();
        pressKey(KeyCode.DOWN);
        model.update();
        assertEquals(Direction.UP, model.getSnake().getDirection(),
                "Змейка не должна изменить направление на DOWN после нажатия при движении UP");
    }

    @Test
    void testResetKey() {
        model.update();
        pressKey(KeyCode.R);

        assertEquals(1, model.getSnake().getBody().size());
    }

    @Test
    void testHandleKeyPressWhenGameOver() throws Exception {
        Field field = model.getClass().getDeclaredField("gameOver");
        field.setAccessible(true);
        field.set(model, true);

        pressKey(KeyCode.UP);

        assertNotEquals(Direction.UP, model.getSnake().getDirection());
    }

    @Test
    void testSnakeCollidesWithSelf() throws Exception { // смерть об себя
        model = new GameModel(50, 50, 10, 1);

        setField(controller, "model", model);

        setField(controller, "renderer", new com.example.task_2_3_1.view.GameRenderer(null));

        Snake snake = model.getSnake();

        for (int i = 0; i < 3; i++) {
            snake.grow();
            model.update();
        }

        model.update();

        assertFalse(model.isGameOver(), "Игра должна быть активна");

        assertEquals(Direction.RIGHT, snake.getDirection(), "not RIGHT");

        pressKey(KeyCode.DOWN);
        model.update();
        assertEquals(Direction.DOWN, snake.getDirection(), "not DOWN");


        pressKey(KeyCode.LEFT);
        model.update();
        assertEquals(Direction.LEFT, snake.getDirection(), "not LEFT");


        pressKey(KeyCode.UP);
        model.update();
        assertEquals(Direction.UP, snake.getDirection(), "not UP");


        assertTrue(model.isGameOver(), "Змейка должна была врезаться в себя");
    }

    @Test
    void testInitGameMethod() throws Exception {
        Method initMethod = GameController.class.getDeclaredMethod("initGame");
        initMethod.setAccessible(true);
        initMethod.invoke(controller);

        Field modelField = GameController.class.getDeclaredField("model");
        modelField.setAccessible(true);
        assertNotNull(modelField.get(controller));
    }
}