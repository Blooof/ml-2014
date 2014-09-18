package ru.bloof.ml.practice1.regression.logit;

import java.util.List;

import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.util.FastMath;

import ru.bloof.ml.practice1.MathUtils;

/**
 * @author Oleg Larionov
 */
public class ErrorFunctionGradient implements MultivariateVectorFunction {
    private final List<PointValuePair> original;

    public ErrorFunctionGradient(List<PointValuePair> original) {
        this.original = original;
    }

    @Override
    public double[] value(double[] doubles) throws IllegalArgumentException {
        double[] result = new double[doubles.length];
        for (int i = 0; i < result.length; i++) {
            for (PointValuePair pair : original) {
                double trY = pair.getValue() == 1 ? 1 : -1;
                double current = MathUtils.scalarMultiply(pair.getPointRef(), doubles) * trY;
                result[i] += FastMath.exp(-current) / MathUtils.errorFunction(current) * -trY * pair.getPointRef()[i];
            }
        }
        return result;
    }
}
