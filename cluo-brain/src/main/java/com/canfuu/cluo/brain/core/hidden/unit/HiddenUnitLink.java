package com.canfuu.cluo.brain.core.hidden.unit;

import com.canfuu.cluo.brain.common.signal.Signal;
import com.canfuu.cluo.brain.common.signal.SignalFeature;
import com.canfuu.cluo.brain.common.util.TimeUtil;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 通道应该具备:
 *   每个通道都有自己所占上层组织的百分比
 *   对于root通道来说，所占100%
 *   对于以下所有叶子节点的百分比是可调控的，调控依据叶子节点所
 * 通道是可以被扩张和缩减的，具体体现为unit接收到什么样的信号，如果是扩张信号，就需要扩张；如果是缩减信号，就需要缩减
 * 实际情况中，扩张和缩减体现在长度和宽度上
 * 但是，由于我们的unit的每次动作电位所带的电量是一定的（正常情况人脑中的+离子是有限的，但是我们的程序可以是无限的）
 * 因此，虽然宽度代表着允许通行的电位的最大值，这里不会体现
 *
 * 扩张和缩减仅体现在长度上
 * 长度代表着可能具有的channels数量，即允许的channels的最大值
 *
 */
public class HiddenUnitLink {
    private String fromUnitId;
    private String toUnitId;

    private int activeValue;

    private SignalFeature feature;
    private AtomicInteger count = new AtomicInteger(0);

    private long lastUseTime;

    public HiddenUnitLink(String fromUnitId, String toUnitId, SignalFeature feature) {
        this.fromUnitId = fromUnitId;
        this.toUnitId = toUnitId;
        this.feature = feature;
        this.lastUseTime = TimeUtil.currentSecondsInCache();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HiddenUnitLink that = (HiddenUnitLink) o;
        return Objects.equals(fromUnitId, that.fromUnitId) && Objects.equals(toUnitId, that.toUnitId) && feature == that.feature;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromUnitId, toUnitId, feature);
    }


    public String getFromUnitId() {
        return fromUnitId;
    }

    public String getToUnitId() {
        return toUnitId;
    }

    public SignalFeature getFeature() {
        return feature;
    }

    public void incrementCount(int delta){
        count.addAndGet(delta);
    }

    public void decrementCount(int delta){
        count.addAndGet(delta);
    }


    public int getCount() {
        return count.get();
    }

    public int getActiveValue() {
        return activeValue;
    }

    public long getLastUseTime() {
        return lastUseTime;
    }

    public void use(){
        lastUseTime = TimeUtil.currentSecondsInCache();
    }
}
