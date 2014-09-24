package ru.bloof.ml.practice1.assocrules;

import java.util.BitSet;

/**
 * @author Oleg Larionov
 */
public class Bucket {
    private long id;
    private BitSet bitSet;

    public Bucket(long id) {
        this.id = id;
        bitSet = new BitSet();
    }

    public BitSet getSet() {
        return (BitSet) bitSet.clone();
    }

    public void add(int i) {
        bitSet.set(i);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Bucket{");
        sb.append("id=").append(id);
        sb.append(", bitSet=").append(bitSet);
        sb.append('}');
        return sb.toString();
    }
}
