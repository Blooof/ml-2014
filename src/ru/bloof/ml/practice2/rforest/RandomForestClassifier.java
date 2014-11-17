package ru.bloof.ml.practice2.rforest;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import ru.bloof.ml.MathUtils;
import ru.bloof.ml.practice2.rforest.norm.Norm;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:blloof@gmail.com">Oleg Larionov</a>
 */
public class RandomForestClassifier {
    private List<DecisionTree> trees;

    public void teach(List<LabeledObject> objects, int treesCount, int objectsSelected, int featuresSelected, Norm norm, List<Integer> selectedFeatures) {
        RandomGenerator rnd = new MersenneTwister();
        trees = new Vector<>();
        int featuresCount = objects.get(0).getFeatures().length;
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
        for (int i = 0; i < treesCount; i++) {
            List<LabeledObject> randomObjects = MathUtils.createRandomList(objects, rnd, objectsSelected);
            List<Integer> randomFeatures;
            if (selectedFeatures == null) {
                randomFeatures = MathUtils.getRandomNumbers(featuresCount, rnd, featuresSelected);
            } else {
                randomFeatures = MathUtils.createRandomList(selectedFeatures, rnd, featuresSelected);
            }

            executorService.submit(() -> {
                DecisionTree tree = new DecisionTree();
                try {
                    tree.teach(randomObjects, randomFeatures, norm);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                trees.add(tree);
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(100, TimeUnit.HOURS);
            System.out.println("Forest is ready");
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