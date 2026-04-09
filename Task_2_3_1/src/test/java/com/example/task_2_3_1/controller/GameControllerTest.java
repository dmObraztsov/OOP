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

    @Test
    void testHandleKeyPressWithNullModel() {
        try {
            setField(controller, "model", null);
        } catch (Exception e) {
            fail("Не удалось занулить модель");
        }

        KeyEvent upEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.UP, false, false, false, false);

        assertDoesNotThrow(() -> controller.handleKeyPress(upEvent),
                "Контроллер должен игнорировать нажатия, если модель еще не инициализирована");
    }

    @Test
    void testAllDirectionsKeyStrokes() {
        assertFalse(model.isGameOver(), "Игра должна быть активна");
        KeyEvent downEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN, false, false, false, false);
        controller.handleKeyPress(downEvent);
        model.update();
        assertEquals(Direction.DOWN, model.getSnake().getDirection(),
                "Змейка должна изменить направление на DOWN после нажатия и шага");
        KeyEvent leftEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.LEFT, false, false, false, false);
        controller.handleKeyPress(leftEvent);
        model.update();
        assertEquals(Direction.LEFT, model.getSnake().getDirection());

        assertFalse(model.isGameOver(), "Игра должна быть активна");
        KeyEvent upEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.UP, false, false, false, false);
        controller.handleKeyPress(upEvent);
        model.update();
        assertEquals(Direction.UP, model.getSnake().getDirection());

        assertFalse(model.isGameOver(), "Игра должна быть активна");
        KeyEvent rightEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.RIGHT, false, false, false, false);
        controller.handleKeyPress(rightEvent);
        model.update();
        assertEquals(Direction.RIGHT, model.getSnake().getDirection());
    }

    @Test
    void testHandleKeyPressWhenGameWon() throws Exception {
        Field targetLengthField = GameModel.class.getDeclaredField("targetLength");
        targetLengthField.setAccessible(true);
        int currentSize = model.getSnake().getBody().size();
        targetLengthField.set(model, currentSize);

        Direction initialDir = model.getSnake().getDirection();

        KeyEvent rightEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.RIGHT, false, false, false, false);
        controller.handleKeyPress(rightEvent);

        assertEquals(initialDir, model.getSnake().getDirection(),
                "Ввод должен игнорироваться, если игра выиграна");
    }

    @Test
    void testInitializeMethod() throws Exception {
        Canvas testCanvas = new Canvas(300, 400);
        javafx.scene.layout.StackPane parent = new javafx.scene.layout.StackPane();
        parent.getChildren().add(testCanvas);

        parent.setPrefWidth(500);
        parent.setPrefHeight(600);

        setField(controller, "canvas", testCanvas);

        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);

        Platform.runLater(() -> {
            controller.initialize();
            latch.countDown();
        });

        boolean executed = latch.await(2, java.util.concurrent.TimeUnit.SECONDS);

        assertTrue(executed, "Platform.runLater не успел выполниться");

        assertEquals(parent.widthProperty().get(), testCanvas.widthProperty().get(), "Ширина Canvas не привязана к родителю");

        Field modelField = GameController.class.getDeclaredField("model");
        modelField.setAccessible(true);
        assertNotNull(modelField.get(controller), "Модель должна быть инициализирована");

        Field rendererField = GameController.class.getDeclaredField("renderer");
        rendererField.setAccessible(true);
        assertNotNull(rendererField.get(controller), "Рендерер должен быть инициализирован");
    }
}