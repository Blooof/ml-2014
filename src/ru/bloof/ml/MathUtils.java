package ru.bloof.ml;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.util.FastMath;

import java.util.*;

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
                new ImmutablePair<>(new ArrayList<>(), new ArrayList<>());
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

    public static <T> Pair<List<T>, List<T>> splitList(List<T> list, RandomGenerator rnd, int count, boolean dupl) {
        List<T> selected = new ArrayList<>(), remained = new ArrayList<>();
        int size = list.size();
        Set<Integer> used = new HashSet<>();
        while (selected.size() < count) {
            int num = rnd.nextInt(size);
            if (!dupl && used.contains(num)) {
                continue;
            }
            used.add(num);
            selected.add(list.get(num));
        }

        for (int i = 0; i < list.size(); i++) {
            if (!used.contains(i)) {
                remained.add(list.get(i));
            }
        }
        return Pair.of(selected, remained);
    }

    public static int max(double[] a) {
        int index = 0;
        double max = a[index];
        for (int i = 1; i < a.length; i++) {
            if (max < a[i]) {
                max = a[i];
                index = i;
            }
        }
        return index;
    }

    public static List<Integer> getRandomNumbers(int max, RandomGenerator rnd, int count) {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            numbers.add(rnd.nextInt(max));
        }
        return numbers;
    }
}