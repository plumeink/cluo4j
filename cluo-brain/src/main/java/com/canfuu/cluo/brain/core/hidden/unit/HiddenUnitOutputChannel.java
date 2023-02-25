package com.canfuu.cluo.brain.core.hidden.unit;

import com.canfuu.cluo.brain.common.CommonConstants;
import com.canfuu.cluo.brain.common.Unit;
import com.canfuu.cluo.brain.common.signal.Signal;
import com.canfuu.cluo.brain.common.signal.SignalFeature;
import com.canfuu.cluo.brain.common.util.TimeUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class HiddenUnitOutputChannel extends HiddenUnitChannel{

    private int SCENE_TRANS = 0;
    private int SCENE_GROW = 1;
    private int SCENE_WILT = 2;

    private final Unit unit;

    private final int transValue;
    private final SignalFeature[] signalFeatures;

    private final HiddenUnitChannel parentChannel;

    private long lastTransSecondsTimestamp = Long.MAX_VALUE;

    private final int activeValue;

    private final AtomicLong memory;

    public HiddenUnitOutputChannel(HiddenUnitChannel parentChannel,Unit unit, int activeValue, int transValue, AtomicLong memory, SignalFeature... signalFeatures) {
        this.unit = unit;
        this.signalFeatures = signalFeatures;
        this.transValue = transValue;
        this.parentChannel = parentChannel;
        this.activeValue = activeValue;
        this.memory = memory;
    }

    @Override
    public void transValue(double value, SignalFeature signalFeature) {

        memory.addAndGet(1);

        double times = value / activeValue;
        if (times < 1) {
            return;
        }

        long lastTransSecondsTimestampCopy = lastTransSecondsTimestamp;
        lastTransSecondsTimestamp = TimeUtil.currentSecondsInCache();
        long gap =lastTransSecondsTimestamp - lastTransSecondsTimestampCopy;

        if(gap>0) {
            memory.addAndGet(-1L * gap / CommonConstants.cleanOutputChannelSeconds);
        }

        if (cleanMySelf()) {
            return;
        }

        Signal signal = new Signal(transValue, signalFeatures);
        for (int i = 0; i < times; i++) {
            unit.accept(signal);
        }

    }

    @Override
    public void grow() {
        memory.addAndGet(1);
    }

    @Override
    public void wilt() {
        memory.addAndGet(-1);
        cleanMySelf();
    }

    private boolean cleanMySelf() {
        if(memory.get()<0){
            parentChannel.removeChild(this);
            return true;
        }

        return false;
    }

}
