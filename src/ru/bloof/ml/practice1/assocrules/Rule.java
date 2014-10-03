package ru.bloof.ml.practice1.assocrules;

import java.util.BitSet;

/**
 * @author Oleg Larionov
 */
public class Rule {
    private BitSet x, y;
    private double sup, conf;

    public Rule(BitSet x, BitSet y, double sup, double conf) {
        this.x = x;
        this.y = y;
        this.sup = sup;
        this.conf = conf;
    }

    public BitSet getX() {
        return x;
    }

    public BitSet getY() {
        return y;
    }

    public double getSup() {
        return sup;
    }

    public double getConf() {
        return conf;
    }

    @Override
    public String toString() {
        return x + "->" + y + ":sup=" + sup + ",conf=" + conf;
    }
}
