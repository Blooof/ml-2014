package ru.bloof.ml.practice1.regression.linear;

import org.apache.commons.math3.analysis.DifferentiableMultivariateFunction;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.util.FastMath;
import ru.bloof.ml.MathUtils;

import java.util.List;

/**
 * @author Oleg Larionov
 */
public class ErrorFunction implements DifferentiableMultivariateFunction {
    private List<PointValuePair> original;

    public ErrorFunction(List<PointValuePair> original) {
        this.original = original;
    }

    @Override
    public MultivariateFunction partialDerivative(int i) {
        throw new UnsupportedOperationException("Partial derivative error");
    }

    @Override
    public MultivariateVectorFunction gradient() {
        return new ErrorFunctionGradient(original);
    }

    @Override
    public double value(double[] doubles) {
        double total = 0;
        for (PointValuePair pair : original) {
            total += FastMath.pow(MathUtils.scalarMultiply(pair.getPointRef(), doubles) - pair.getValue(), 2);
        }
        return total;
    }
}
