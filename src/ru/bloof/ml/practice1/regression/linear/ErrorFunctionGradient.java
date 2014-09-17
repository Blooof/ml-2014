package ru.bloof.ml.practice1.regression.linear;

import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.optimization.PointValuePair;

import java.util.List;

/**
 * @author Oleg Larionov
 */
public class ErrorFunctionGradient implements MultivariateVectorFunction {
    private final List<PointValuePair> original;

    public ErrorFunctionGradient(List<PointValuePair> original) {
        this.original = original;
    }

    @Override
    public double[] value(double[] point) throws IllegalArgumentException {
        double[] value = new double[point.length];
        for (int i = 0; i < value.length; i++) {
            for (PointValuePair pair : original) {
                double current = 0;
                for (int j = 0; j < point.length; j++) {
                    current += pair.getPointRef()[j] * point[j];
                }
                current -= pair.getValue();
                current *= 2 * pair.getPointRef()[i];
                value[i] += current;
            }
        }
        return value;
    }
}
