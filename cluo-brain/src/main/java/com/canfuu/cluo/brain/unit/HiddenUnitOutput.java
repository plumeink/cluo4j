package com.canfuu.cluo.brain.unit;

import com.canfuu.cluo.brain.common.CommonEntity;
import com.canfuu.cluo.brain.signal.InnerSignal;
import com.canfuu.cluo.brain.signal.OuterSignal;
import com.canfuu.cluo.brain.signal.TransportOuterSignal;

public class HiddenUnitOutput  extends CommonEntity {

    private int value = 5;

    void accept(InnerSignal innerSignal, Unit unit) {
        OuterSignal outerSignal = createOuterSignal(innerSignal);
        unit.accept(outerSignal);
    }

    private OuterSignal createOuterSignal(InnerSignal innerSignal) {
        OuterSignal outerSignal = new OuterSignal();
        outerSignal.setValue(innerSignal.getValue());
        return outerSignal;
    }
}
