package com.canfuu.cluo.brain.core.hidden.group;

import com.canfuu.cluo.brain.common.Unit;
import com.canfuu.cluo.brain.common.UnitGroup;
import com.canfuu.cluo.brain.common.signal.Signal;
import com.canfuu.cluo.brain.common.signal.SignalFeature;
import com.canfuu.cluo.brain.core.hidden.HiddenUnitManager;
import com.canfuu.cluo.brain.core.hidden.unit.HiddenUnit;
import com.canfuu.cluo.brain.support.UnitCenter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class HiddenUnitGroup implements UnitGroup {

    private List<HiddenUnit> hiddenUnits = new ArrayList<>();

    @Override
    public Unit chooseUnit() {
        if(hiddenUnits.size()==0){
            return null;
        }
        return hiddenUnits.get(new Random().nextInt(hiddenUnits.size()));
    }

    @Override
    public void addUnit(Unit unit) {
        hiddenUnits.add((HiddenUnit) unit);
    }

    @Override
    public List<Unit> getAllUnit() {
        return new ArrayList<>(hiddenUnits);
    }

    private static class Main {
        public static List<Object[]> list = new ArrayList<>();
        public static void main(String[] args) throws InterruptedException {
            HiddenUnitManager.init();
            HiddenUnitGroup hiddenGroup = UnitCenter.createHiddenGroup(11);
            List<Unit> unit = hiddenGroup.getAllUnit();
            List<Unit> inputUnit = new ArrayList<>();
            List<Unit> outputUnit = new ArrayList<>();
            for (int i = 0; i < unit.size(); i++) {
                if(i<10){
                    inputUnit.add(unit.get(i));
                }
            }
            for (int i = 0; i < 10; i++) {

                TestOutputUnit testOutputUnit = new TestOutputUnit(hiddenGroup,i);
                UnitCenter.addHiddenUnit(testOutputUnit);
                hiddenGroup.addUnit(testOutputUnit);
                outputUnit.add(testOutputUnit);
            }
            outputUnit.forEach(unit1 -> {

            System.out.println("output -> "+unit1.getId());
            });

            new Thread(() ->{
                while (true) {
                    for (int i = 0; i < list.size(); i++) {
                        Object[] objects = list.get(i);
                        SignalFeature[] features = new SignalFeature[objects.length-2];
                        for (int j = 2; j < objects.length; j++) {
                            features[j-2] = (SignalFeature) objects[j];
                        }
                        int value = (int) objects[1];
                        int data = (int) objects[0];
                        Signal signal = new Signal(value, features);
                        inputUnit.get(data%10).accept(signal);
                        inputUnit.get((data+1==10?0:(data+1))).accept(signal);
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();

            Scanner scanner = new Scanner(System.in);
            while (true){
                int i = scanner.nextInt();
                if(i==-1){
                    StringJoiner sj = new StringJoiner(",");
                    for (int j = 0; j < list.size(); j++) {
                        sj.add(list.get(j)[0]+"");
                    }
                    System.out.println("data info: "+ sj);
                    System.out.println("unit info: ");
                    unit.forEach(temp -> {
                        System.out.println(temp.getId()+" "+temp.toString());
                    });
                    System.out.println("ref  info: "+ HiddenUnitManager.getAllLinks());
                    continue;
                }
                System.out.println("I say: "+ i);
                Object[] objects = {i, 10, SignalFeature.EXCITATION, SignalFeature.AXON_GROW};
                addList(objects);
            }

        }

        public static synchronized void addList(Object[] objects){
            list.add(objects);
            if(list.size()==11){
                list.remove(0);
            }
        }

        private static class TestOutputUnit extends HiddenUnit{

            private static Map<Integer, AtomicInteger> map = new ConcurrentHashMap<>();

            private static long acceptTimestamp = System.currentTimeMillis();

            private static long lastTimestamp = System.currentTimeMillis();

            static {
                new Thread(() -> {
                    while (true) {
                        try {
                            Thread.sleep(100);
                            long s = System.currentTimeMillis();
                            if(!map.isEmpty() && (s>acceptTimestamp|| s>lastTimestamp)){
                                StringJoiner sb = new StringJoiner(",");
                                map.forEach((k,v) -> {
                                    sb.add(k+"("+v+")");
                                });

                                System.out.println("AI say: "+ sb);
                                map.clear();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                }).start();
            }
            private int output;


            public TestOutputUnit(HiddenUnitGroup hiddenUnitGroup, int output) {
                super(hiddenUnitGroup);
                this.output = output;

            }

            @Override
            public void accept(Signal value) {
                Object[] objects = {output, 10, SignalFeature.AXON_WILT, SignalFeature.INHIBITION};
                map.computeIfAbsent(output, o->new AtomicInteger(0)).addAndGet(1);
                acceptTimestamp = System.currentTimeMillis() + 10000L;
                addList(objects);
            }

        }
    }
}
