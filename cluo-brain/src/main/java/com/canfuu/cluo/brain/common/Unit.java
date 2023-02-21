package com.canfuu.cluo.brain.common;

public interface Unit extends Runnable{

    String getId();

    void linkToUnit(Unit unit);

    void accept(byte value);

    void cleanValue();

    boolean isPositive();

    void wantNextUnit();

}