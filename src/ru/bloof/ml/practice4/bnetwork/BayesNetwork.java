package ru.bloof.ml.practice4.bnetwork;

import ru.bloof.ml.practice4.bnetwork.factor.Factor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:blloof@gmail.com">Oleg Larionov</a>
 */
public class BayesNetwork {
    private List<Factor> factors;
    private Set<Event> scope;

    public BayesNetwork(List<Factor> factors) {
        this.factors = factors;
        this.scope = new HashSet<>();
        for (Factor f : factors) {
            scope.addAll(f.getScope());
        }
    }

    public Factor eliminate(Set<Event> events, Set<Evidence> evidences) {
        if (!scope.containsAll(events)) {
            throw new IllegalArgumentException("Scope has different events");
        }
        if (evidences == null) {
            evidences = Collections.emptySet();
        }

        // reduce all factors by evidence
        List<Factor> reducedFactors = new ArrayList<>();
        for (Factor f : factors) {
            Factor reducedFactor = f;
            for (Evidence ev : evidences) {
                if (reducedFactor.inScope(ev.getEvent())) {
                    reducedFactor = reducedFactor.reduce(ev);
                }
            }
            if (!reducedFactor.getScope().isEmpty()) {
                reducedFactors.add(reducedFactor);
            }
        }

        Set<Event> varsToRemove = new HashSet<>(scope);
        varsToRemove.removeAll(events);
        for (Evidence ev : evidences) {
            varsToRemove.remove(ev.getEvent());
        }

        // eliminate variables
        for (Event e : varsToRemove) {
            List<Factor> newFactors = reducedFactors.stream().filter(f -> f.inScope(e)).collect(Collectors.toList());
            if (newFactors.isEmpty()) {
                continue;
            }
            reducedFactors.removeAll(newFactors);
            Factor multiFactor = multiplyFactors(newFactors);
            multiFactor = multiFactor.marginalize(e);
            if (!multiFactor.getScope().isEmpty()) {
                reducedFactors.add(multiFactor);
            }
        }

        // multiply remaining factors
        Factor resultFactor = multiplyFactors(reducedFactors);
        resultFactor.normalize();
        return resultFactor;
    }

    private Factor multiplyFactors(List<Factor> factors) {
        Factor multiFactor = factors.get(0);
        for (Factor f : factors) {
            if (multiFactor != f) {
                multiFactor = multiFactor.multiply(f);
            }
        }
        return multiFactor;
    }
}
