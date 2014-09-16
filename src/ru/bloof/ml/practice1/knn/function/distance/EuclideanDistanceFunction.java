package ru.bloof.ml.practice1.knn.function.distance;

import static org.apache.commons.math3.util.FastMath.pow;
import static org.apache.commons.math3.util.FastMath.sqrt;

import ru.bloof.ml.practice1.knn.Point;

/**
 * @author Oleg Larionov
 */
public class EuclideanDistanceFunction implements DistanceFunction {
    @Override
    public double distance(Point a, Point b) {
        return sqrt(pow(a.getX() - b.getX(), 2) + pow(a.getY() - b.getY(), 2));
    }
}
