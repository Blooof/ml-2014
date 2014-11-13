package ru.bloof.ml.practice2.rforest;

/**
 * @author <a href="mailto:blloof@gmail.com">Oleg Larionov</a>
 */
public class DecisionTreeNode {
    private DecisionTreeNode parent;
    private DecisionTreeNode left;
    private DecisionTreeNode right;
    private int threshold;
    private int feature;
    private double[] probs;

    public DecisionTreeNode(double[] probs) {
        this.probs = probs;
    }

    public void split(DecisionTreeNode left, DecisionTreeNode right, int threshold, int feature) {
        setLeft(left);
        setRight(right);
        this.threshold = threshold;
        this.feature = feature;
    }

    public DecisionTreeNode getLeft() {
        return left;
    }

    public void setLeft(DecisionTreeNode left) {
        this.left = left;
        left.parent = this;
    }

    public DecisionTreeNode getRight() {
        return right;
    }

    public void setRight(DecisionTreeNode right) {
        this.right = right;
        right.parent = this;
    }

    public DecisionTreeNode getParent() {
        return parent;
    }

    public int getThreshold() {
        return threshold;
    }

    public int getFeature() {
        return feature;
    }

    public double[] getProbs() {
        return probs;
    }

    public DecisionTreeNode nextNode(int[] features) {
        if (left == null || right == null) {
            return null;
        }
        if (features[feature] <= threshold) {
            return left;
        } else {
            return right;
        }
    }
}