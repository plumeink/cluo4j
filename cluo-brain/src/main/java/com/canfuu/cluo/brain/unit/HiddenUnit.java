package com.canfuu.cluo.brain.unit;

import com.canfuu.cluo.brain.common.CommonEntity;
import com.canfuu.cluo.brain.signal.InnerSignal;
import com.canfuu.cluo.brain.signal.OuterSignal;

import java.util.ArrayList;
import java.util.List;

/**
 * 神经元
 */
public class HiddenUnit extends CommonEntity implements Unit{

    private HiddenUnitOutputGroup outputGroup = new HiddenUnitOutputGroup();

    public void linkToUnit(Unit unit) {
        outputGroup.linkToUnit(unit);
    }

    @Override
    public void accept(OuterSignal signal) {

        stimulate();

        if(extractSignal(signal)) {

            InnerSignal innerSignal = createInnerSignal(signal);

            outputGroup.transSignal(innerSignal);

        }

    }

    private boolean extractSignal(OuterSignal signal) {
        return true;
    }

    private InnerSignal createInnerSignal(OuterSignal signal) {
        return null;
    }

    private void stimulate() {

    }





}
