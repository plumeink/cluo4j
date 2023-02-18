package com.canfuu.cluo.brain.unit;

import com.canfuu.cluo.brain.common.CommonEntity;
import com.canfuu.cluo.brain.signal.InnerSignal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class HiddenUnitOutputGroup  extends CommonEntity {

    private static final Logger log = LoggerFactory.getLogger(HiddenUnitOutputGroup.class);

    private List<HiddenUnitOutput> outputs = new ArrayList<>();

    private Unit nextUnit;

    void transSignal(InnerSignal innerSignal){
        if(nextUnit!=null) {
            outputs.forEach(hiddenUnitOutput -> {
                hiddenUnitOutput.accept(innerSignal, nextUnit);
            });
        } else {
            log.info("HiddenUnitOutputGroup "+getId() +" do not have next unit.");
        }
    }

    void linkToUnit(Unit nextUnit) {
        this.nextUnit = nextUnit;
        HiddenUnitOutput output = new HiddenUnitOutput();
        this.outputs.add(output);
    }

}
