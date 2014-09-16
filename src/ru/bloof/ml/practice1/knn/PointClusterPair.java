package ru.bloof.ml.practice1.knn;

/**
 * @author Oleg Larionov
 */
public class PointClusterPair {
    private final Point point;
    private final int cluster;

    public PointClusterPair(Point point, int cluster) {
        this.point = point;
        this.cluster = cluster;
    }

    public static PointClusterPair parse(String string) {
        String[] tokens = string.split(",");
        Point p = new Point(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1]));
        int c = Integer.parseInt(tokens[2]);
        return new PointClusterPair(p, c);
    }

    public Point getPoint() {
        return point;
    }

    public int getCluster() {
        return cluster;
    }

    @Override
    public String toString() {
        return point.toString() + "->" + cluster;
    }
}
