package ru.bloof.ml.practice1.knn;

import static java.lang.String.format;

/**
 * @author Oleg Larionov
 */
public class Point {
    private final double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return format("[%f;%f]", x, y);
    }
}
