package ru.bloof.ml.practice4.bnetwork;

import java.util.Objects;

/**
 * @author <a href="mailto:blloof@gmail.com">Oleg Larionov</a>
 */
public class Evidence {
    private Event event;
    private boolean value;

    public Evidence(Event event, boolean value) {
        this.event = event;
        this.value = value;
    }

    public Event getEvent() {
        return event;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(event, value);
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Evidence other = (Evidence) obj;
        return Objects.equals(this.event, other.event) && Objects.equals(this.value, other.value);
    }

    @Override
    public String toString() {
        return "Evidence[" + event + "=" + value + "]";
    }
}
