package com.canfuu.cluo.brain.util;

public class TimeUtil {
    public static long currentTime(){
        return System.currentTimeMillis();
    }

    public static int currentSeconds(){
        return (int)currentTime() / 1000;
    }
}
