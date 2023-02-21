package com.canfuu.cluo.brain.core.hidden.unit;

import com.canfuu.cluo.brain.common.CommonConstants;
import com.canfuu.cluo.brain.common.CommonEntity;
import com.canfuu.cluo.brain.common.Unit;
import com.canfuu.cluo.brain.common.util.TimeUtil;

import java.util.*;

class HiddenUnitOutputGroup  extends CommonEntity {

    private final List<HiddenUnitOutput> outputs = new ArrayList<>();

    private final Map<HiddenUnitOutput, Integer> outputsUsedTimeMap = new LinkedHashMap<>();

    private Unit nextUnit;

    private Unit myUnit;

    private int savedValue = 0;

    private int valueCanNewOutput = 5;


    public HiddenUnitOutputGroup(Unit myUnit){
        this.myUnit = myUnit;
    }

    void linkToUnit(Unit nextUnit) {
        this.nextUnit = nextUnit;
        this.outputs.clear();
        this.outputsUsedTimeMap.clear();
        HiddenUnitOutput output = new HiddenUnitOutput(myUnit);
        this.outputs.add(output);
        this.outputsUsedTimeMap.put(output, TimeUtil.currentSeconds());
    }

    public void transValue(int value) {
        if (nextUnit != null) {
            for (int i = 0; i < outputs.size(); i++) {
                HiddenUnitOutput output = outputs.get(i);

                int usedValue = output.accept(value, nextUnit);

                outputsUsedTimeMap.put(output, TimeUtil.currentSeconds());

                value = value - usedValue;
                if (value == 0) {
                    return;
                } else if (i == outputs.size() - 1) {
                    savedValue += value;

                    if (savedValue >= valueCanNewOutput) {

                        savedValue = savedValue - valueCanNewOutput;

                        createNewOutput();
                    }
                }
            }
        } else {
            myUnit.wantNextUnit();
        }
    }

    void createNewOutput() {
        HiddenUnitOutput output = new HiddenUnitOutput(myUnit);
        this.outputs.add(output);
        this.outputsUsedTimeMap.put(output, TimeUtil.currentSeconds());
    }

    public void cleanOutput() {
        List<HiddenUnitOutput> removeOutputs = new ArrayList<>();

        int currentSeconds = TimeUtil.currentSeconds();

        outputsUsedTimeMap.forEach((output, seconds) -> {
            if((seconds + CommonConstants.CLEAN_SECONDS_THRESHOLD) < currentSeconds){
                outputs.remove(output);
                removeOutputs.add(output);
            }
        });

        removeOutputs.forEach(outputsUsedTimeMap::remove);
    }
}