package com.canfuu.cluo.brain.core.factory;

import com.canfuu.cluo.brain.common.Unit;
import com.canfuu.cluo.brain.core.hidden.unit.HiddenUnit;
import com.canfuu.cluo.brain.core.hidden.unit.HiddenUnitOutput;
import com.canfuu.cluo.brain.core.input.InputUnit;
import com.canfuu.cluo.brain.core.output.OutputUnit;
import com.canfuu.cluo.brain.support.UnitRuntimeService;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class HiddenUnitFactory implements Runnable{

    private List<HiddenUnit> allHiddenUnits = new ArrayList<>();

    private List<HiddenUnit> hiddenUnitsLinkToInput = new ArrayList<>();

    private List<HiddenUnit> hiddenUnitsLinkToOutput = new ArrayList<>();

    private List<HiddenUnit> hiddenUnitsInMiddle = new ArrayList<>();


    private List<HiddenUnit> hiddenUnitsPositive = new ArrayList<>();


    private List<HiddenUnit> hiddenUnitsNegative = new ArrayList<>();


    private boolean runningInFactory = true;


    private UnitRuntimeService unitRuntimeService;

    public HiddenUnitFactory(UnitRuntimeService unitRuntimeService, boolean runningInFactory){
        this.unitRuntimeService = unitRuntimeService;
        this.runningInFactory = runningInFactory;
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(60);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public void initialize(List<Unit> inputUnits, List<Unit> outputUnits, int hiddenUnitCount) {
        Random r = new Random();
        for (int i = 0; i < inputUnits.size(); i++) {
            Unit inputUnit = inputUnits.get(i);
            HiddenUnit hiddenUnit = new HiddenUnit(this, true);
            inputUnit.linkToUnit(hiddenUnit);
            allHiddenUnits.add(hiddenUnit);
            hiddenUnitsLinkToInput.add(hiddenUnit);
        }
        for (int i = 0; i < outputUnits.size(); i++) {
            Unit outputUnit = outputUnits.get(i);
            HiddenUnit hiddenUnit = new HiddenUnit(this, true);
            hiddenUnit.linkToUnit(outputUnit);
            allHiddenUnits.add(hiddenUnit);
            hiddenUnitsLinkToOutput.add(hiddenUnit);
        }

        for (int i = 0; i < hiddenUnitCount; i++) {
            HiddenUnit hiddenUnit = new HiddenUnit(this, r.nextInt()%100 > 10);
            if(hiddenUnit.isPositive()){
                hiddenUnitsPositive.add(hiddenUnit);
            } else {
                hiddenUnitsNegative.add(hiddenUnit);
            }
            allHiddenUnits.add(hiddenUnit);
            hiddenUnitsInMiddle.add(hiddenUnit);
        }

        if(!runningInFactory){
            System.out.println("active hidden "+allHiddenUnits.size());
            unitRuntimeService.asyncActiveHiddenUnit(allHiddenUnits);
        }

    }

    public Unit wantUnit(Unit unit) {
        if(unit instanceof HiddenUnit){
            Random random = new Random();

            if(random.nextInt()%100<10 && hiddenUnitsLinkToOutput !=null){
                return hiddenUnitsLinkToOutput.get(random.nextInt(hiddenUnitsLinkToOutput.size()));
            }

            if(random.nextInt()%100<20 && hiddenUnitsNegative.size()>0){
                return hiddenUnitsNegative.get(random.nextInt(hiddenUnitsNegative.size()));
            }

            return hiddenUnitsPositive.get(random.nextInt(hiddenUnitsPositive.size()));
        }
        return null;
    }


    @Override
    public void run() {
       unitRuntimeService.syncActiveHiddenUnit(allHiddenUnits);
    }
}
