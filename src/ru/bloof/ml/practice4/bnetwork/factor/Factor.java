package ru.bloof.ml.practice4.bnetwork.factor;

import ru.bloof.ml.practice4.bnetwork.Event;
import ru.bloof.ml.practice4.bnetwork.Evidence;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:blloof@gmail.com">Oleg Larionov</a>
 */
public class Factor {
    private Set<Event> scope;
    private Map<Set<Evidence>, Double> function;

    public Factor(Set<Event> scope) {
        this(scope, new HashMap<>());
    }

    private Factor(Set<Event> scope, Map<Set<Evidence>, Double> function) {
        this.scope = scope;
        this.function = function;
    }

    public void setValue(Set<Evidence> params, double value) {
        if (function.containsKey(params)) {
            throw new IllegalArgumentException("Already defined value for " + params);
        }
        function.put(params, value);
    }

    public boolean inScope(Event e) {
        return scope.contains(e);
    }

    public Collection<Event> getScope() {
        return scope;
    }

    public Factor reduce(Evidence e) {
        Set<Event> reducedScope = reduceScope(e.getEvent());
        Map<Set<Evidence>, Double> newFunction = function.entrySet()
                .parallelStream()
                .filter(entry -> entry.getKey().contains(e))
                .collect(Collectors.toMap(en -> {
                    HashSet<Evidence> newEvs = new HashSet<>(en.getKey());
                    newEvs.remove(e);
                    return newEvs;
                }, Map.Entry::getValue));
        return new Factor(reducedScope, newFunction);
    }

    public Factor multiply(Factor other) {
        Set<Event> newScope = new HashSet<>(scope);
        newScope.addAll(other.getScope());
        Set<Event> intersection = new HashSet<>(scope);
        intersection.retainAll(other.getScope());
        if (intersection.isEmpty()) {
            throw new IllegalArgumentException("Cannot multiply factors without intersection");
        }

        Map<Set<Evidence>, Double> newFunction = new HashMap<>();
        for (Map.Entry<Set<Evidence>, Double> entry1 : function.entrySet()) {
            for (Map.Entry<Set<Evidence>, Double> entry2 : other.values().entrySet()) {
                Set<Evidence> total = new HashSet<>(entry1.getKey());
                total.retainAll(entry2.getKey());
                boolean good = total.size() == intersection.size() && total.parallelStream().allMatch(ev -> intersection.contains(ev.getEvent()));
                if (good) {
                    Set<Evidence> newEvidences = new HashSet<>(entry1.getKey());
                    newEvidences.addAll(entry2.getKey());
                    newFunction.put(newEvidences, entry1.getValue() * entry2.getValue());
                }
            }
        }
        return new Factor(newScope, newFunction);
    }

    public Factor marginalize(Event e) {
        Set<Event> reducedScope = reduceScope(e);
        Map<Set<Evidence>, Double> newFunction = new HashMap<>();
        for (Map.Entry<Set<Evidence>, Double> entry : function.entrySet()) {
            Set<Evidence> newParam = entry.getKey()
                    .parallelStream()
                    .filter(ev -> ev.getEvent() != e)
                    .collect(Collectors.toSet());
            newFunction.compute(newParam, (ev, cur) -> cur == null ? entry.getValue() : cur + entry.getValue());
        }
        return new Factor(reducedScope, newFunction);
    }

    public Double value(Set<Evidence> evs) {
        if (scope.isEmpty()) {
            return 1.;
        }
        for (Evidence ev : evs) {
            if (!scope.contains(ev.getEvent())) {
                throw new IllegalArgumentException("Factor has different scope");
            }
        }

        return function.get(evs);
    }

    public void normalize() {
        double normConst = function.values().parallelStream().mapToDouble(a -> a).sum();
        function.replaceAll((k, v) -> v / normConst);
    }

    public Map<Set<Evidence>, Double> values() {
        return function;
    }

    private Set<Event> reduceScope(Event e) {
        Set<Event> reducedScope = new HashSet<>(scope);
        reducedScope.remove(e);
        return reducedScope;
    }

    public void print(OutputStream os) {
        try (PrintWriter pw = new PrintWriter(os)) {
            String s = scope.parallelStream().map(Enum::toString).collect(Collectors.joining(",", "Scope=", ""));
            pw.println(s);

            for (Map.Entry<Set<Evidence>, Double> entry : function.entrySet()) {
                pw.println(entry.getKey().parallelStream().map(Evidence::toString).collect(Collectors.joining(",")) + "->" + entry.getValue());
            }
        }
    }
}
