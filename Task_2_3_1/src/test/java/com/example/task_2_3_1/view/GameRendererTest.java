package com.example.task_2_3_1.view;

import com.example.task_2_3_1.model.GameModel;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameRendererTest {
    @BeforeAll
    static void init() {
        try { Platform.startup(() -> {}); } catch (Exception ignored) {}
    }

    @Test
    void testRenderingMethods() {
        Canvas canvas = new Canvas(400, 400);
        GameRenderer renderer = new GameRenderer(canvas.getGraphicsContext2D());
        GameModel model = new GameModel(10, 10, 5, 1);

        assertDoesNotThrow(() -> renderer.fullRender(model));
    }
}