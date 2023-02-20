package com.canfuu.cluo.brain.unit;

import com.canfuu.cluo.brain.common.CommonEntity;
import com.canfuu.cluo.brain.signal.InnerSignal;
import com.canfuu.cluo.brain.signal.OuterSignal;
import com.canfuu.cluo.brain.signal.TransportOuterSignal;

public class HiddenUnitOutput  extends CommonEntity {

    //value向下传递的阈值
    private int valueThreshold = 1;


    // 每次向后传递的value数量
    private byte transValue = 3;

    public HiddenUnitOutput() {
    }

    int accept(int value, Unit unit) {

        int returnValue = 0;

        if (value >= valueThreshold) {
            returnValue = value - valueThreshold;

            unit.accept(transValue);
        }

        return returnValue;
    }
}