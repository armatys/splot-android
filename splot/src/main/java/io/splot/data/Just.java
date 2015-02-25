package io.splot.data;

/** Represents an {@link Optional} with a non-null value.
 * Created by mako on 25.02.2015.
 */
public class Just<T> extends Optional<T> {
    private final T mValue;

    Just(T value) {
        mValue = value;
    }

    @Override public boolean isPresent() {
        return true;
    }

    @Override public T get() {
        return mValue;
    }

    @Override public T or(T defaultValue) {
        return mValue;
    }

    @Override public T orNull() {
        return mValue;
    }

    @Override public String toString() {
        return "Optional.Just(" + mValue + ")";
    }
}
