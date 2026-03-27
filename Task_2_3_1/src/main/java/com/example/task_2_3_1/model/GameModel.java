package com.example.task_2_3_1.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameModel {
    private final int width, height, targetLength, foodCount;
    private final Snake snake;
    private final List<Point> food = new ArrayList<>();
    private boolean gameOver = false;
    private boolean gameWon = false;

    public GameModel(int width, int height, int targetLength, int foodCount) {
        this.width = width;
        this.height = height;
        this.targetLength = targetLength;
        this.foodCount = foodCount;
        this.snake = new Snake(new Point(width / 2, height / 2));
        spawnFood();
    }

    private void spawnFood() {
        Random random = new Random();
        while (food.size() < foodCount) {
            Point p = new Point(random.nextInt(width), random.nextInt(height));
            if (!snake.getBody().contains(p) && !food.contains(p)) {
                food.add(p);
            }
        }
    }

    private Point calculateNextHead() {
        Point head = snake.getHead();
        return new Point(head.x() + snake.getDirection().dx,
                head.y() + snake.getDirection().dy);
    }

    private boolean isCollision(Point p) {
        return p.x() < 0 || p.x() >= width || p.y() < 0 || p.y() >= height
                || snake.getBody().contains(p);
    }


    public Snake getSnake() {
        return snake;
    }

    public List<Point> getFood() {
        return food;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void update() {
        if (gameOver || gameWon) {
            return;
        }

        Point nextHead = calculateNextHead();

        if (isCollision(nextHead)) {
            gameOver = true;
            return;
        }

        if (food.contains(nextHead)) {
            snake.grow();
            food.remove(nextHead);
            spawnFood();
            if (snake.getBody().size() >= targetLength) {
                gameWon = true;
            }
        } else {
            snake.move();
        }
    }

    public void reset() {
        this.gameOver = false;
        this.gameWon = false;
        this.snake.getBody().clear();
        this.snake.getBody().add(new Point(width / 2, height / 2));
        this.snake.setDirection(Direction.RIGHT);
        this.food.clear();
        spawnFood();
    }


}
