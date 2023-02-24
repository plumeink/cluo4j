package com.canfuu.cluo.brain.core.hidden.unit;

import com.canfuu.cluo.brain.common.CommonEntity;
import com.canfuu.cluo.brain.common.Unit;
import com.canfuu.cluo.brain.common.util.TimeUtil;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 神经元
 */
public class HiddenUnit extends CommonEntity implements Unit {

    private HiddenUnitOutputGroup outputGroup = new HiddenUnitOutputGroup(this);

    //达到这个电位，就会开始传递信息
    private int valueThreshold = -55;

    // 兴奋的传递，是由于达到valueThreshold后，快速吸收周围K+，然后通过轴突向下传递
    // 可是由于是机器，我们可以假设外界的钾离子是无限的也就是，向下传递的离子数量在1000-2000之间
    private int transValue = 1500;

    private final AtomicInteger savedValue = new AtomicInteger(0);


    private long lastAboveThresholdTime = System.currentTimeMillis();

    private final Lock active = new ReentrantLock();

    public HiddenUnit(){
    }

    public void linkToUnit(Unit unit) {
        outputGroup.linkToUnit(unit);
    }

    public void accept(int b) {

        long currentTime = TimeUtil.currentTime();

        long gap = currentTime - lastAboveThresholdTime;

        HiddenUnitStatus status = HiddenUnitStatus.chooseByRecordTime(gap);


        double realValue = b;

        if (HiddenUnitStatus.NO_RESPONSE_ABOVE == status) {

            return;

        } else if (HiddenUnitStatus.INSENSITIVE_ABOVE == status) {

            // 系数还需要调研
            realValue = realValue * 0.8;

        } else if (HiddenUnitStatus.BELOW_NORMAL == status) {

            // 系数还需要调研
            realValue = realValue * 0.9;

        }


        active.lock();

        int result = savedValue.addAndGet((int) realValue);
        if ((status.chooseValueByRecordTime(gap)+result) >= valueThreshold) {

            savedValue.set(0);
            lastAboveThresholdTime = currentTime;

        } else {

            return;

        }
        active.unlock();


        outputGroup.transValue(transValue);
    }




}