package com.canfuu.cluo.brain.common.signal;

import com.canfuu.cluo.brain.common.CommonEntity;

public class Signal extends CommonEntity {

    private byte value = 0;

    public byte getValue() {
        return value;
    }

    public void setValue(byte value) {
        this.value = value;
    }
}
