package com.canfuu.cluo.brain.unit;

import com.canfuu.cluo.brain.common.CommonEntity;
import com.canfuu.cluo.brain.signal.InnerSignal;
import com.canfuu.cluo.brain.signal.OuterSignal;
import com.canfuu.cluo.brain.signal.TransportOuterSignal;

import java.util.ArrayList;
import java.util.List;

/**
 * 神经元
 */
public class HiddenUnit extends CommonEntity implements Unit{

    private HiddenUnitOutputGroup outputGroup = new HiddenUnitOutputGroup();

    private int valueThreshold = 0;

    public void linkToUnit(Unit unit) {
        outputGroup.linkToUnit(unit);
    }

    @Override
    public void accept(OuterSignal signal) {

        stimulate();

        if(extractSignal(signal)) {

            InnerSignal acceptInnerSignal = createInnerSignal(signal);

            InnerSignal transInnerSignal = transInnerSignal(acceptInnerSignal);

            outputGroup.transSignal(transInnerSignal);

        }

    }

    private InnerSignal transInnerSignal(InnerSignal acceptInnerSignal) {
        return acceptInnerSignal;
    }

    private boolean extractSignal(OuterSignal signal) {
        if(signal instanceof TransportOuterSignal){
            return signal.getValue()>=0;
        } else {
            // TODO: 2023/2/18 do sth
            return false;
        }
    }


    private InnerSignal createInnerSignal(OuterSignal signal) {
        InnerSignal innerSignal = new InnerSignal();
        innerSignal.setValue(signal.getValue());
        return innerSignal;
    }

    private void stimulate() {

    }





}
