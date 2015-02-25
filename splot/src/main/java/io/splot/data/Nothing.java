package io.splot.data;

/** Represents an {@link Optional} with a null value.
 * Created by mako on 25.02.2015.
 */
public class Nothing<T> extends Optional<T> {
    Nothing() {
    }

    @Override public boolean isPresent() {
        return false;
    }

    @Override public T get() {
        return null;
    }

    @Override public T or(T defaultValue) {
        return defaultValue;
    }

    @Override public T orNull() {
        return null;
    }

    @Override public String toString() {
        return "Optional.Nothing";
    }
}
