package ru.bloof.ml.practice2.bayes;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.mutable.MutableInt;

/**
 * @author <a href="mailto:oleg.larionov@odnoklassniki.ru">Oleg Larionov</a>
 */
public class Runner {
    public static final String DATA_DIR = "practice_2/bayes/Bayes/pu1/";
    private static final String SPAM = "spmsg";

    public static void main(String[] args) throws Exception {
        double precision = 0, recall = 0;
        String testDir;
        for (int i = 1; i <= 10; i++) {
            testDir = "part" + i;
            BayesSpamClassifier classifier = new BayesSpamClassifier();
            Path dataDir = Paths.get(DATA_DIR);
            DirectoryStream<Path> partDirsStream = Files.newDirectoryStream(dataDir);
            for (Path part : partDirsStream) {
                if (!part.toString().endsWith(testDir)) {
                    Files.walk(part).filter(file -> !Files.isDirectory(file)).forEachOrdered(path -> {
                        Message msg = createMessage(path);
                        if (path.toString().contains(SPAM)) {
                            classifier.teachSpam(msg);
                        } else {
                            classifier.teachNotSpam(msg);
                        }
                    });
                }
            }

            Path testDirPath = Paths.get(DATA_DIR, testDir);
            MutableInt totalSelect = new MutableInt(), tp = new MutableInt(), totalSpam = new MutableInt();
            Files.walk(testDirPath).filter(file -> !Files.isDirectory(file)).forEachOrdered(path -> {
                Message msg = createMessage(path);
                boolean result = classifier.isSpam(msg);
                if (result) {
                    totalSelect.increment();
                    if (path.toString().contains(SPAM)) {
                        tp.increment();
                    }
                }
                if (path.toString().contains(SPAM)) {
                    totalSpam.increment();
                }
            });
            precision += tp.doubleValue() / totalSelect.doubleValue();
            recall += tp.doubleValue() / totalSpam.doubleValue();
        }
        System.out.println(String.format("Result : precision=%f, recall=%f", precision / 10, recall / 10));
    }

    private static Message createMessage(Path path) {
        try {
            return new Message(Arrays.asList(Files.readAllLines(path)
                    .get(2)
                    .split(" ")).parallelStream().map(Long::parseLong).collect(Collectors
                    .toList()));
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
