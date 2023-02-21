package com.canfuu.cluo.brain.core.output;

import com.canfuu.cluo.brain.common.CommonEntity;
import com.canfuu.cluo.brain.common.Unit;

import java.util.concurrent.LinkedBlockingQueue;

public class OutputUnit  extends CommonEntity implements Unit {




    @Override
    public void linkToUnit(Unit unit) {

    }

    @Override
    public void accept(byte value) {
        System.out.print(value+",");
    }

    @Override
    public void cleanValue() {

    }

    @Override
    public boolean isPositive() {
        return false;
    }

    @Override
    public void wantNextUnit() {

    }

    @Override
    public void run() {

    }
}