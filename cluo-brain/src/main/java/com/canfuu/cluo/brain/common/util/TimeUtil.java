package com.canfuu.cluo.brain.common.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeUtil {

    private static ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private static long secondsInCache = System.currentTimeMillis()/1000;

    static {
        scheduledExecutorService.scheduleAtFixedRate(()-> secondsInCache = System.currentTimeMillis()/1000,0L, 1000L, TimeUnit.MILLISECONDS);
    }

    public static long currentTime(){
        return System.currentTimeMillis();
    }

    public static long currentSecondsInCache() {
        return secondsInCache;
    }

    public static long currentSecondsInCacheByInterval(int interval) {
        return secondsInCache/interval;
    }
}
