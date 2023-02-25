package com.canfuu.cluo.brain.common.signal;

import com.canfuu.cluo.brain.common.CommonEntity;

import java.util.Arrays;
import java.util.HashSet;

public class Signal extends CommonEntity {

    private int value;
    private HashSet<SignalFeature> features;

    public Signal(int value, SignalFeature... features) {
        this.value = value;
        this.features =new HashSet<>();
        this.features.addAll(Arrays.asList(features));
    }

    public int value() {
        return value;
    }

    public HashSet<SignalFeature> feature() {
        return features;
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

    public boolean isExcitation(){
        return features.contains(SignalFeature.EXCITATION);
    }
    public boolean isInhibition(){
        return features.contains(SignalFeature.INHIBITION);
    }
    public boolean isAxonGrow(){
        return features.contains(SignalFeature.AXON_GROW);
    }
    public boolean isAxonWilt(){
        return features.contains(SignalFeature.AXON_WILT);
    }
}
