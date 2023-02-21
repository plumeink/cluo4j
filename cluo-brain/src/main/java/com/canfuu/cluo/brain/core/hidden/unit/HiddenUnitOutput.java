package com.canfuu.cluo.brain.core.hidden.unit;

import com.canfuu.cluo.brain.common.CommonEntity;
import com.canfuu.cluo.brain.common.Unit;

public class HiddenUnitOutput  extends CommonEntity {

    //value向下传递的阈值
    private int valueThreshold = 1;


    // 每次向后传递的value数量
    private byte transValue = 3;

    private Unit myUnit;

    public HiddenUnitOutput(Unit myUnit) {
        this.myUnit = myUnit;
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