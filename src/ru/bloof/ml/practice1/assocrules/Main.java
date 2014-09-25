package ru.bloof.ml.practice1.assocrules;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.util.*;

/**
 * @author Oleg Larionov
 */
public class Main {
    public static final String FILENAME = "supermarket.arff";
    public static final double MIN_SUPPORT = 0.05;
    public static final double MIN_CONF = 0.45;
    private static final int MERGE_ATTR = 1;

    public static void main(String[] args) throws Exception {
        ConverterUtils.DataSource source = new ConverterUtils.DataSource(FILENAME);
        Instances data = source.getDataSet();
        if (data.classIndex() == -1) {
            data.setClassIndex(data.numAttributes() - 1);
        }
        int instances = data.numInstances();
        Map<Long, Bucket> buckets = new HashMap<>();
        for (int i = 0; i < instances; i++) {
            Instance instance = data.instance(i);
            long bucketId = Long.parseLong(instance.toString(3));
            get(buckets, bucketId).add((int) instance.value(MERGE_ATTR));
        }
        int[] counts = new int[data.attribute(MERGE_ATTR).numValues()];
        System.out.println(buckets.size());
        System.out.println(counts.length);
        buckets.values().stream().flatMapToInt(bucket -> bucket.getSet().stream()).forEachOrdered(integer -> {
            counts[integer]++;
        });
        Map<BitSet, Double> g1 = new HashMap<>();
        for (int i = 0; i < counts.length; i++) {
            double value = counts[i] * 1. / buckets.size();
            if (value >= MIN_SUPPORT) {
                BitSet set = new BitSet();
                set.set(i);
                g1.put(set, value);
            }
        }
        System.out.println(g1.size());

        Set<BitSet> prevg = g1.keySet();
        List<Set<BitSet>> g = new ArrayList<>();
        for (int i = 2; i < counts.length; i++) {
            Set<BitSet> gi = new HashSet<>();
            for (BitSet entry : prevg) {
                for (Map.Entry<BitSet, Double> g1Entry : g1.entrySet()) {
                    int index = g1Entry.getKey().nextSetBit(0);
                    if (!entry.get(index)) {
                        BitSet candidate = (BitSet) entry.clone();
                        candidate.set(index);
                        gi.add(candidate);
                    }
                }
            }
            final Set<BitSet> checkPrevg = prevg;
            gi.removeIf(candidate -> {
                BitSet check = (BitSet) candidate.clone();
                int bit = 0;
                while ((bit = check.nextSetBit(bit)) != -1) {
                    check.clear(bit);
                    if (!checkPrevg.contains(check)) {
                        return true;
                    }
                    check.set(bit);
                    bit++;
                }
                double value = count(buckets, candidate) * 1. / buckets.size();
                return value < MIN_SUPPORT;
            });
            if (gi.isEmpty()) {
                break;
            }
            g.add(gi);
            prevg = gi;
        }
        System.out.println(g);

        List<Rule> rules = new ArrayList<>();
        for (Set<BitSet> gi : g) {
            for (BitSet x : gi) {
                findRules(rules, buckets, x, new BitSet());
            }
        }
        System.out.println(rules);
    }

    private static void findRules(List<Rule> rules, Map<Long, Bucket> buckets, BitSet x, BitSet y) {
        int setBit;
        int maxYBit = y.length();
        while ((setBit = x.nextSetBit(maxYBit)) != -1) {
            BitSet _x = (BitSet) x.clone(), _y = (BitSet) y.clone();
            _x.clear(setBit);
            _y.set(setBit);

            BitSet sum = (BitSet) _x.clone();
            sum.or(_y);
            double support = count(buckets, sum) * 1. / buckets.size();
            double confidence = count(buckets, _x) * 1. / buckets.size();
            confidence = support / confidence;
            if (confidence >= MIN_CONF) {
                rules.add(new Rule(_x, _y, support, confidence));
            }
            if (_x.cardinality() > 1) {
                findRules(rules, buckets, _x, _y);
            }
            maxYBit = setBit + 1;
        }
    }

    private static int count(Map<Long, Bucket> map, BitSet set) {
        return (int) map.values().parallelStream().filter(bucket -> {
            BitSet s = bucket.getSet();
            s.and(set);
            return set.equals(s);
        }).count();
    }

    private static Bucket get(Map<Long, Bucket> map, Long key) {
        if (!map.containsKey(key)) {
            map.put(key, new Bucket(key));
        }
        return map.get(key);
    }
}
