package com.canfuu.cluo.brain.core.hidden.unit;

import com.canfuu.cluo.brain.common.CommonEntity;
import com.canfuu.cluo.brain.common.Unit;
import com.canfuu.cluo.brain.common.util.TimeUtil;
import com.canfuu.cluo.brain.core.factory.HiddenUnitFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 神经元
 */
public class HiddenUnit extends CommonEntity implements Unit {

    private HiddenUnitOutputGroup outputGroup = new HiddenUnitOutputGroup(this);

    private int valueThreshold = 1280;
    private AtomicInteger savedValue = new AtomicInteger(0);
    private int decrementValuePerSeconds = 1;
    private int previousAcceptTime = -1;

    private boolean positive = true;

    private final HiddenUnitFactory factory;

    public HiddenUnit(HiddenUnitFactory factory, boolean positive){
        this.factory = factory;
        this.positive = positive;
    }

    @Override
    public void linkToUnit(Unit unit) {
        System.out.println(getId() +" next is "+unit.getId());
        outputGroup.linkToUnit(unit);
    }

    @Override
    public void accept(byte b) {
        addValue(b);
        previousAcceptTime = TimeUtil.currentSeconds();
    }

    @Override
    public void run() {
        // TODO: 2023/2/21 最好执行前判断一下是否在clean，如果clean可以慢点执行
        int value = savedValue.get();
        if (value > valueThreshold) {
            addValue(-1 * valueThreshold);
            outputGroup.transValue(value - valueThreshold);
        }
    }

    @Override
    public void cleanValue() {
        int currentSeconds = TimeUtil.currentSeconds();
        int gapSeconds = previousAcceptTime - currentSeconds;
        int gapValue = gapSeconds * decrementValuePerSeconds;
        addValue(gapValue);
        outputGroup.cleanOutput();
    }
    @Override
    public boolean isPositive(){
        return positive;
    }

    @Override
    public void wantNextUnit() {
        Unit nextUnit = factory.wantUnit(this);
        if(nextUnit !=null){
            System.out.println(getId() + " req a next "+nextUnit.getId());
            outputGroup.cleanOutput();
            outputGroup = new HiddenUnitOutputGroup(this);
            outputGroup.linkToUnit(nextUnit);
        }
    }

    private void addValue(int value) {
        this.savedValue.addAndGet(value);
    }



}