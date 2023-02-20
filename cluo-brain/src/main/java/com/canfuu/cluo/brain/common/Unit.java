package com.canfuu.cluo.brain.common;

public interface Unit extends Runnable{
    void accept(byte value);

    void cleanValue();
}