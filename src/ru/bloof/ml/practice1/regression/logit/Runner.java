package ru.bloof.ml.practice1.regression.logit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.SimpleValueChecker;
import org.apache.commons.math3.optimization.general.ConjugateGradientFormula;
import org.apache.commons.math3.optimization.general.NonLinearConjugateGradientOptimizer;

import ru.bloof.ml.practice1.MathUtils;

/**
 * @author Oleg Larionov
 */
public class Runner {
    public static final String FILENAME = "chips.txt";
    public static final int MAX_EVAL = 1000;

    public static void main(String[] args) throws Exception {
        List<PointValuePair> original = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
            reader.lines().map(Runner::parse).forEachOrdered(original::add);
        }
        ErrorFunction errorFunction = new ErrorFunction(original);
        NonLinearConjugateGradientOptimizer optimizer = new NonLinearConjugateGradientOptimizer(
                ConjugateGradientFormula.POLAK_RIBIERE, new SimpleValueChecker(0.01, -1));
        PointValuePair pair = optimizer.optimize(MAX_EVAL, errorFunction, GoalType.MINIMIZE, new double[6]);
        System.out.println(Arrays.toString(pair.getPointRef()));
        System.out.println(pair.getValue());

//        printOriginal(original, pair);
    }

    @SuppressWarnings("unused")
    private static void printOriginal(List<PointValuePair> original, PointValuePair pair) {
        int correct = 0;
        for (PointValuePair p : original) {
            int actual = MathUtils.scalarMultiply(p.getPointRef(), pair.getPointRef()) >= 0 ? 1 : 0;
            if (p.getValue() == actual) {
                correct++;
            }
            System.out.println("Expected " + p.getValue() + ", actual " + actual);
        }
        System.out.println("Correct " + correct + "/" + original.size());
    }

    private static PointValuePair parse(String s) {
        double[] x = new double[6];
        String[] tokens = s.split(",");
        x[0] = Double.parseDouble(tokens[0]);
        x[1] = Double.parseDouble(tokens[1]);
        x[2] = 1;
        x[3] = x[0] * x[0];
        x[4] = x[1] * x[1];
        x[5] = x[0] * x[1];
        double y = Double.parseDouble(tokens[2]);
        return new PointValuePair(x, y, false);
    }
}
