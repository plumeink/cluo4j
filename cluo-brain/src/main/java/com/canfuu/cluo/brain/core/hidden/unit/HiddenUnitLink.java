package com.canfuu.cluo.brain.core.hidden.unit;

import com.canfuu.cluo.brain.common.signal.Signal;
import com.canfuu.cluo.brain.common.util.TimeUtil;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class HiddenUnitLink {
    private String fromUnitId;
    private String toUnitId;
    private Signal signal;

    private AtomicLong useCount = new AtomicLong(0);
    private long lastUseTime;

    public HiddenUnitLink(String fromUnitId, String toUnitId, Signal signal) {
        this.fromUnitId = fromUnitId;
        this.toUnitId = toUnitId;
        this.signal = signal;
        this.lastUseTime = TimeUtil.currentSecondsInCache();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HiddenUnitLink that = (HiddenUnitLink) o;
        return Objects.equals(fromUnitId, that.fromUnitId) && Objects.equals(toUnitId, that.toUnitId) && Objects.equals(signal, that.signal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromUnitId, toUnitId, signal);
    }

    public Signal createSignal(){
        useCount.addAndGet(1L);
        lastUseTime=TimeUtil.currentSecondsInCache();
        return new Signal(signal);
    }

    public String getFromUnitId() {
        return fromUnitId;
    }

    public String getToUnitId() {
        return toUnitId;
    }

    public Signal getSignal() {
        return signal;
    }

    public long getUseCount() {
        return useCount.get();
    }

    public long getLastUseTime() {
        return lastUseTime;
    }
}
