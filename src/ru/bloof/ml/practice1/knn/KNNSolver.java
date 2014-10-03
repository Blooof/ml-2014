package ru.bloof.ml.practice1.knn;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.math3.util.FastMath;
import ru.bloof.ml.practice1.knn.function.distance.DistanceFunction;

import java.util.*;

import static java.lang.String.format;

public class KNNSolver {
    public static final double EPS = 1e-6;
    private final List<PointClusterPair> original = new ArrayList<>();

    public void teach(PointClusterPair pair) {
        original.add(pair);
    }

    public int solve(final Point x, final DistanceFunction f, final int k) {
        List<PointClusterPair> sorted = new ArrayList<>(original);
        Collections.sort(sorted, (o1, o2) -> {
            double d1 = f.distance(x, o1.getPoint());
            double d2 = f.distance(x, o2.getPoint());
            if (FastMath.abs(d1 - d2) < EPS) {
                return 0;
            } else if (d1 > d2) {
                return 1;
            } else {
                return -1;
            }
        });
        Map<Integer, MutableInt> clusters = new HashMap<>();
        for (int i = 0; i < k; i++) {
            PointClusterPair pair = sorted.get(i);
            if (!clusters.containsKey(pair.getCluster())) {
                clusters.put(pair.getCluster(), new MutableInt());
            }
            clusters.get(pair.getCluster()).increment();
        }
        Map.Entry<Integer, MutableInt> maxCluster = null;
        for (Map.Entry<Integer, MutableInt> entry : clusters.entrySet()) {
            if (maxCluster == null) {
                maxCluster = entry;
            } else {
                if (entry.getValue().intValue() > maxCluster.getValue().intValue()) {
                    maxCluster = entry;
                }
            }
        }
        assert maxCluster != null;
        System.out.println(format("Cluster=%d, %d/%d",
                maxCluster.getKey().intValue(), maxCluster.getValue().intValue(), k));
        return maxCluster.getKey().intValue();
    }
}
