package core.util;

import java.io.Serializable;
import java.util.Objects;

public class Pair<K, V> implements Serializable {
    private K first;
    private V second;

    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }

    public K getFirst() {
        return first;
    }

    public V getSecond() {
        return second;
    }

    @Override
    public int hashCode() {
        return (first == null ? 0 : first.hashCode()) ^ (second == null ? 0 : second.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Pair)) {
            return false;
        }

        Pair<?, ?> p = (Pair<?, ?>) obj;
        return Objects.equals(p.first, first) && Objects.equals(p.second, second);
    }

    public static <X, Y> Pair<X, Y> create(X x, Y y) {
        return new Pair<>(x, y);
    }
}
