package com.canfuu.cluo.brain.support;

import com.canfuu.cluo.brain.common.Unit;
import com.canfuu.cluo.brain.core.hidden.unit.HiddenUnit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UnitRuntimeService {

    private ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(1500);

    public void asyncActiveHiddenUnit(List<HiddenUnit> hiddenUnits) {
        for (int i = 0; i < hiddenUnits.size(); i++) {
            threadPool.scheduleAtFixedRate(hiddenUnits.get(i), 0L, 1L, TimeUnit.SECONDS);
        }
        System.out.println("success");
    }

    public void syncActiveHiddenUnit(List<HiddenUnit> hiddenUnits) {
        hiddenUnits.forEach(HiddenUnit::run);
    }
}
