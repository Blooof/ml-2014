package ru.bloof.ml.practice1.regression.linear;

import org.apache.commons.math3.analysis.MultivariateFunction;

/**
 * @author Oleg Larionov
 */
public class LinearRegressionFunction implements MultivariateFunction {
    private final double[] alpha;

    public LinearRegressionFunction(double[] alpha) {
        this.alpha = alpha;
    }

    @Override
    public double value(double[] doubles) {
        double value = 0;
        for (int i = 0; i < alpha.length; i++) {
            value += doubles[i] * alpha[i];
        }
        return value;
    }
}
