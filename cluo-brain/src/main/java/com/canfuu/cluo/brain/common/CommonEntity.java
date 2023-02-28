package com.canfuu.cluo.brain.common;


import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class CommonEntity {
    private final String id = UUID.randomUUID().toString();

    private final String split1 = id.substring(0,2);
    private final String split2 = id.substring(2,4);
    private final String split3 = id.substring(4,6);
    private final String split4 = id.substring(6,8);


    public String getId() {
        return id;
    }

    public String getSplit1() {
        return split1;
    }

    public String getSplit2() {
        return split2;
    }

    public String getSplit3() {
        return split3;
    }

    public String getSplit4() {
        return split4;
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
