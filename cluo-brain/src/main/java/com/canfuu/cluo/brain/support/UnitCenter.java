package com.canfuu.cluo.brain.support;

import com.canfuu.cluo.brain.common.Unit;
import com.canfuu.cluo.brain.common.UnitGroup;
import com.canfuu.cluo.brain.common.UnitType;
import com.canfuu.cluo.brain.core.hidden.group.HiddenUnitGroup;
import com.canfuu.cluo.brain.core.hidden.unit.HiddenUnit;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UnitCenter {
    private static Map<String, Unit> unitMap = new ConcurrentHashMap<>();

    public static HiddenUnitGroup createHiddenGroup(int count,HiddenUnitGroup nextHiddenUnitGroup) {
        HiddenUnitGroup hiddenUnitGroup = new HiddenUnitGroup(nextHiddenUnitGroup);
        for (int i = 0; i < count; i++) {
            HiddenUnit hiddenUnit = new HiddenUnit(hiddenUnitGroup);
            addHiddenUnit(hiddenUnit);
            hiddenUnitGroup.addUnit(hiddenUnit);
        }
        return hiddenUnitGroup;
    }

    public static void addHiddenUnit(HiddenUnit hiddenUnit){
        unitMap.put(hiddenUnit.getId(),hiddenUnit);
    }



    public static Unit findById(String id){
       return unitMap.get(id);
    }

    public static String chooseUnit(String unitId) {
        Unit unit = unitMap.get(unitId);
        Unit chooseUnit = unit.group().chooseUnit(true);
        if(chooseUnit==null){
            return null;
        }
        return chooseUnit.getId();
    }

}
