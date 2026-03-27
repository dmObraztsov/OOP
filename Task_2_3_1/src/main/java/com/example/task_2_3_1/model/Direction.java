package com.example.task_2_3_1.model;

public enum Direction {
    UP(0, -1), DOWN(0, 1), RIGHT(1, 0), LEFT(-1, 0);
    final int dx, dy;
    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }
}
