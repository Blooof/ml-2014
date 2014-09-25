package ru.bloof.ml.practice1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.util.FastMath;

/**
 * @author Oleg Larionov
 */
public class MathUtils {
    public static double scalarMultiply(double[] a, double[] b) {
        double result = 0;
        for (int i = 0; i < a.length; i++) {
            result += a[i] * b[i];
        }
        return result;
    }

    public static double logitErrorFunction(double x) {
        return FastMath.log(errorFunction(x));
    }

    public static double errorFunction(double x) {
        return 1 + FastMath.exp(-x);
    }

    public static Pair<List<PointValuePair>, List<PointValuePair>> split(List<PointValuePair> original, double ratio) {
        Pair<List<PointValuePair>, List<PointValuePair>> result =
                new ImmutablePair<List<PointValuePair>, List<PointValuePair>>(new ArrayList<>(), new ArrayList<>());
        Random rnd = new Random();
        for (PointValuePair pair : original) {
            if (rnd.nextDouble() <= ratio) {
                result.getLeft().add(pair);
            } else {
                result.getRight().add(pair);
            }
        }
        return result;
    }
}
