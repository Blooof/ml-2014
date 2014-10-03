package ru.bloof.ml.practice1.knn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ru.bloof.ml.practice1.knn.function.distance.DistanceFunction;
import ru.bloof.ml.practice1.knn.function.distance.EuclideanDistanceFunction;

/**
 * @author Oleg Larionov
 */
public class Runner {
    public static final String FILENAME = "chips.txt";

    public static void main(String[] args) throws Exception {
        KNNSolver solver = new KNNSolver();
        List<PointClusterPair> pairs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
            reader.lines().map(PointClusterPair::parse).forEachOrdered(pairs::add);
        }
        pairs.stream().forEachOrdered(solver::teach);

        DistanceFunction f = new EuclideanDistanceFunction();
        int k = 5;
        int errors = pairs.parallelStream().mapToInt(value -> solver.solve(value.getPoint(), f, k) == value.getCluster() ? 0 : 1).sum();
        System.out.println(errors);
//        manualTest(solver, f, k);
    }

    @SuppressWarnings("unused")
    private static void manualTest(KNNSolver solver, DistanceFunction f, int k) throws IOException {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                String[] tokens = input.readLine().split(" ");
                Point p = new Point(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1]));
                solver.solve(p, f, k);
            }
        }
    }
}
