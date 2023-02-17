package com.canfuu.cluo.brain.unit;

import com.canfuu.cluo.brain.signal.Signal;

import java.util.HashMap;
import java.util.Map;

public class UnitOutput {
    public UnitInput toInput;
    public Unit myUnit;
    public Unit toUnit;
    public UnitOutput(UnitInput toInput, Unit myUnit, Unit toUnit) {
        this.toInput = toInput;
        this.toInput.fromOutput = this;
        this.toUnit = toUnit;

        this.myUnit = myUnit;
        this.myUnit.outputs.add(this);
    }

    public UnitOutput(Unit myUnit) {
        this.myUnit = myUnit;
        this.myUnit.outputs.add(this);
    }

}
