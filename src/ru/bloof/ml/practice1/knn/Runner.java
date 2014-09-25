package ru.bloof.ml.practice1.knn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;

import ru.bloof.ml.practice1.knn.function.distance.DistanceFunction;
import ru.bloof.ml.practice1.knn.function.distance.EuclideanDistanceFunction;

/**
 * @author Oleg Larionov
 */
public class Runner {
    public static final String FILENAME = "chips.txt";

    public static void main(String[] args) throws Exception {
        KNNSolver solver = new KNNSolver();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
            reader.lines().map(PointClusterPair::parse).forEachOrdered(solver::teach);
        }

        DistanceFunction f = new EuclideanDistanceFunction();
        int k = 5;
        try (BufferedReader input = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                String[] tokens = input.readLine().split(" ");
                Point p = new Point(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1]));
                solver.solve(p, f, k);
            }
        }
    }
}
