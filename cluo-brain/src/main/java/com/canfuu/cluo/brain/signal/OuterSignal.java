package com.canfuu.cluo.brain.signal;

import com.canfuu.cluo.brain.common.CommonEntity;

public class OuterSignal  extends CommonEntity {
    private boolean positive = true;



    public boolean isPositive() {
        return positive;
    }

    public void setPositive(boolean positive) {
        this.positive = positive;
    }
}
