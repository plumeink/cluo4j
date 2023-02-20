package com.canfuu.cluo.brain.common;


import java.util.Objects;
import java.util.UUID;

public class CommonEntity {
    private final String id = UUID.randomUUID().toString();

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommonEntity that = (CommonEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
