package com.canfuu.cluo.brain.core.hidden;

import com.canfuu.cluo.brain.common.Link;
import com.canfuu.cluo.brain.common.Unit;
import com.canfuu.cluo.brain.common.signal.Signal;

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
    public static void transToUnit(String unitId, Signal signal) {
        executor.execute(() ->{
            findById(unitId).accept(signal);
        });

    }

    public static Unit findById(String id){
        return unitMap.get(id);
    }


    public static Map<Link<String,String>, AtomicInteger> getAllLinks(){
        return hiddenLinksManager.getAllLinks();
    }

    public static void removeLink(String fromId, String toId) {
        findById(fromId).removeLink(toId);
    }

    public static void wantToLinkOther(String unitId){
    }


    public static void notWantToLinkOther(String unitId, long seconds) {
    }

    public static void transValueLessThanOne(String fromUnitId, String toUnitId) {
    }
}
