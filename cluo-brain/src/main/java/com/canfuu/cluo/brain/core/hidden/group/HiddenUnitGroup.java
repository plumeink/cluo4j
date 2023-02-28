package com.canfuu.cluo.brain.core.hidden.group;

import com.canfuu.cluo.brain.common.Unit;
import com.canfuu.cluo.brain.common.UnitGroup;
import com.canfuu.cluo.brain.core.hidden.unit.HiddenUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class HiddenUnitGroup implements UnitGroup {

    private List<HiddenUnit> hiddenUnits = new ArrayList<>();

    @Override
    public Unit chooseUnit() {
        if(hiddenUnits.size()==0){
            return null;
        }
        return hiddenUnits.get(new Random().nextInt(hiddenUnits.size()));
    }

    @Override
    public void addUnit(Unit unit) {
        hiddenUnits.add((HiddenUnit) unit);
    }

}
