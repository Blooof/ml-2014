package ru.bloof.ml.practice4.bnetwork;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:oleg.larionov@odnoklassniki.ru">Oleg Larionov</a>
 */
public class GraphPrinter {
    public static void print(Map<Event, Set<Event>> graph, PrintWriter pw) {
        pw.println("digraph BNetwork {");
        for (Map.Entry<Event, Set<Event>> entry : graph.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                for (Event e : entry.getValue()) {
                    pw.println(entry.getKey() + " -> " + e + ";");
                }
            }
        }
        pw.println("}");
    }
}
