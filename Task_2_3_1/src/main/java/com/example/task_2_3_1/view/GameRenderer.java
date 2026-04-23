package com.example.task_2_3_1.view;

import com.example.task_2_3_1.config.GameConfig;
import com.example.task_2_3_1.model.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.*;

import java.util.LinkedList;

public class GameRenderer {
    private final GraphicsContext gc;
    private double offsetX;
    private double offsetY;

    public GameRenderer(GraphicsContext gc) {
        this.gc = gc;
    }

    public void updateOffsets(double canvasWidth, double canvasHeight){
        this.offsetX = (canvasWidth - (GameConfig.FIELD_WIDTH * GameConfig.TILE_SIZE)) / 2;
        this.offsetY = (canvasHeight - (GameConfig.FIELD_HEIGHT * GameConfig.TILE_SIZE)) / 2;
    }

    public void fullRender(GameModel model) {
        drawBackground(model.getWidth(), model.getHeight());
        drawFood(model);
        drawSnake(model);
    }

    public void renderStep(GameModel model, MoveResult result) {
        if (result == null) {
            if (model.isGameOver() || model.isGameWon()) drawEndScreen(model);
            return;
        }

        if (result.eatenFood() == null) {
            drawTile(result.oldTail().x(), result.oldTail().y());
        }

        LinkedList<Point> body = model.getSnake().getBody();

        for (int i = 1; i < body.size() - 1; i++) {
            drawBodyPart(body.get(i).x(), body.get(i).y());
        }

        if (body.size() > 1) {
            Point tailPos = body.getLast();
            drawTile(tailPos.x(), tailPos.y());
            drawTail(tailPos.x(), tailPos.y());
        }

        drawHead(result.newHead().x(), result.newHead().y());

        if (result.spawnedNewFood()) {
            drawFood(model);
        }
    }

    private void drawTail(int x, int y) {
        gc.setFill(Color.web(GameConfig.COLOR_SNAKE_TAIL));

        double tailReduce = 6.0;
        double size = GameConfig.TILE_SIZE - tailReduce;

        gc.fillOval(
                (int) offsetX + x * GameConfig.TILE_SIZE + tailReduce / 2.0,
                (int) offsetY + y * GameConfig.TILE_SIZE + tailReduce / 2.0,
                (int) size,
                (int) size
        );
    }

    private void drawBackground(int width, int height) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                drawTile(i, j);
            }
        }
    }

    private void drawSnake(GameModel model) {
        var body = model.getSnake().getBody();
        for (int i = 0; i < body.size(); i++) {
            Point p = body.get(i);
            if (i == 0) {
                drawHead(p.x(), p.y());
            } else if (i == body.size() - 1 && body.size() > 1) {
                gc.setFill(Color.web(GameConfig.COLOR_SNAKE_TAIL));
                double tailReduce = 4;
                gc.fillOval(p.x() * GameConfig.TILE_SIZE + tailReduce / 2,
                        p.y() * GameConfig.TILE_SIZE + tailReduce / 2,
                        GameConfig.TILE_SIZE - tailReduce, GameConfig.TILE_SIZE - tailReduce);
            } else {
                drawBodyPart(p.x(), p.y());
            }
        }
    }

    private void drawEndScreen(GameModel model) {
        String message = model.isGameWon() ? GameConfig.GAME_WON : GameConfig.GAME_OVER;
        Color color = model.isGameWon() ? Color.GOLD : Color.RED;

        gc.setFont(Font.font(GameConfig.FONT_FAMILY, FontWeight.BOLD, GameConfig.FONT_SIZE_MAIN));
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(GameConfig.LINE_WIDTH);
        gc.setLineJoin(StrokeLineJoin.ROUND);

        double x = gc.getCanvas().getWidth() / 2;
        double y = gc.getCanvas().getHeight() / 2;

        gc.setTextAlign(TextAlignment.CENTER);
        gc.strokeText(message, x, y);
        gc.setFill(color);
        gc.fillText(message, x, y);
    }

    private void drawTile(int i, int j) {

        gc.setFill(Color.web((i + j) % 2 == 0 ? GameConfig.COLOR_BG_LIGHT : GameConfig.COLOR_BG_DARK));

        gc.fillRect(
                (int) Math.round(offsetX + i * GameConfig.TILE_SIZE - 0.5),
                (int) Math.round(offsetY + j * GameConfig.TILE_SIZE - 0.5),
                (int) GameConfig.TILE_SIZE + 2,
                (int) GameConfig.TILE_SIZE + 2
        );
    }

    private void drawHead(int x, int y) {
        double size = GameConfig.TILE_SIZE;
        double eyeSize = size * 0.15;

        gc.setFill(Color.web(GameConfig.COLOR_SNAKE_HEAD));
        gc.fillRect((int) offsetX + x * size + 1, (int) offsetY + y * size + 1, (int) size - 1, (int) size - 1);

        gc.setFill(Color.BLACK);

        double eyeY = (int) offsetY + y * size + (size * 0.25);
        gc.fillOval((int) offsetX + x * size + (size * 0.2), eyeY, eyeSize, eyeSize);
        gc.fillOval((int) offsetX + x * size + (size * 0.6), eyeY, eyeSize, eyeSize);
    }

    private void drawBodyPart(int x, int y) {
        gc.setFill(Color.web(GameConfig.COLOR_SNAKE_BODY));
        gc.fillRect((int) offsetX + x * GameConfig.TILE_SIZE + 1, (int) offsetY + y * GameConfig.TILE_SIZE + 1,
                (int) GameConfig.TILE_SIZE - 2, (int) GameConfig.TILE_SIZE - 2);
    }

    private void drawFood(GameModel model) {
        for (Point p : model.getFood()) {
            gc.setFill(Color.web(GameConfig.COLOR_FOOD_MAIN));
            gc.fillOval((int) offsetX + p.x() * GameConfig.TILE_SIZE + 2, (int) offsetY+ p.y() * GameConfig.TILE_SIZE + 2, (int) GameConfig.TILE_SIZE - 4, (int) GameConfig.TILE_SIZE - 4);
            gc.setFill(Color.web(GameConfig.COLOR_FOOD_STEM));
            gc.fillRect((int) offsetX + p.x() * GameConfig.TILE_SIZE + GameConfig.TILE_SIZE / 2.0 - 1, (int) offsetY + p.y() * GameConfig.TILE_SIZE + 1, 2, 4);
        }
    }
}