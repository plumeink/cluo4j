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

    private int defaultValue = -90;

    // 兴奋的传递，是由于达到valueThreshold后，快速吸收周围K+，然后通过轴突向下传递
    // 可是由于是机器，我们可以假设外界的钾离子是无限的也就是，向下传递的离子数量在1000-2000之间
    private int transValue = 1500;

    private AtomicInteger value = new AtomicInteger(defaultValue);

    private HiddenUnitStatus status;

    private Long lastAboveThresholdTime = null;

    private Lock lock = new ReentrantLock();

    public HiddenUnit(){
    }

    public void linkToUnit(Unit unit) {
        outputGroup.linkToUnit(unit);
    }

    public void accept(int b) {


        lock.lock();

        long currentTime = TimeUtil.currentTime();

        try {

            if (lastAboveThresholdTime != null) {

                long gap = currentTime - lastAboveThresholdTime;
                status = HiddenUnitStatus.chooseByRecordTime(gap);
                value.set(status.chooseValueByRecordTime(gap));

            }

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

            int result = value.addAndGet((int) realValue);

            if (result >= valueThreshold) {

                value.set(HiddenUnitStatus.NO_RESPONSE_ABOVE.getFromV());
                status = HiddenUnitStatus.NO_RESPONSE_ABOVE;
                lastAboveThresholdTime = currentTime;

            } else {

                return;

            }

        } finally {
            lock.unlock();
        }

        outputGroup.transValue(transValue);
    }




}