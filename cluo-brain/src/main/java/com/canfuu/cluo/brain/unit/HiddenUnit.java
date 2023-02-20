package com.canfuu.cluo.brain.unit;

import com.canfuu.cluo.brain.common.CommonEntity;
import com.canfuu.cluo.brain.util.TimeUtil;

/**
 * 神经元
 */
public class HiddenUnit extends CommonEntity implements Unit {

    private HiddenUnitOutputGroup outputGroup = new HiddenUnitOutputGroup();

    private int valueThreshold = 5;
    private int savedValue = 0;
    private int decrementValuePerSeconds = 1;
    private int previousAcceptTime = -1;

    private int defaultValue = 0;


    public void linkToUnit(Unit unit) {
        outputGroup.linkToUnit(unit);
    }

    @Override
    public void accept(int b) {
        int nowValue = savedValue + b;
        if (nowValue > valueThreshold) {
            value(valueThreshold);
            outputGroup.transValue(nowValue - valueThreshold);
        } else {
            value(nowValue);
        }

        previousAcceptTime = TimeUtil.currentSeconds();
    }

    public void cleanValue() {
        int currentSeconds = TimeUtil.currentSeconds();
        int gapSeconds = currentSeconds - previousAcceptTime;
        int gapValue = gapSeconds * decrementValuePerSeconds;
        if (gapValue >= savedValue) {
            value(0);
        } else {
            value(savedValue - gapValue);
        }
        outputGroup.cleanOutput(gapSeconds);
    }


    private void value(int value) {
        this.savedValue = value;
    }

    public int value() {
        return savedValue;
    }
}