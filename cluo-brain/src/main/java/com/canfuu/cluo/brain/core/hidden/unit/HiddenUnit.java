package com.canfuu.cluo.brain.core.hidden.unit;

import com.canfuu.cluo.brain.common.CommonConstants;
import com.canfuu.cluo.brain.common.CommonEntity;
import com.canfuu.cluo.brain.common.Unit;
import com.canfuu.cluo.brain.common.UnitGroup;
import com.canfuu.cluo.brain.common.signal.Signal;
import com.canfuu.cluo.brain.common.signal.SignalFeature;
import com.canfuu.cluo.brain.common.util.TimeUtil;
import com.canfuu.cluo.brain.core.hidden.group.HiddenUnitGroup;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 神经元
 */
public class HiddenUnit extends CommonEntity implements Unit{

    private final HiddenUnitChannel channel = new HiddenUnitChannel(this.getId());

    private HiddenUnitGroup hiddenUnitGroup;

    //达到这个电位，就会开始传递信息
    private int valueThreshold = CommonConstants.hiddenUnitValueThreshold;

    // 兴奋的传递，是由于达到valueThreshold后，快速吸收周围K+，然后通过轴突向下传递
    // 可是由于是机器，我们可以假设外界的钾离子是无限的也就是，向下传递的离子数量在1000-2000之间
    private int transValue = CommonConstants.hiddenUnitTransValueCount;

    private double savedValue = 0;

    private long lastAboveThresholdTime = System.currentTimeMillis();

    private long lastTransSecond = TimeUtil.currentSecondsInCache();

    private final Lock active = new ReentrantLock();

    public HiddenUnit(HiddenUnitGroup hiddenUnitGroup){
        this.hiddenUnitGroup = hiddenUnitGroup;
    }

    public void accept(Signal signal) {

        long currentTime = TimeUtil.currentTime();

        long gap = currentTime - lastAboveThresholdTime;

        HiddenUnitStatus status = HiddenUnitStatus.chooseByRecordTime(gap);

        double realValue = signal.value();

        if(signal.isInhibition() && realValue>0){
            realValue = realValue * -1;
        } if(realValue<0 && savedValue<CommonConstants.unitValueMin){
            return;
        }

        if (HiddenUnitStatus.NO_RESPONSE_ABOVE == status) {
            return;

        } else if (HiddenUnitStatus.INSENSITIVE_ABOVE == status) {

            // 系数还需要调研
            realValue = realValue * 0.8;

        } else if (HiddenUnitStatus.BELOW_NORMAL == status) {

            // 系数还需要调研
            realValue = realValue * 0.9;
        }

        int choosingValue = status.chooseValueByRecordTime(gap);

        active.lock();

        double result = savedValue + realValue;

        if ((result + choosingValue) >= valueThreshold) {
            savedValue = 0;

            lastAboveThresholdTime = currentTime;

            active.unlock();
        } else {
            savedValue = result;

            active.unlock();
            return;
        }

        long currentSeconds = TimeUtil.currentSecondsInCache();
        long transGap = currentSeconds-lastTransSecond;
        if(transGap>CommonConstants.forgetSecondsToTriggerWilt){
            channel.transValue(-1*transGap*CommonConstants.forgetValuePerSeconds, SignalFeature.AXON_WILT);
        }

        lastTransSecond = currentSeconds;
        channel.transValue(transValue, signal.getAxonFeature());
    }

    @Override
    public UnitGroup group() {
        return hiddenUnitGroup;
    }

    @Override
    public String toString() {

        long currentTime = TimeUtil.currentTime();

        long gap = currentTime - lastAboveThresholdTime;

        return getId() + " "+HiddenUnitStatus.chooseByRecordTime(gap) + " " + savedValue+" link: "+ channel.toString();
    }
}