package com.canfuu.cluo.brain.common;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CommonConstants {

    public static final boolean asyncChannel = true;

    public static final long cleanOutputChannelSeconds = 60 * 60 * 24L;

    public static final int hiddenUnitTransValueCount = 1500;


    public static int transValue = 8;
    public static int activeValue = 20;
    public static int hiddenUnitValueThreshold = -55;


    public static int forgetSecondsToTriggerWilt = 60;

    public static int forgetValuePerSeconds = 2000;
    public static int unitValueMin = -90;

    public static String unitLinksDir = "/Users/chutianshu/Documents/data/cluo/hidden-unit-links/";

    public static String refFileSuffix = ".ref";

    public static int growSpeed = 10;

    public static int wiltSpeed = -1;

    public static int linkSpeed = 30;

    public static int breakLinkSpeed = -5;


    public static int unitStatusTimeRangeRatio = 2;
}