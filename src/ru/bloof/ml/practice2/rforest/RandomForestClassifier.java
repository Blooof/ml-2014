package ru.bloof.ml.practice2.rforest;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import ru.bloof.ml.MathUtils;
import ru.bloof.ml.practice2.rforest.norm.Norm;

/**
 * @author <a href="mailto:blloof@gmail.com">Oleg Larionov</a>
 */
public class RandomForestClassifier {
    private List<DecisionTree> trees;

    public void teach(List<LabeledObject> objects, int treesCount, Norm norm, List<Integer> selectedFeatures) {
        long time = System.currentTimeMillis();
        RandomGenerator rnd = new MersenneTwister();
        trees = new Vector<>();
        int featuresCount = objects.get(0).getFeatures().length;
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
        MutableDouble avgPrecision = new MutableDouble(), avgRecall = new MutableDouble();
        for (int i = 0; i < treesCount; i++) {
            int objectsSelected = objects.size();
            Pair<List<LabeledObject>, List<LabeledObject>> splitObjects = MathUtils.splitList(objects, rnd, objectsSelected, true);
            List<Integer> randomFeatures;
            if (selectedFeatures == null) {
                randomFeatures = MathUtils.getRandomNumbers(featuresCount, rnd, (int) Math.sqrt(featuresCount), false);
            } else {
                randomFeatures = MathUtils.splitList(selectedFeatures, rnd, (int) Math.sqrt(selectedFeatures.size()), false)
                        .getLeft();
            }
            executorService.submit(() -> {
                DecisionTree tree = new DecisionTree();
                try {
                    tree.teach(splitObjects.getLeft(), randomFeatures, norm);
                    double correct = 0, selected = 0, fn = 0;
                    for (LabeledObject testObject : splitObjects.getRight()) {
                        double[] probs = tree.classify(testObject.getFeatures());
                        int myLabel = MathUtils.max(probs);
                        if (myLabel == 1) {
                            selected++;
                            if (testObject.getLabel() == 1) {
                                correct++;
                            }
                        } else if (testObject.getLabel() == 1) {
                            fn++;
                        }
                    }
                    synchronized (avgPrecision) {
                        if (selected > 0) {
                            avgPrecision.add(correct / selected);
                        }
                        if (correct + fn > 0) {
                            avgRecall.add(correct / (correct + fn));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                trees.add(tree);
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(100, TimeUnit.HOURS);
            System.out.println("Forest is ready. Construct time " + (System.currentTimeMillis() - time) + " msec");
            System.out
                    .println(String.format("avg precision=%f, avg recall=%f", avgPrecision.doubleValue() / treesCount, avgRecall
                            .doubleValue() / treesCount));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int classify(int[] features) {
        int[] count = new int[2];
        for (DecisionTree t : trees) {
            double[] probs = t.classify(features);
            if (probs[0] > probs[1]) {
                count[0]++;
            } else {
                count[1]++;
            }
        }
        if (count[0] > count[1]) {
            return 0;
        } else {
            return 1;
        }
    }
}