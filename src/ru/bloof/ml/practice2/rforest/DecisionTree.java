package ru.bloof.ml.practice2.rforest;

import ru.bloof.ml.practice2.rforest.norm.Norm;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:blloof@gmail.com">Oleg Larionov</a>
 */
public class DecisionTree {
    public static final double MAX_PROBABILITY = 0.95;
    public static final int MIN_PART_SIZE = 1;
    private DecisionTreeNode rootNode;

    public void teach(List<LabeledObject> objects, List<Integer> features, Norm norm) {
        double[] probs = new double[2];
        for (LabeledObject o : objects) {
            probs[o.getLabel()]++;
        }
        for (int i = 0; i < 2; i++) {
            probs[i] /= objects.size();
        }
        double cost = norm.calculate(probs);
        rootNode = new DecisionTreeNode(probs);

        Queue<Entity> q = new ArrayDeque<>();
        q.add(new Entity(rootNode, cost, objects));
        while (!q.isEmpty()) {
            teachNode(features, norm, q);
        }
    }

    private void teachNode(List<Integer> features, Norm norm, Queue<Entity> q) {
        Entity currentEntity = q.remove();
        double minCost = currentEntity.cost;
        int splitFeature = -1, threshold = 0;
        double minLeftProbs[] = null, minRightProbs[] = null, minLeftCost = 0, minRightCost = 0;
        List<LabeledObject> leftPart = null, rightPart = null;
        for (final int feature : features) {
            List<LabeledObject> sortedObjects = currentEntity.objects.parallelStream().sorted((o1, o2) -> o1.getFeatures()[feature] - o2.getFeatures()[feature]).collect(Collectors.toList());
            int[] leftCounts = new int[2], rightCounts = new int[2];
            for (LabeledObject o : sortedObjects) {
                rightCounts[o.getLabel()]++;
            }
            for (int i = 0; i < sortedObjects.size(); i++) {
                rightCounts[sortedObjects.get(i).getLabel()]--;
                leftCounts[sortedObjects.get(i).getLabel()]++;
                if (i != sortedObjects.size() - 1 && sortedObjects.get(i + 1).getFeatures()[feature] == sortedObjects.get(i).getFeatures()[feature]) {
                    continue;
                }
                double leftSum = leftCounts[0] + leftCounts[1];
                double rightSum = rightCounts[0] + rightCounts[1];
                double[] leftProbs = {leftCounts[0] / leftSum, leftCounts[1] / leftSum}, rightProbs = {rightCounts[0] / rightSum, rightCounts[1] / rightSum};
                double leftCost = norm.calculate(leftProbs), rightCost = norm.calculate(rightProbs), avgCost = (leftCost + rightCost) / 2;
                if (avgCost < minCost) {
                    minCost = avgCost;
                    splitFeature = feature;
                    threshold = sortedObjects.get(i).getFeatures()[feature];
                    minLeftProbs = leftProbs;
                    minRightProbs = rightProbs;
                    minLeftCost = leftCost;
                    minRightCost = rightCost;
                    leftPart = sortedObjects.subList(0, i + 1);
                    rightPart = sortedObjects.subList(i + 1, sortedObjects.size());
                }
            }
        }
        if (splitFeature >= 0) {
            DecisionTreeNode left = buildNode(q, minLeftProbs, minLeftCost, leftPart);
            DecisionTreeNode right = buildNode(q, minRightProbs, minRightCost, rightPart);
            currentEntity.parent.split(left, right, threshold, splitFeature);
        }
    }

    private DecisionTreeNode buildNode(Queue<Entity> q, double[] minRightProbs, double minRightCost, List<LabeledObject> rightPart) {
        DecisionTreeNode right = new DecisionTreeNode(minRightProbs);
        Entity rightEntity = new Entity(right, minRightCost, rightPart);
        if (!(rightPart.size() <= MIN_PART_SIZE || minRightProbs[0] >= MAX_PROBABILITY || minRightProbs[1] >= MAX_PROBABILITY)) {
            q.add(rightEntity);
        }
        return right;
    }

    public double[] classify(int[] features) {
        DecisionTreeNode currentNode = rootNode;
        while (true) {
            DecisionTreeNode nextNode = currentNode.nextNode(features);
            if (nextNode == null) {
                return currentNode.getProbs();
            }
            currentNode = nextNode;
        }
    }

    private static class Entity {
        DecisionTreeNode parent;
        double cost;
        List<LabeledObject> objects;

        public Entity(DecisionTreeNode parent, double cost, List<LabeledObject> objects) {
            this.parent = parent;
            this.cost = cost;
            this.objects = objects;
        }
    }
}