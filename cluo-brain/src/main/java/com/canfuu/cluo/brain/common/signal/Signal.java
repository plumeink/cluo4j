package com.canfuu.cluo.brain.common.signal;

import com.canfuu.cluo.brain.common.CommonEntity;

import java.util.Arrays;
import java.util.HashSet;

public class Signal extends CommonEntity {

    /**
     * 每次触发多少信息
     */
    private final double value;
    private final SignalFeature feature;

    private boolean stop = false;

    public Signal(double value, SignalFeature feature) {
        this.value = value;
        this.feature =feature;
    }

    public boolean isStop() {
        return stop;
    }

    public void stop() {
        this.stop = true;
    }

    public SignalFeature getFeature() {
        return feature;
    }

    public double value() {
        return value;
    }

    public boolean isAxonGrow(){
        return feature.equals(SignalFeature.AXON_GROW);
    }

    @Override
    public String toString() {
        return feature+"-"+value+ " isStop:"+stop;
    }

}
