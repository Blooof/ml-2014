package ru.bloof.ml.practice1;

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
}
