package ru.bloof.ml.practice4.bnetwork;

import ru.bloof.ml.practice4.bnetwork.factor.Factor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:blloof@gmail.com">Oleg Larionov</a>
 */
public class Helper {
    public static final double EPS = 1e-9;

    public static void addEvidence(Factor factor, Double value, Evidence... evs) {
        Set<Evidence> evidences = new HashSet<>();
        Collections.addAll(evidences, evs);
        factor.setValue(evidences, value);
    }

    @SafeVarargs
    public static <K> Set<K> createSet(K... events) {
        Set<K> scope = new HashSet<>();
        Collections.addAll(scope, events);
        return scope;
    }
}
