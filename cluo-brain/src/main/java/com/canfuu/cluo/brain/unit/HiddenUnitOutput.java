package com.canfuu.cluo.brain.unit;

import com.canfuu.cluo.brain.common.CommonEntity;
import com.canfuu.cluo.brain.signal.InnerSignal;
import com.canfuu.cluo.brain.signal.OuterSignal;

public class HiddenUnitOutput  extends CommonEntity {

    void accept(InnerSignal innerSignal, Unit unit) {
        OuterSignal outerSignal = createOuterSignal(innerSignal);
        unit.accept(outerSignal);
    }

    private OuterSignal createOuterSignal(InnerSignal innerSignal) {
        return null;
    }
}
