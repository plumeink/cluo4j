package com.canfuu.cluo.brain.core.hidden;

import com.canfuu.cluo.brain.common.Link;
import com.canfuu.cluo.brain.common.Unit;
import com.canfuu.cluo.brain.common.signal.Signal;
import com.canfuu.cluo.brain.core.hidden.unit.HiddenUnitLink;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class HiddenUnitManager {

    private static HiddenLinksManager hiddenLinksManager = new HiddenLinksManager();

    private static final Executor executor = Executors.newCachedThreadPool();

    private static final Map<String, Unit> unitMap = new ConcurrentHashMap<>();
    public static void init(){

    }


    public static Unit findById(String id){
        return unitMap.get(id);
    }


    public static Map<String, List<HiddenUnitLink>> getAllLinks(String unitId){
        return hiddenLinksManager.getAllLinks(unitId);
    }


    /**
     * 传递给下一个unit
     *
     * @param unitId
     * @param signal
     */
    public static void transToUnit(String unitId, Signal signal) {
        executor.execute(() ->{
            findById(unitId).accept(signal);
        });

    }

    /**
     * 释放了grow的时候会调用
     * @param unitId
     */
    public static void wantToLinkOther(String unitId){
        hiddenLinksManager.incrementLinkable(unitId);
    }

    /**
     * seconds秒内没有收到信息
     * @param unitId
     * @param seconds
     */
    public static void notWantToLinkOther(String unitId, long seconds) {
        hiddenLinksManager.decrementLinkable(unitId, seconds);
    }

}
