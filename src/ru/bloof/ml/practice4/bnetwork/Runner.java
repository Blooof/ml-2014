package ru.bloof.ml.practice4.bnetwork;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * @author <a href="mailto:blloof@gmail.com">Oleg Larionov</a>
 */
public class Runner {
    public static void main(String[] args) throws Exception {
        Map<Event, Set<Event>> graph = new HashMap<>();
        Map<Pair<Event, Set<Evidence>>, Double> probs = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("data/bayesian_network/bayesian_network.txt"))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null && !StringUtils.isBlank(line)) {
                StringTokenizer st = new StringTokenizer(line, " :");
                Event e = getEvent(st.nextToken());
                double prob = Double.parseDouble(st.nextToken());
                checkProb(e, prob);
                probs.put(Pair.of(e, null), prob);
            }

            while ((line = reader.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, " :");
                Event b = getEvent(st.nextToken());
                while (st.hasMoreTokens()) {
                    Event e = getEvent(st.nextToken());
                    graph.computeIfAbsent(e, k -> new HashSet<>()).add(b);
                }

                Set<Event> events = graph.get(b);
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
                                Pair<Event, Set<Evidence>> key = Pair.of(b, evidences);
                                if (probs.containsKey(key)) {
                                    throw new IllegalArgumentException("Duplicate rows for event " + b);
                                }
                                probs.put(key, prob);
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
    }

    private static void checkProb(Event b, double prob) {
        if (prob < 0 || prob > 1) {
            throw new IllegalArgumentException("Bad prob for event " + b);
        }
    }

    private static Event getEvent(String token) {
        return Event.values()[Integer.parseInt(token)];
    }
}
