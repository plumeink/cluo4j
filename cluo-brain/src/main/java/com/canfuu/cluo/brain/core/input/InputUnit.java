package com.canfuu.cluo.brain.core.input;

import com.canfuu.cluo.brain.common.CommonEntity;
import com.canfuu.cluo.brain.common.Unit;

public class InputUnit  extends CommonEntity implements Unit {
    private Unit nextUnit;
    @Override
    public void linkToUnit(Unit unit) {
        nextUnit = unit;
    }

    @Override
    public void accept(byte value) {
        nextUnit.accept(value);
    }

    @Override
    public void cleanValue() {

    }

    @Override
    public boolean isPositive() {
        return true;
    }

    @Override
    public void wantNextUnit() {

    }

    @Override
    public void run() {

    }
}
