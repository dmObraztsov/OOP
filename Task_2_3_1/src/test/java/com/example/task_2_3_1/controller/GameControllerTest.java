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

    @Test
    void testDirectionChangesAfterUpdate() {
        assertFalse(model.isGameOver(), "Игра должна быть активна");
        KeyEvent downEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN, false, false, false, false);
        controller.handleKeyPress(downEvent);
        model.update();
        assertEquals(Direction.DOWN, model.getSnake().getDirection(),
                "Змейка должна изменить направление на DOWN после нажатия и шага");
    }

    @Test
    void testResetKey() {
        model.update();
        KeyEvent rEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.R, false, false, false, false);
        controller.handleKeyPress(rEvent);

        assertEquals(1, model.getSnake().getBody().size());
    }

    @Test
    void testHandleKeyPressWhenGameOver() throws Exception {
        Field field = model.getClass().getDeclaredField("gameOver");
        field.setAccessible(true);
        field.set(model, true);

        KeyEvent upEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.UP, false, false, false, false);
        controller.handleKeyPress(upEvent);

        assertNotEquals(Direction.UP, model.getSnake().getDirection());
    }

    @Test
    void testHandleExitKey() {
        KeyEvent exitEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.E, false, false, false, false);
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