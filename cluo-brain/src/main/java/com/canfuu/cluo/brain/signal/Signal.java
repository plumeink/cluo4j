package com.canfuu.cluo.brain.signal;

import com.canfuu.cluo.brain.unit.Unit;

import java.util.List;

public class Signal {
    public byte data;

    public Unit unit;

    public Signal previousSignal;

    public void feedback() {
    }
}
