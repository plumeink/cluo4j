package com.canfuu.cluo.brain.core.hidden.group;

import com.canfuu.cluo.brain.common.Unit;
import com.canfuu.cluo.brain.common.UnitGroup;
import com.canfuu.cluo.brain.common.signal.Signal;
import com.canfuu.cluo.brain.common.signal.SignalFeature;
import com.canfuu.cluo.brain.common.util.LoggerUtil;
import com.canfuu.cluo.brain.core.hidden.HiddenUnitManager;
import com.canfuu.cluo.brain.core.hidden.unit.HiddenUnit;
import com.canfuu.cluo.brain.support.UnitCenter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class HiddenUnitGroup implements UnitGroup {

    private List<HiddenUnit> hiddenUnits = new ArrayList<>();

    private HiddenUnitGroup nextHiddenUnitGroup = null;

    public HiddenUnitGroup(HiddenUnitGroup nextHiddenUnitGroup) {
        this.nextHiddenUnitGroup=nextHiddenUnitGroup;
    }

    @Override
    public Unit chooseUnit(boolean linkFar) {
        if(hiddenUnits.size()==0){
            return null;
        }
        Random random = new Random();

        if(linkFar && nextHiddenUnitGroup!=null && random.nextInt(100)<80){
            return nextHiddenUnitGroup.chooseUnit(false);
        }
        return hiddenUnits.get(random.nextInt(hiddenUnits.size()));
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

        public static List<Object[]> mainList = new ArrayList<>();
        public static void main(String[] args) throws InterruptedException {
            HiddenUnitManager.init();



            List<Unit> inputUnit = new ArrayList<>();
            List<Unit> outputUnit = new ArrayList<>();
            List<Unit> unit = new ArrayList<>();


            HiddenUnitGroup outputGroup = UnitCenter.createHiddenGroup(0, null);
            for (int i = 0; i < 10; i++) {

                TestOutputUnit testOutputUnit = new TestOutputUnit(outputGroup,i);
                UnitCenter.addHiddenUnit(testOutputUnit);
                outputGroup.addUnit(testOutputUnit);
                outputUnit.add(testOutputUnit);
            }

            HiddenUnitGroup middleGroup = UnitCenter.createHiddenGroup(20, outputGroup);

            HiddenUnitGroup inputGroup = UnitCenter.createHiddenGroup(10, middleGroup);

            inputUnit.addAll(inputGroup.hiddenUnits);

            unit.addAll(inputUnit);
            unit.addAll(middleGroup.hiddenUnits);
            unit.addAll(outputUnit);


            outputUnit.forEach(unit1 -> {

            LoggerUtil.print("output -> "+unit1.getId());
            });
            inputUnit.forEach(unit1 -> {

                LoggerUtil.print("input -> "+unit1.getId());
            });

            new Thread(() ->{
                while (true) {
                    try {
                        List<Object[]> list = new ArrayList<>(Main.mainList);
                        for (int i = 0; i < list.size(); i++) {
                            Object[] objects = list.get(i);
                            List<SignalFeature> features = new ArrayList<>();
                            for (int j = 2; j < objects.length; j++) {
                                features.add((SignalFeature) objects[j]);
                            }
                            int value = (int) objects[1];
                            int data = (int) objects[0];
                            Signal signal = new Signal(value, features.toArray(new SignalFeature[0]));
                            inputUnit.get(data % 10).accept(signal);
                            if (i < 4) {
                                if (data == 9) {
                                    inputUnit.get(0).accept(signal);
                                    inputUnit.get(1).accept(signal);
                                } else if (data == 8) {
                                    inputUnit.get((data % 10) + 1).accept(signal);
                                    inputUnit.get(0).accept(signal);
                                } else {
                                    inputUnit.get((data % 10) + 1).accept(signal);
                                    inputUnit.get((data % 10) + 2).accept(signal);
                                }

                            }
                        }

                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();

            new Thread(() ->{
                while (true) {
                    try {
                        List<Object[]> list = new ArrayList<>(Main.list);
                        for (int i = 0; i < list.size(); i++) {
                            Object[] objects = list.get(i);
                            List<SignalFeature> features = new ArrayList<>();
                            for (int j = 2; j < objects.length; j++) {
                                features.add((SignalFeature) objects[j]);
                            }
                            int value = (int) objects[1];
                            int data = (int) objects[0];
                            Signal signal = new Signal(value / 10, features.toArray(new SignalFeature[0]));
                            inputUnit.get(data % 10).accept(signal);
                        }
                        Thread.sleep(50);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();

            Scanner scanner = new Scanner(System.in);
            while (true){
                try {
                    int i = scanner.nextInt();
                    if(i==-1){
                        StringJoiner sj = new StringJoiner(",");
                        for (int j = 0; j < list.size(); j++) {
                            sj.add(list.get(j)[0]+"");
                        }
                        LoggerUtil.print("data info: "+ sj);
                        sj = new StringJoiner(",");
                        for (int j = 0; j < mainList.size(); j++) {
                            sj.add(mainList.get(j)[0]+"");
                        }
                        LoggerUtil.print("output info: "+ sj);
                        LoggerUtil.print("unit info: ");
                        unit.forEach(temp -> {
                            String prefix = "";
                            if(outputUnit.contains(temp)){
                                prefix="output";
                            }
                            if(middleGroup.hiddenUnits.contains(temp)){
                                prefix="middle";
                            }
                            if(inputUnit.contains(temp)){
                                prefix="input ";
                            }
                            LoggerUtil.print(prefix +" "+ temp.toString());
                        });
                        LoggerUtil.print("ref  info: "+ HiddenUnitManager.getAllLinks());
                        continue;
                    }
                    LoggerUtil.print("I say: "+ i);
                    Object[] objects = {i, 10, SignalFeature.EXCITATION, SignalFeature.AXON_GROW};
                    addList(objects);
                    addMainList(objects);
                }finally {

                }
            }

        }

        public static synchronized void addList(Object[] objects){
            list.add(0, objects);
            if(list.size()>20){
                list.remove(list.size()-1);
            }
        }

        public static synchronized void addMainList(Object[] objects) {
            mainList.add(0, objects);
            if(list.size()>10){
                list.remove(list.size()-1);
            }
        }

        private static class TestOutputUnit extends HiddenUnit{

            private static Map<Integer, AtomicInteger> map = new ConcurrentHashMap<>();

            private  static long acceptTimestamp = System.currentTimeMillis();

            private static long lastTimestamp = System.currentTimeMillis();

            static {
                new Thread(() -> {
                    while (true) {
                        try {
                            Thread.sleep(100);
                            long s = System.currentTimeMillis();
                            if(!map.isEmpty() && (s>acceptTimestamp|| s>lastTimestamp)){
                                StringJoiner sb = new StringJoiner(",");
                                int[] maxK = {0};
                                int[] maxV = {0};
                                 map.forEach((k,v) -> {
                                    int t= v.get();
                                    if(t>maxV[0]){
                                        maxV[0] = t;
                                        maxK[0] = k;
                                    }
                                    v.getAndAdd(-1*t);
                                    sb.add(k+"("+t+")");
                                });

                                LoggerUtil.print("AI say: "+ maxK[0]+ " ....... all output:"+sb);
                                lastTimestamp = lastTimestamp+30000L;
                                acceptTimestamp = Long.MAX_VALUE;
                                Object[] objects = {maxK[0], 10, SignalFeature.INHIBITION, SignalFeature.AXON_GROW};
                                addMainList(objects);
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
                Object[] objects = {output, 10, SignalFeature.INHIBITION};
                map.computeIfAbsent(output, o->new AtomicInteger(0)).addAndGet(1);
                acceptTimestamp = System.currentTimeMillis() + 10000L;
                addList(objects);
            }

        }
    }
}
