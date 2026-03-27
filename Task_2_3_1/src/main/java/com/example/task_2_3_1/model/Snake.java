package com.example.task_2_3_1.model;

import java.util.LinkedList;

public class Snake {
    private final LinkedList<Point> body = new LinkedList<>();
    private Direction direction = Direction.RIGHT;

    public Snake(Point start) {
        body.add(start);
    }

    public void move(){
        Point head = body.getFirst();
        body.addFirst(new Point(head.x() + direction.dx, head.y() + direction.dy));
        body.removeLast();
    }

    public void grow(){
        Point head = body.getFirst();
        body.addFirst(new Point(head.x() + direction.dx, head.y() + direction.dy));
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public LinkedList<Point> getBody() {
        return body;
    }

    public Point getHead(){
        return body.getFirst();
    }
}
