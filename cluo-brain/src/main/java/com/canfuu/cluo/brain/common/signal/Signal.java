package com.canfuu.cluo.brain.common.signal;

import com.canfuu.cluo.brain.common.CommonEntity;

import java.util.Arrays;
import java.util.HashSet;

public class Signal extends CommonEntity {

    private final int value;
    private final HashSet<SignalFeature> features;

    public Signal(int value, SignalFeature... features) {
        this.value = value;
        this.features =new HashSet<>();
        this.features.addAll(Arrays.asList(features));
    }

    public Signal(Signal signal) {
        this(signal.value, signal.features.toArray(new SignalFeature[0]));
    }

    public int value() {
        return value;
    }

    public SignalFeature getAxonFeature() {
        if (isAxonGrow()) {
            return SignalFeature.AXON_GROW;
        }
        if(isAxonWilt()){
            return SignalFeature.AXON_WILT;
        }

        return null;
    }

    public HashSet<SignalFeature> getFeatures() {
        return features;
    }

    public boolean isExcitation(){
        return features.contains(SignalFeature.EXCITATION);
    }

    public boolean isAxonGrow(){
        return features.contains(SignalFeature.AXON_GROW);
    }
    public boolean isAxonWilt(){
        return features.contains(SignalFeature.AXON_WILT);
    }

    @Override
    public String toString() {
        return "Signal{" +
                "value=" + value +
                ", features=" + features +
                '}';
    }

}
