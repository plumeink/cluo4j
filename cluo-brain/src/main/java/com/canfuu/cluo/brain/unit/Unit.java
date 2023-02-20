package com.canfuu.cluo.brain.unit;

import com.canfuu.cluo.brain.signal.OuterSignal;

public interface Unit extends Runnable{
    void accept(byte value);

    void cleanValue();
}