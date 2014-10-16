package ru.bloof.ml.practice2.bayes;

import java.util.List;

/**
 * @author <a href="mailto:oleg.larionov@odnoklassniki.ru">Oleg Larionov</a>
 */
public class Message {
    private List<Long> subject, words;

    public Message(List<Long> subject, List<Long> words) {
        this.words = words;
        this.subject = subject;
    }

    public Message(List<Long> words) {
        this(null, words);
    }

    public List<Long> getSubject() {
        return subject;
    }

    public List<Long> getWords() {
        return words;
    }
}
