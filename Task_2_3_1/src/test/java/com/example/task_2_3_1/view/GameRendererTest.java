package com.example.task_2_3_1.view;

import com.example.task_2_3_1.model.*;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class GameRendererTest {

    @BeforeAll
    static void init() {
        try { Platform.startup(() -> {}); } catch (Exception ignored) {}
    }

    private GameModel createModel(int w, int h) {
        return new GameModel(w, h, 10, 1);
    }

    @Test
    void testFullRenderAndBackground() {
        Canvas canvas = new Canvas(300, 300);
        GameRenderer renderer = new GameRenderer(canvas.getGraphicsContext2D());
        GameModel model = createModel(10, 10);

        assertDoesNotThrow(() -> renderer.fullRender(model));
    }

    @Test
    void testRenderStepBasicMove() {
        Canvas canvas = new Canvas(300, 300);
        GameRenderer renderer = new GameRenderer(canvas.getGraphicsContext2D());

        Point oldTail = new Point(1, 1);
        Point newHead = new Point(2, 2);
        MoveResult result = new MoveResult(newHead, oldTail, null, false);

        GameModel model = createModel(10, 10);

        assertDoesNotThrow(() -> renderer.renderStep(model, result));
    }

    @Test
    void testRenderStepWithFood() {
        Canvas canvas = new Canvas(300, 300);
        GameRenderer renderer = new GameRenderer(canvas.getGraphicsContext2D());

        Point eatenFood = new Point(5, 5);
        MoveResult result = new MoveResult(new Point(5, 5), new Point(4, 5), eatenFood, true);

        GameModel model = createModel(10, 10);

        assertDoesNotThrow(() -> renderer.renderStep(model, result));
    }

    @Test
    void testRenderGameOver() throws Exception {
        Canvas canvas = new Canvas(300, 300);
        GameRenderer renderer = new GameRenderer(canvas.getGraphicsContext2D());
        GameModel model = createModel(10, 10);

        setPrivateField(model, "gameOver", true);

        assertDoesNotThrow(() -> renderer.renderStep(model, null));
    }

    @Test
    void testRenderGameWon() throws Exception {
        Canvas canvas = new Canvas(300, 300);
        GameRenderer renderer = new GameRenderer(canvas.getGraphicsContext2D());
        GameModel model = createModel(10, 10);

        setPrivateField(model, "gameWon", true);

        assertDoesNotThrow(() -> renderer.renderStep(model, null));
    }

    @Test
    void testSnakeWithLongBody() {
        Canvas canvas = new Canvas(600, 600);
        GameRenderer renderer = new GameRenderer(canvas.getGraphicsContext2D());
        GameModel model = createModel(20, 20);

        Snake snake = model.getSnake();
        snake.grow();
        snake.grow();
        snake.grow();

        assertDoesNotThrow(() -> renderer.fullRender(model));

        MoveResult result = new MoveResult(new Point(0,0), new Point(0,0), null, false);
        assertDoesNotThrow(() -> renderer.renderStep(model, result));
    }

    private void setPrivateField(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }
}