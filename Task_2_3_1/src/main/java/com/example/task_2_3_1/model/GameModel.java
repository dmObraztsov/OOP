package com.example.task_2_3_1.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameModel {
    private final int width, height, targetLength, foodCount;
    private final Snake snake;
    private final List<Point> food = new ArrayList<>();
    private Direction direction = Direction.RIGHT;
    private boolean gameOver = false;
    private boolean gameWon = false;
    private boolean directionChangedThisTick = false;

    public GameModel(int width, int height, int targetLength, int foodCount) {
        this.width = width;
        this.height = height;
        this.targetLength = targetLength;
        this.foodCount = foodCount;
        this.snake = new Snake(new Point(width / 2, height / 2));
        spawnFood();
    }

    private void spawnFood() {
        List<Point> freeCells = new ArrayList<>();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Point p = new Point(x, y);
                if (!snake.getBody().contains(p) && !food.contains(p)) {
                    freeCells.add(p);
                }
            }
        }

        Random random = new Random();
        while (food.size() < foodCount && !freeCells.isEmpty()) {
            int randomIndex = random.nextInt(freeCells.size());
            Point foodPoint = freeCells.remove(randomIndex);
            food.add(foodPoint);
        }
    }

    private Point calculateNextHead() {
        Point head = snake.getHead();
        return new Point(head.x() + this.direction.dx,
                head.y() + this.direction.dy);
    }

    private boolean isCollision(Point p) {
        return p.x() < 0 || p.x() >= width || p.y() < 0 || p.y() >= height
                || snake.getBody().contains(p);
    }


    public void setDirection(Direction newDirection) {
        if (directionChangedThisTick) {
            return;
        }

        Direction current = snake.getDirection();

        if (newDirection == Direction.UP && current == Direction.DOWN) return;
        if (newDirection == Direction.DOWN && current == Direction.UP) return;
        if (newDirection == Direction.LEFT && current == Direction.RIGHT) return;
        if (newDirection == Direction.RIGHT && current == Direction.LEFT) return;

        this.direction = newDirection;
        directionChangedThisTick = true;
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

    public MoveResult update() {
        if (gameOver || gameWon) {
            return null;
        }

        snake.setDirection(this.direction);
        Point oldTail = new Point(snake.getBody().getLast().x(), snake.getBody().getLast().y());
        Point nextHead = calculateNextHead();
        Point eatenFood = null;
        boolean spawnedNewFood = false;

        if (isCollision(nextHead)) {
            gameOver = true;
            return null;
        }

        if (food.contains(nextHead)) {
            eatenFood = nextHead;
            snake.grow();
            food.remove(nextHead);
            spawnFood();
            spawnedNewFood = true;
            if (snake.getBody().size() >= targetLength) gameWon = true;
        } else {
            snake.move();
        }

        directionChangedThisTick = false;
        return new MoveResult(oldTail, nextHead, eatenFood, spawnedNewFood);
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
