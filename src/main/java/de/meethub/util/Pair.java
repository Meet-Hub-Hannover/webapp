package de.meethub.util;

public class Pair<T1, T2> {

    private final T1 value1;
    private final T2 value2;

    public Pair(final T1 x, final T2 y) {
        this.value1 = x;
        this.value2 = y;
    }

    public static<X, Y> Pair<X, Y> create(final X x, final Y y) {
        return new Pair<X, Y>(x, y);
    }

    public T1 getFirst() {
        return this.value1;
    }

    public T2 getSecond() {
        return this.value2;
    }

}
