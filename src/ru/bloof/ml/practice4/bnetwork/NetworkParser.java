package ru.bloof.ml.practice4.bnetwork;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import ru.bloof.ml.practice4.bnetwork.factor.Factor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:blloof@gmail.com">Oleg Larionov</a>
 */
public class NetworkParser {
    private static BayesNetwork createNetwork(Map<Event, List<Pair<Set<Evidence>, Double>>> probs) {
        List<Factor> factors = new ArrayList<>();
        for (Map.Entry<Event, List<Pair<Set<Evidence>, Double>>> entry : probs.entrySet()) {
            Set<Event> scope = new HashSet<>();
            scope.add(entry.getKey());
            Set<Evidence> left = entry.getValue().get(0).getLeft();
            if (left != null) {
                scope.addAll(left.parallelStream().map(Evidence::getEvent).collect(Collectors.toList()));
            }
            Factor factor = new Factor(scope);
            for (Pair<Set<Evidence>, Double> p : entry.getValue()) {
                Set<Evidence> evidences = new HashSet<>();
                if (p.getLeft() != null) {
                    evidences.addAll(p.getLeft());
                }
                Set<Evidence> trueValue = new HashSet<>(evidences);
                trueValue.add(new Evidence(entry.getKey(), true));
                factor.setValue(trueValue, p.getRight());
                evidences.add(new Evidence(entry.getKey(), false));
                factor.setValue(evidences, 1. - p.getRight());
            }
            factors.add(factor);
        }
        return new BayesNetwork(factors);
    }

    private static boolean checkDAG(Map<Event, Set<Event>> graph) {
        int[] visited = new int[Event.values().length];
        for (int i = 0; i < visited.length; i++) {
            if (visited[i] == 0) {
                if (!dfs(graph, i, visited)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean dfs(Map<Event, Set<Event>> graph, int i, int[] visited) {
        visited[i] = 1;
        Set<Event> out = graph.get(Event.values()[i]);
        if (out != null) {
            for (Event e : out) {
                int j = e.ordinal();
                if (visited[j] == 1) {
                    return false;
                } else if (visited[j] == 0) {
                    dfs(graph, j, visited);
                }
            }
        }
        visited[i] = 2;
        return true;
    }

    private static void checkProb(Event b, double prob) {
        if (prob < 0 || prob > 1) {
            throw new IllegalArgumentException("Bad prob for event " + b);
        }
    }

    private static Event getEvent(String token) {
        return Event.values()[Integer.parseInt(token)];
    }

    public static BayesNetwork parse(String fileName) throws IOException {
        Map<Event, Set<Event>> revGraph = new HashMap<>(), graph = new HashMap<>();
        Map<Event, List<Pair<Set<Evidence>, Double>>> probs = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null && !StringUtils.isBlank(line)) {
                StringTokenizer st = new StringTokenizer(line, " :");
                Event e = getEvent(st.nextToken());
                double prob = Double.parseDouble(st.nextToken());
                checkProb(e, prob);
                probs.computeIfAbsent(e, t -> new ArrayList<>()).add(Pair.of(null, prob));
            }

            while ((line = reader.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, " :");
                Event b = getEvent(st.nextToken());
                while (st.hasMoreTokens()) {
                    Event e = getEvent(st.nextToken());
                    graph.computeIfAbsent(b, k -> new HashSet<>()).add(e);
                    revGraph.computeIfAbsent(e, k -> new HashSet<>()).add(b);
                }

                Set<Event> events = revGraph.get(b);
                int rows = 0;
                while ((line = reader.readLine()) != null) {
                    if (!"end".equals(line)) {
                        st = new StringTokenizer(line, " ");
                        Set<Evidence> evidences = new HashSet<>();
                        String token;
                        while (st.hasMoreTokens()) {
                            token = st.nextToken();
                            if (!":".equals(token)) {
                                Evidence ev;
                                if (!token.startsWith("!")) {
                                    ev = new Evidence(getEvent(token), true);
                                } else {
                                    ev = new Evidence(getEvent(token.substring(1)), false);
                                }
                                if (events == null || !events.contains(ev.getEvent())) {
                                    throw new IllegalArgumentException("Bad event " + ev.getEvent() + " for event " + b);
                                }
                                if (evidences.contains(ev)) {
                                    throw new IllegalArgumentException("Duplicate evidences in file");
                                }
                                evidences.add(ev);
                            } else {
                                if (evidences.isEmpty()) {
                                    throw new IllegalArgumentException("No evidences in file");
                                }
                                double prob = Double.parseDouble(st.nextToken());
                                checkProb(b, prob);
                                List<Pair<Set<Evidence>, Double>> list = probs.computeIfAbsent(b, t -> new ArrayList<>());
                                if (list.parallelStream().anyMatch(p -> p.getLeft().equals(evidences))) {
                                    throw new IllegalArgumentException("Duplicate rows for event " + b);
                                }
                                list.add(Pair.of(evidences, prob));
                                ++rows;
                            }
                        }
                    } else {
                        if (events != null && events.size() > 0) {
                            if (rows != 1 << events.size()) {
                                throw new IllegalArgumentException("Bad CPD for event " + b);
                            }
                        }
                        break;
                    }
                }
            }
        }

        if (!checkDAG(graph)) {
            throw new IllegalArgumentException("Graph is not DAG");
        }

        // print graph to file
        try (PrintWriter pw = new PrintWriter("graph.dot")) {
            GraphPrinter.print(graph, pw);
        }
        Runtime.getRuntime().exec("/usr/local/bin/dot -Tpng -ograph.png graph.dot");

        return createNetwork(probs);
    }
}
