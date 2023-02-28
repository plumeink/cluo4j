package com.canfuu.cluo.brain.common;

import com.canfuu.cluo.brain.common.signal.Signal;

public interface Unit {

    void accept(Signal value);

    UnitGroup group();

    public String getId();


}