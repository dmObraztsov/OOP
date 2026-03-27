package com.example.task_2_3_1.controller;

import com.example.task_2_3_1.model.Direction;
import com.example.task_2_3_1.model.GameModel;
import com.example.task_2_3_1.model.Point;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.LinkedList;


public class GameController {
    @FXML
    private Canvas canvas;
    private GameModel model;
    private static final int TILE_SIZE = 30;
    private static final int DELAY = 150_000_000;

    private void initGame() {
        int n = (int) (canvas.getWidth() / TILE_SIZE);
        int m = (int) (canvas.getHeight() / TILE_SIZE);

        model = new GameModel(n, m, 5, 10);
    }

    private void startTimer() {
        new AnimationTimer() {
            long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= DELAY) {
                    model.update();
                    draw();
                    lastUpdate = now;
                }
            }
        }.start();
    }

    public void initialize() {
        StackPane parent = (StackPane) canvas.getParent();
        canvas.widthProperty().bind(parent.widthProperty());
        canvas.heightProperty().bind(parent.heightProperty());
        javafx.application.Platform.runLater(this::initGame);
        startTimer();
    }

    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        for (int i = 0; i < model.getWidth(); i++) {
            for (int j = 0; j < model.getHeight(); j++) {
                if ((i + j) % 2 == 0) {
                    gc.setFill(Color.web("#AAD751"));
                } else {
                    gc.setFill(Color.web("#A2D149"));
                }
                gc.fillRect(i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        gc.setFill(Color.web("#E74C3C"));
        for (Point p : model.getFood()) {
            gc.fillOval(p.x() * TILE_SIZE + 2, p.y() * TILE_SIZE + 2, TILE_SIZE - 4, TILE_SIZE - 4);
            gc.setFill(Color.FORESTGREEN);
            gc.fillRect(p.x() * TILE_SIZE + TILE_SIZE / 2.0 - 1, p.y() * TILE_SIZE + 1, 2, 4);
            gc.setFill(Color.web("#E74C3C"));
        }

        LinkedList<Point> body = model.getSnake().getBody();
        for (int i = 0; i < body.size(); i++) {
            Point p = body.get(i);

            if (i == 0) {
                gc.setFill(Color.web("#4A752C"));
                gc.fillRoundRect(p.x() * TILE_SIZE - 1, p.y() * TILE_SIZE - 1,
                        TILE_SIZE + 1, TILE_SIZE + 1, 10, 10);

                gc.setFill(Color.BLACK);
                gc.fillOval(p.x() * TILE_SIZE + 5, p.y() * TILE_SIZE + 5, 4, 4);
                gc.fillOval(p.x() * TILE_SIZE + 11, p.y() * TILE_SIZE + 5, 4, 4);

            } else if (i == body.size() - 1 && body.size() > 1) {
                gc.setFill(Color.DARKGREEN);
                double tailReduce = 4;
                gc.fillOval(p.x() * TILE_SIZE + tailReduce / 2,
                        p.y() * TILE_SIZE + tailReduce / 2,
                        TILE_SIZE - tailReduce, TILE_SIZE - tailReduce);

            } else {
                gc.setFill(Color.web("#528032"));
                gc.fillRoundRect(p.x() * TILE_SIZE + 1, p.y() * TILE_SIZE + 1,
                        TILE_SIZE - 2, TILE_SIZE - 2, 5, 5);
            }
        }

        if (model.isGameOver()) {
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 50));
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(8);
            gc.setLineJoin(StrokeLineJoin.ROUND);
            gc.strokeText("GAME OVER", canvas.getWidth() / 2, canvas.getHeight() / 2);
            gc.setFill(Color.RED);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("GAME OVER", canvas.getWidth() / 2, canvas.getHeight() / 2);
        }

        if (model.isGameWon()) {
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 50));
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(8);
            gc.setLineJoin(StrokeLineJoin.ROUND);
            gc.strokeText("GAME WON", canvas.getWidth() / 2, canvas.getHeight() / 2);
            gc.setFill(Color.GOLD);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("GAME WON", canvas.getWidth() / 2, canvas.getHeight() / 2);
        }
    }

    @FXML
    private void handleKeyPress(KeyEvent event) {
        if (model == null) return;

        if (event.getCode() == KeyCode.R) {
            model.reset();
            return;
        }

        if (event.getCode() == KeyCode.E) {
            javafx.application.Platform.exit();
            System.exit(0);
            return;
        }


        if (model.isGameOver() || model.isGameWon()) return;

        Direction current = model.getSnake().getDirection();
        switch (event.getCode()) {
            case UP -> {
                if (current != Direction.DOWN) model.getSnake().setDirection(Direction.UP);
            }
            case DOWN -> {
                if (current != Direction.UP) model.getSnake().setDirection(Direction.DOWN);
            }
            case LEFT -> {
                if (current != Direction.RIGHT) model.getSnake().setDirection(Direction.LEFT);
            }
            case RIGHT -> {
                if (current != Direction.LEFT) model.getSnake().setDirection(Direction.RIGHT);
            }
        }
    }
}
