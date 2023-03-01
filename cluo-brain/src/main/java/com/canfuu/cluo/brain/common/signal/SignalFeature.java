package com.canfuu.cluo.brain.common.signal;

public enum SignalFeature {
    // 单纯的兴奋
    EXCITATION(85),
    // 抑制
    INHIBITION(15),
    // 轴突生长
    AXON_GROW(50),
    // 轴突退化
    AXON_WILT(50);

    final int percentage;

    SignalFeature(int percentage){
        this.percentage = percentage;
    }

    public int getPercentage() {
        return percentage;
    }
}
