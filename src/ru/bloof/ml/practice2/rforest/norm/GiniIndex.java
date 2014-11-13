package ru.bloof.ml.practice2.rforest.norm;

import java.util.Arrays;

/**
 * @author <a href="mailto:blloof@gmail.com">Oleg Larionov</a>
 */
public class GiniIndex implements Norm {
    @Override
    public double calculate(double[] probs) {
        return Arrays.stream(probs).map(a -> a * (1 - a)).sum();
    }
}