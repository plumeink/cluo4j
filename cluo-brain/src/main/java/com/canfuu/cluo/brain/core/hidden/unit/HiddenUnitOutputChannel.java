package com.canfuu.cluo.brain.core.hidden.unit;

import com.canfuu.cluo.brain.common.CommonConstants;
import com.canfuu.cluo.brain.common.Unit;
import com.canfuu.cluo.brain.common.signal.Signal;
import com.canfuu.cluo.brain.common.signal.SignalFeature;
import com.canfuu.cluo.brain.common.util.TimeUtil;

import java.util.concurrent.atomic.AtomicInteger;

public class HiddenUnitOutputChannel extends HiddenUnitChannel{

    private final Unit unit;

    private final int transValue;
    private final SignalFeature[] signalFeatures;

    private final HiddenUnitChannel parentChannel;

    private long lastTransSecondsTimestamp = 0L;

    private AtomicInteger growTag = new AtomicInteger(0);

    private final int activeValue;

    public HiddenUnitOutputChannel(HiddenUnitChannel parentChannel,Unit unit, int activeValue, int transValue, SignalFeature... signalFeatures) {
        this.unit = unit;
        this.signalFeatures = signalFeatures;
        this.transValue = transValue;
        this.parentChannel = parentChannel;
        this.activeValue = activeValue;

    }

    @Override
    public void transValue(double value, SignalFeature signalFeature) {

        cleanMySelf();

        double times = value / activeValue;
        if(times<=1){
            return;
        }

        lastTransSecondsTimestamp = TimeUtil.currentSecondsInCache();

        Signal signal = new Signal(transValue, signalFeatures);
        for (int i = 0; i < times; i++) {
            unit.accept(signal);
        }

    }

    @Override
    public void grow() {
        // 如果生长的次数达到了一定次数，就克隆一个一模一样的自己
        growTag.addAndGet(1);
        cleanMySelf();
    }

    @Override
    public void wilt() {
        // 如果退化达到一定次数，就把自己销毁
        growTag.addAndGet(-1);
        cleanMySelf();
    }

    private void cleanMySelf() {
        if(canCleanMySelf()){
            parentChannel.removeChild(this);
        }
    }

    private boolean canCleanMySelf() {
        return (TimeUtil.currentSecondsInCache()-lastTransSecondsTimestamp) > CommonConstants.cleanOutputChannelSeconds;
    }
}
