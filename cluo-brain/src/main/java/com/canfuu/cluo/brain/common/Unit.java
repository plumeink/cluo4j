package com.canfuu.cluo.brain.common;

import com.canfuu.cluo.brain.common.signal.Signal;
import com.canfuu.cluo.brain.core.hidden.group.HiddenUnitGroup;

public interface Unit {

    void accept(Signal value);

    UnitGroup group();

    public String getId();


}