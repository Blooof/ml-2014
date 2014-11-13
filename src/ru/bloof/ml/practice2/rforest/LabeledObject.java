package ru.bloof.ml.practice2.rforest;

/**
 * @author <a href="mailto:blloof@gmail.com">Oleg Larionov</a>
 */
public class LabeledObject {
    private final int[] features;
    private final int label;

    public LabeledObject(int[] features, int label) {
        this.features = features;
        this.label = label;
    }

    public int[] getFeatures() {
        return features;
    }

    public int getLabel() {
        return label;
    }
}