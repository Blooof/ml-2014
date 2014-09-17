package ru.bloof.ml.practice1.regression.linear;

import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.SimplePointChecker;
import org.apache.commons.math3.optimization.general.ConjugateGradientFormula;
import org.apache.commons.math3.optimization.general.NonLinearConjugateGradientOptimizer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Oleg Larionov
 */
public class Runner {
    public static final String FILENAME = "prices.txt";
    public static final int MAX_EVAL = 100;

    public static void main(String[] args) throws Exception {
        List<PointValuePair> original = new ArrayList<>();
        try (BufferedReader input = new BufferedReader(new FileReader(FILENAME))) {
            input.lines().map(s -> {
                String[] tokens = s.split(",");
                double[] x = new double[3];
                x[0] = Double.parseDouble(tokens[0]) / 10_000;
                x[1] = Double.parseDouble(tokens[1]) / 10;
                x[2] = 1;
                double y = Double.parseDouble(tokens[2]) / 1_000_000;
                return new PointValuePair(x, y, false);
            }).forEach(original::add);
        }
        ErrorFunction errorFunction = new ErrorFunction(original);
        NonLinearConjugateGradientOptimizer optimizer = new NonLinearConjugateGradientOptimizer(
                ConjugateGradientFormula.POLAK_RIBIERE, new SimplePointChecker<>(0.1, -1));
        PointValuePair p = optimizer.optimize(MAX_EVAL, errorFunction, GoalType.MINIMIZE, new double[]{0, 0, 0});
        System.out.println(Arrays.toString(p.getPointRef()));
        System.out.println(p.getValue());
        LinearRegressionFunction regressionFunction = new LinearRegressionFunction(p.getPointRef());
//        printOriginal(original, regressionFunction);
        try (BufferedReader input = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                String line = input.readLine();
                String[] tokens = line.split(" ");
                double[] x = new double[3];
                x[0] = Double.parseDouble(tokens[0]) / 10_000;
                x[1] = Double.parseDouble(tokens[1]) / 10;
                x[2] = 1;
                System.out.println(regressionFunction.value(x) * 1_000_000);
            }
        }
    }

    @SuppressWarnings("unused")
    private static void printOriginal(List<PointValuePair> original, LinearRegressionFunction f) {
        for (PointValuePair pair : original) {
            System.out.println("Expected " + pair.getValue() + ", actual " + f.value(pair.getPointRef()));
        }
    }
}
