package com.canfuu.cluo.brain.zone;

import com.canfuu.cluo.brain.signal.Signal;
import com.canfuu.cluo.brain.unit.Unit;
import com.canfuu.cluo.brain.unit.UnitInput;
import com.canfuu.cluo.brain.unit.UnitOutput;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Zone {
    private List<Unit> inputUnits = new ArrayList<>();
    private List<Unit> outputUnits = new ArrayList<>();

    private final int unitMaxInputLength;
    private final int unitMaxOutputLength;

    public Zone(int unitMaxInputLength, int unitMaxOutputLength) {
        Unit output = new Unit();
        outputUnits.add(output);

        Unit input = new Unit(output);
        inputUnits.add(input);

        this.unitMaxInputLength = unitMaxInputLength;
        this.unitMaxOutputLength = unitMaxOutputLength;
    }

    public void teach(String text, String except) {

        byte[] inputBytes = text.getBytes(StandardCharsets.UTF_8);
        int unitIndex = 0;
        int currentI = 0;
        for (int i = 0; i < inputBytes.length; i++) {

            List<UnitInput> inputs = inputUnits.get(unitIndex).inputs;

            if (inputs.size() == currentI) {
                inputUnits.get(unitIndex).createInput();
            }

            currentI++;

            if (currentI == unitMaxInputLength) {
                currentI = 0;
                unitIndex++;
                if (unitIndex == inputUnits.size()) {
                    inputUnits.add(new Unit());
                }
            }
        }

        byte[] outputBytes = except.getBytes(StandardCharsets.UTF_8);
        unitIndex = 0;
        currentI = 0;
        for (int i = 0; i < outputBytes.length; i++) {

            List<UnitOutput> outputs = outputUnits.get(unitIndex).outputs;

            if (outputs.size() == currentI) {
                outputUnits.get(unitIndex).createOutput();
            }

            currentI++;

            if (currentI == unitMaxOutputLength) {
                currentI = 0;
                unitIndex++;
                if (unitIndex == outputUnits.size()) {
                    outputUnits.add(new Unit());
                }
            }
        }

        unitIndex = 0;
        currentI = 0;
        for (int i = 0; i < inputBytes.length; i++) {

            List<UnitInput> inputs = inputUnits.get(unitIndex).inputs;

            Signal signal = new Signal();
            signal.data = inputBytes[i];
            inputs.get(currentI).accept(signal);

            currentI++;

            if (currentI == unitMaxInputLength) {
                currentI = 0;
                unitIndex++;
            }
        }



    }
}