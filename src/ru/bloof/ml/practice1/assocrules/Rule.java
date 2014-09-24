package ru.bloof.ml.practice1.assocrules;

import java.util.BitSet;

/**
 * @author Oleg Larionov
 */
public class Rule {
    private BitSet x, y;

    public Rule(BitSet x, BitSet y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return x + "->" + y;
    }
}
