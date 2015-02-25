package io.splot.data;

/** Optional values.
 * Created by mako on 25.02.2015.
 */
public abstract class Optional<T> {
    public static <T> Optional<T> nothing() {
        return new Nothing<T>();
    }

    public static <T> Optional<T> just(T value) {
        if (value == null) {
            throw new NullPointerException("Cannot create a Just value with null pointer.");
        }
        return new Just<T>(value);
    }

    public static <T> Optional<T> fromNullable(T valueOrNull) {
        return valueOrNull == null ? new Nothing<T>() : new Just<T>(valueOrNull);
    }
    
    public abstract boolean isPresent();
    public abstract T get();
    public abstract T or(T defaultValue);
    public abstract T orNull();
    @Override public abstract String toString();
}
