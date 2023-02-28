package com.canfuu.cluo.brain.common;

import java.util.Objects;

public class Link<F,T> {
    private F from;
    private T to;

    public Link(F from, T to) {
        this.from = from;
        this.to = to;
    }

    public F getFrom() {
        return from;
    }

    public T getTo() {
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link<?, ?> link = (Link<?, ?>) o;
        return Objects.equals(from, link.from) && Objects.equals(to, link.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public String toString() {
        return from+"->"+to;
    }
}
