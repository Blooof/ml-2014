package ru.bloof.ml.practice4.bnetwork;

import ru.bloof.ml.practice4.bnetwork.factor.Factor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:blloof@gmail.com">Oleg Larionov</a>
 */
public class Runner {
    public static final String FILENAME = "data/bayesian_network.txt";

    public static void main(String[] args) throws Exception {
        BayesNetwork network = NetworkParser.parse(FILENAME);
        Set<Event> scope = new HashSet<>();
        Collections.addAll(scope, Event.Lightning);
        Set<Evidence> evidences = new HashSet<>();
        Collections.addAll(evidences, new Evidence(Event.Storm, true));
        Factor f = network.eliminate(scope, evidences);
        f.print(System.out);
    }
}
