package com.canfuu.cluo.brain.core.hidden.unit;

import com.canfuu.cluo.brain.common.CommonEntity;
import com.canfuu.cluo.brain.common.Unit;

import java.util.Random;

public class HiddenUnitOutput  extends CommonEntity {

    //value向下传递的阈值
    private int valueThreshold = -1024;


    // 每次向后传递的value数量
    private byte transValue;

    private Unit myUnit;

    public HiddenUnitOutput(Unit myUnit) {
        this.myUnit = myUnit;
        transValue = ((Integer)(new Random().nextInt(128)-256)).byteValue();
    }

    int accept(int value, Unit unit) {

        int returnValue = 0;
        if(valueThreshold == -1024){
            valueThreshold = value;
        }
        if (value >= valueThreshold) {
            returnValue = value - valueThreshold;

            unit.accept(transValue);
        }

        return returnValue;
    }
}