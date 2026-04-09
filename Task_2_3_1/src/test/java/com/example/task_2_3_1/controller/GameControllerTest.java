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

import static org.junit.jupiter.api.Assertions.*;

public class GameControllerTest {
    private GameController controller;
    private GameModel model;

    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (Exception ignored) {}
    }

    @BeforeEach
    void setUp() throws Exception {
        controller = new GameController();
        model = new GameModel(10, 10, 10, 0);

        Canvas canvas = new Canvas(100, 100);

        setField(controller, "canvas", canvas);
        setField(controller, "model", model);

        GameRenderer renderer = new GameRenderer(canvas.getGraphicsContext2D());
        setField(controller, "renderer", renderer);
    }

    private void setField(Object obj, String name, Object value) throws Exception {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(obj, value);
    }

    @Test
    void testDirectionInput() throws IllegalAccessException, NoSuchFieldException {
        KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN, false, false, false, false);
        controller.handleKeyPress(event);

        Field dirField = GameModel.class.getDeclaredField("direction");
        dirField.setAccessible(true);
        assertEquals(Direction.DOWN, dirField.get(model));
    }

    @Test
    void testResetGame() throws Exception {
        setField(model, "gameOver", true);

        KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.R, false, false, false, false);
        controller.handleKeyPress(event);

        assertFalse(model.isGameOver(), "Клавиша R должна сбрасывать состояние игры");
    }
}