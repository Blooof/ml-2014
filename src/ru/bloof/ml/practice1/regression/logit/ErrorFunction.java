package ru.bloof.ml.practice1.regression.logit;

import org.apache.commons.math3.analysis.DifferentiableMultivariateFunction;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.optimization.PointValuePair;
import ru.bloof.ml.MathUtils;

import java.util.List;

/**
 * @author Oleg Larionov
 */
public class ErrorFunction implements DifferentiableMultivariateFunction {
    private int count = 0;
    private final List<PointValuePair> original;

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
        System.out.println(count++);
        double value = 0;
        for (PointValuePair pair : original) {
            double trY = pair.getValue() == 1 ? 1 : -1;
            double scalar = MathUtils.scalarMultiply(pair.getPointRef(), doubles);
            value += MathUtils.logitErrorFunction(scalar * trY);
        }
        return value;
    }
}
