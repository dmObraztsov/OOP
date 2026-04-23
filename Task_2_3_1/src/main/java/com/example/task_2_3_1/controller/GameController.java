package com.example.task_2_3_1.controller;

import com.example.task_2_3_1.config.GameConfig;
import com.example.task_2_3_1.model.Direction;
import com.example.task_2_3_1.model.GameModel;
import com.example.task_2_3_1.model.MoveResult;
import com.example.task_2_3_1.view.GameRenderer;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;



public class GameController {
    @FXML
    private Canvas canvas;
    private GameModel model;
    private GameRenderer renderer;

    private void initGame() {
        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();

        double n = canvasWidth / GameConfig.FIELD_WIDTH;
        double m = canvasHeight / GameConfig.FIELD_HEIGHT;

        GameConfig.TILE_SIZE = Math.min(n, m);
        renderer.updateOffsets(canvasWidth, canvasHeight);
        model = new GameModel(GameConfig.FIELD_WIDTH, GameConfig.FIELD_HEIGHT, GameConfig.TARGET_LENGTH, GameConfig.INITIAL_FOOD_COUNT);
    }

    private void startTimer() {
        new AnimationTimer() {
            long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (model == null) {
                    return;
                }

                if (now - lastUpdate >= GameConfig.UPDATE_INTERVAL) {
                    MoveResult result = model.update();
                    renderer.renderStep(model, result);
                    lastUpdate = now;
                }
            }
        }.start();
    }

    public void initialize() {
        StackPane parent = (StackPane) canvas.getParent();
        canvas.widthProperty().bind(parent.widthProperty());
        canvas.heightProperty().bind(parent.heightProperty());
        renderer = new GameRenderer(canvas.getGraphicsContext2D());

        javafx.application.Platform.runLater(() -> {
            initGame();
            renderer.fullRender(model);
            startTimer();
        });
    }


    @FXML
    public void handleKeyPress(KeyEvent event) {
        if (model == null) return;

        if (event.getCode() == KeyCode.R) {
            model.reset();
            renderer.fullRender(model);
            return;
        }

        if (event.getCode() == KeyCode.E) {
            javafx.application.Platform.exit();
            System.exit(0);
            return;
        }


        if (model.isGameOver() || model.isGameWon()) return;

        switch (event.getCode()) {
            case UP -> model.setDirection(Direction.UP);
            case DOWN -> model.setDirection(Direction.DOWN);
            case LEFT -> model.setDirection(Direction.LEFT);
            case RIGHT -> model.setDirection(Direction.RIGHT);
        }
    }
}
