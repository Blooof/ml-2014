package ru.bloof.ml.practice2.bayes;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.mutable.MutableDouble;

import static java.lang.Math.log;

/**
 * @author <a href="mailto:oleg.larionov@odnoklassniki.ru">Oleg Larionov</a>
 */
public class BayesSpamClassifier {
    public static final double PROB_LIMIT = 0.8;
    private Map<Long, Long> good, bad;
    private long badMsgCount, goodMsgCount, totalMsgCount;

    public BayesSpamClassifier() {
        good = new HashMap<>();
        bad = new HashMap<>();
    }

    public void teachSpam(Message msg) {
        teach(msg, bad);
        badMsgCount++;
    }

    public void teachNotSpam(Message msg) {
        teach(msg, good);
        goodMsgCount++;
    }

    private void teach(Message msg, Map<Long, Long> map) {
        msg.getWords().parallelStream().distinct().forEachOrdered(word -> {
            map.compute(word, (w, count) -> count == null ? 1 : count + 1);
        });
        totalMsgCount++;
    }

    public boolean isSpam(Message msg) {
        MutableDouble eta = new MutableDouble();
        msg.getWords().parallelStream().distinct().forEachOrdered(word -> {
            double denom = (probWordBad(word) * badMsgCount / totalMsgCount) +
                    (probWordGood(word) * goodMsgCount / totalMsgCount);
            if (denom > 0) {
                double p = (probWordBad(word) * badMsgCount / totalMsgCount) / denom;
                double log = log(1 - p) - log(p);
                log = Math.max(Math.min(log, 1000), -1000);
                eta.add(log);
            }
        });
        double p = 1. / (1. + Math.exp(eta.doubleValue()));
        return p >= PROB_LIMIT;
    }

    private double probWordGood(Long word) {
        return 1. * good.getOrDefault(word, 0l) / goodMsgCount;
    }

    private double probWordBad(Long word) {
        return 1. * bad.getOrDefault(word, 0l) / badMsgCount;
    }
}
