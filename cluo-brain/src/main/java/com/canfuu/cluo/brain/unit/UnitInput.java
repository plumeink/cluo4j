package com.canfuu.cluo.brain.unit;

import com.canfuu.cluo.brain.signal.Signal;

import java.util.List;

public class UnitInput {
    public Unit myUnit;
    public Unit fromUnit;
    public UnitOutput fromOutput;

    public UnitInput(Unit myUnit){
        this.myUnit=myUnit;
    }

}
