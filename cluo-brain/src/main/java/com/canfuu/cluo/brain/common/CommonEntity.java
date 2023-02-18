package com.canfuu.cluo.brain.common;


import java.util.UUID;

public class CommonEntity {
    private final String id = UUID.randomUUID().toString();

    public String getId() {
        return id;
    }
}
