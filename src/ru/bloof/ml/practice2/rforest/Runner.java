package ru.bloof.ml.practice2.rforest;

import org.apache.commons.math3.util.FastMath;
import ru.bloof.ml.practice2.rforest.norm.GiniIndex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:blloof@gmail.com">Oleg Larionov</a>
 */
public class Runner {
    public static final String TRAIN_DATA_PATH = "data/random_forest/arcene_train.data";
    public static final String TRAIN_LABELS_PATH = "data/random_forest/arcene_train.labels";
    public static final String TEST_DATA_PATH = "data/random_forest/arcene_valid.data";
    public static final String TEST_LABELS_PATH = "data/random_forest/arcene_valid.labels";
    public static final int FEATURES_COUNT = 10000;
    public static final int OBJECTS_COUNT = 100;
    public static final int TREES_COUNT = 5;
    public static final int M = (int) FastMath.sqrt(FEATURES_COUNT);
    public static final int N = 100;
    public static final double MIN_PEARSON_COEF = 0.2;
    public static final boolean USE_FEATURE_SELECTING = true;

    public static void main(String[] args) throws Exception {
        List<LabeledObject> trainSet = new ArrayList<>();
        int f[][] = new int[OBJECTS_COUNT][], l[] = new int[OBJECTS_COUNT];
        try (BufferedReader trainData = new BufferedReader(new FileReader(TRAIN_DATA_PATH));
             BufferedReader trainLabels = new BufferedReader(new FileReader(TRAIN_LABELS_PATH))) {
            for (int i = 0; i < OBJECTS_COUNT; i++) {
                String[] s = trainData.readLine().split(" ");
                int[] features = new int[FEATURES_COUNT];
                for (int j = 0; j < FEATURES_COUNT; j++) {
                    features[j] = Integer.parseInt(s[j]);
                }
                f[i] = features;
                int label = Math.max(Integer.parseInt(trainLabels.readLine()), 0); // 0-1
                l[i] = label;
                trainSet.add(new LabeledObject(features, label));
            }
        }
        List<Integer> selectedFeatures = null;
        if (USE_FEATURE_SELECTING) {
            selectedFeatures = selectFeatures(f, l);
            System.out.println("features selected = " + selectedFeatures.size());
        }
        RandomForestClassifier classifier = new RandomForestClassifier();
        classifier.teach(trainSet, TREES_COUNT, N, M, new GiniIndex(), selectedFeatures);

        int correct = 0, fp = 1, wrong = 0, totalCorrect = 0;
        try (BufferedReader testData = new BufferedReader(new FileReader(TEST_DATA_PATH));
             BufferedReader testLabels = new BufferedReader(new FileReader(TEST_LABELS_PATH))) {
            for (int i = 0; i < OBJECTS_COUNT; i++) {
                String[] s = testData.readLine().split(" ");
                int[] features = new int[FEATURES_COUNT];
                for (int j = 0; j < FEATURES_COUNT; j++) {
                    features[j] = Integer.parseInt(s[j]);
                }
                int myLabel = classifier.classify(features);
                int label = Math.max(Integer.parseInt(testLabels.readLine()), 0); // 0-1
                if (label == 1) {
                    if (myLabel == 1) {
                        correct++;
                        totalCorrect++;
                    } else {
                        wrong++;
                    }
                } else if (myLabel == 0) {
                    totalCorrect++;
                } else {
                    fp++;
                }
                trainSet.add(new LabeledObject(features, label));
            }
        }
        System.out.println(totalCorrect + "/" + OBJECTS_COUNT);
        System.out.println(String.format("found=%d/%d precision=%f recall=%f", correct, correct + wrong, 1. * correct / (correct + fp), 1. * correct / (correct + wrong)));
    }

    public static List<Integer> selectFeatures(int[][] features, int[] labels) {
        List<Integer> selected = new ArrayList<>();
        for (int i = 0; i < FEATURES_COUNT; i++) {
            double averageF = 0, averageL = 0;
            for (int j = 0; j < OBJECTS_COUNT; j++) {
                averageF += features[j][i];
                averageL += labels[j];
            }
            averageF /= OBJECTS_COUNT;
            averageL /= OBJECTS_COUNT;
            double num = 0, denomF = 0, denomL = 0;
            for (int j = 0; j < OBJECTS_COUNT; j++) {
                double diffF = features[j][i] - averageF;
                double diffL = labels[j] - averageL;
                num += diffF * diffL;
                denomF += diffF * diffF;
                denomL += diffL * diffL;
            }
            double pearsonCoef = Math.abs(num) < 0.00001 ? 0 : num / Math.sqrt(denomF * denomL);
            if (Math.abs(pearsonCoef) >= MIN_PEARSON_COEF) {
                selected.add(i);
            }
        }
        return selected;
    }
}