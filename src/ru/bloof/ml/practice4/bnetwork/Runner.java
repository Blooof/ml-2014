package ru.bloof.ml.practice4.bnetwork;

import ru.bloof.ml.practice4.bnetwork.factor.Factor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @author <a href="mailto:blloof@gmail.com">Oleg Larionov</a>
 */
public class Runner {
    public static final String FILENAME = "data/bayesian_network/bayesian_network.txt";

    public static void main(String[] args) throws Exception {
        BayesNetwork network = NetworkParser.parse(FILENAME);
        run(network);
    }

    private static void run(BayesNetwork network) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter pw = new PrintWriter(System.out)) {
            String line;
            while ((line = reader.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line);
                Set<Event> events = new HashSet<>();
                Set<Evidence> evidences = new HashSet<>();
                boolean eventsNow = true;
                while (st.hasMoreTokens()) {
                    String token = st.nextToken();
                    if (!"|".equals(token)) {
                        if (eventsNow) {
                            Event e = Event.values()[Integer.parseInt(token)];
                            events.add(e);
                        } else {
                            Evidence ev;
                            if (token.startsWith("!")) {
                                ev = new Evidence(Event.values()[Integer.parseInt(token.substring(1))], false);
                            } else {
                                ev = new Evidence(Event.values()[Integer.parseInt(token)], true);
                            }
                            evidences.add(ev);
                        }
                    } else {
                        eventsNow = false;
                    }
                }
                if (events.isEmpty()) {
                    System.out.println("Events cannot be empty");
                    continue;
                }
                Factor f = network.eliminate(events, evidences);
                f.print(pw);
            }
        }
    }
}
