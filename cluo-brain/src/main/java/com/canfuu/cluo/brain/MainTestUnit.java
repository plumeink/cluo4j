package com.canfuu.cluo.brain;

import com.canfuu.cluo.brain.common.Unit;
import com.canfuu.cluo.brain.core.factory.HiddenUnitFactory;
import com.canfuu.cluo.brain.core.input.InputUnit;
import com.canfuu.cluo.brain.core.output.OutputUnit;
import com.canfuu.cluo.brain.support.UnitRuntimeService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainTestUnit {
    public static void main(String[] args) throws InterruptedException {
        UnitRuntimeService unitRuntimeService = new UnitRuntimeService();
        HiddenUnitFactory hiddenUnitFactory = new HiddenUnitFactory(unitRuntimeService, true);
        List<Unit> inputUnits = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            InputUnit inputUnit = new InputUnit();
            inputUnits.add(inputUnit);
        }
        
        List<Unit> outputUnits = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            OutputUnit outputUnit = new OutputUnit();
            outputUnits.add(outputUnit);
        }
        hiddenUnitFactory.initialize(inputUnits, outputUnits, 1000);

        new Thread(() ->{
            while (true){
                Scanner scanner = new Scanner(System.in);
                String next = scanner.nextLine();
                System.out.println("input "+ next);
                for (byte b : next.getBytes()) {
                    inputUnits.forEach(unit -> unit.accept(b));
                }
            }

        }).start();
        
        while (true){
            Thread.sleep(1000);
        }
    }
}
