package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;

import java.io.Serializable;
import java.util.Objects;

public class ObjectObjectImmutablePair<K, V> implements Pair<K, V>, Serializable {
    private static final long serialVersionUID = 0L;
    protected final K left;
    protected final V right;

    public ObjectObjectImmutablePair(K left, V right) {
        this.left = left;
        this.right = right;
    }

    public static <K, V> ObjectObjectImmutablePair<K, V> of(K left, V right) {
        return new ObjectObjectImmutablePair<K, V>(left, right);
    }

    public K left() {
        return this.left;
    }

    public V right() {
        return this.right;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (!(other instanceof Pair)) {
            return false;
        } else {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
    }

    public int hashCode() {
        return (this.left == null ? 0 : this.left.hashCode()) * 19 + (this.right == null ? 0 : this.right.hashCode());
    }

    public String toString() {
        return "<" + this.left() + "," + this.right() + ">";
    }
}
