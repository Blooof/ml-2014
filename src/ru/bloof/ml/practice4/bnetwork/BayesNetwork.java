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
        while (!varsToRemove.isEmpty()) {
            final Event eliminatedEvent = findMinEvent(reducedFactors, varsToRemove);
            varsToRemove.remove(eliminatedEvent);
            List<Factor> newFactors = reducedFactors.stream().filter(f -> f.inScope(eliminatedEvent)).collect(Collectors.toList());
            if (newFactors.isEmpty()) {
                continue;
            }
            reducedFactors.removeAll(newFactors);
            Factor multiFactor = multiplyFactors(newFactors);
            multiFactor = multiFactor.marginalize(eliminatedEvent);
            if (!multiFactor.getScope().isEmpty()) {
                reducedFactors.add(multiFactor);
            }
        }

        // multiply remaining factors
        Factor resultFactor = multiplyFactors(reducedFactors);
        resultFactor.normalize();
        return resultFactor;
    }

    private Event findMinEvent(List<Factor> reducedFactors, Set<Event> varsToRemove) {
        int[] counts = new int[Event.values().length];
        for (Factor f : reducedFactors) {
            for (Event e : f.getScope()) {
                counts[e.ordinal()]++;
            }
        }
        int min = Integer.MAX_VALUE;
        Event minEvent = null;
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] > 0 && counts[i] < min && varsToRemove.contains(Event.values()[i])) {
                min = counts[i];
                minEvent = Event.values()[i];
            }
        }
        return minEvent;
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
