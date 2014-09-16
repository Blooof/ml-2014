package ru.bloof.ml.practice1.knn.function.distance;

import ru.bloof.ml.practice1.knn.Point;

/**
 * @author Oleg Larionov
 */
public interface DistanceFunction {
    double distance(Point a, Point b);
}
