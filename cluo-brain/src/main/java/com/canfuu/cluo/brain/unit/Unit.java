package com.canfuu.cluo.brain.unit;

import com.canfuu.cluo.brain.signal.Signal;

import java.util.ArrayList;
import java.util.List;

public class Unit {
    public List<UnitOutput> outputs;
    public List<UnitInput> inputs;
    public Unit toUnit;

    public Unit() {
        outputs = new ArrayList<>();
        inputs = new ArrayList<>();
    }

    public Unit(Unit toUnit){
        this.toUnit = toUnit;
        List<UnitInput> toInputs = this.toUnit.createInput();
        for (int i = 0; i < toInputs.size(); i++) {
            UnitInput toInput = toInputs.get(i);
            toInput.fromUnit = this;
            UnitOutput output = new UnitOutput(toInput, this, toUnit);
        }
    }

    public List<UnitOutput> createOutput() {
        List<UnitOutput> outputList = new ArrayList<>();
        UnitOutput output = new UnitOutput(this);
        outputList.add(output);
        this.outputs.addAll(outputList);
        return outputList;
    }

    public List<UnitInput> createInput() {
        List<UnitInput> inputList = new ArrayList<>();

        for (int i = 0; i < 1; i++) {
            UnitInput input = new UnitInput(this);
            inputList.add(input);
        }

        this.inputs.addAll(inputList);
        return inputList;
    }



}
