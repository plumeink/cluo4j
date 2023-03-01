package com.canfuu.cluo.brain.core.hidden.unit;

import com.canfuu.cluo.brain.common.CommonConstants;
import com.canfuu.cluo.brain.common.util.TimeUtil;

public enum HiddenUnitStatus {
    // 正常情况
    NORMAL(-70, -70,null,null),

    // 绝对不应期：在组织兴奋后的最初一段时期内，不论再受到多大的刺激，都不能再引起兴奋，这段时间内的兴奋阈值无限大，兴奋性降低到0。
    // 时间相当于动作电位的峰电位时期。这时由于Na通道全部开放，或者全部失活，不能产生Na内流而产生动作电位。
    NO_RESPONSE_ABOVE(-70,-60, 0, 1),

    // 相对不应期：在绝对不应期之后的一段时间内，给予组织大于阈强度刺激，有可能使组织产生新的兴奋性，且低于正常值。
    // 时间相当于负后电位的前半期，这是Na通道只有部分从失活中恢复，说明细胞在这段时间内的兴奋性正处于逐渐恢复的过程，但仍低于正常。
    INSENSITIVE_ABOVE(-60, -80, 1,10),

    // 在相对不应期之后，细胞的可兴奋性可稍高于正常，用低于阈强度的刺激也能引起兴奋。
    // 时间上相当于负后电位的后半期，这时Na通道虽未完全恢复，但是膜电位距离阈电位较近，容易引起兴奋。
    SENSITIVE_ABOVE(-80, -90, 10, 20),

    // 兴奋性低于正常，即需要较强的刺激才能引起兴奋。时间上相当于正后电位。
    // 这时Na通道已经完全恢复，但是膜电位距离阈电位较远，不容易产生兴奋。细胞在经历低常期之后，兴奋性才能完全恢复正常，以阈刺激又能引发一次新的兴奋，即产生动作电位。
    BELOW_NORMAL(-90,-70, 20, 90);

    private final Integer fromV;
    private final Integer toV;
    private final Integer fromT;
    private final Integer toT;

    private final int ratio = CommonConstants.unitStatusTimeRangeRatio;

    HiddenUnitStatus(Integer fromV, Integer toV, Integer fromT, Integer toT) {
        this.fromV = fromV;
        this.fromT = fromT;
        this.toV = toV;
        this.toT = toT;
    }

    public int getFromV() {
        return fromV;
    }

    public int getToV() {
        return toV;
    }

    public int getFromT() {
        return fromT;
    }

    public int getToT() {
        return toT;
    }


    public static HiddenUnitStatus chooseByRecordTime(long gap) {

        if(NO_RESPONSE_ABOVE.timeGapIsInRange(gap)){
            return NO_RESPONSE_ABOVE;
        }
        if(INSENSITIVE_ABOVE.timeGapIsInRange(gap)){
            return INSENSITIVE_ABOVE;
        }
        if(SENSITIVE_ABOVE.timeGapIsInRange(gap)){
            return SENSITIVE_ABOVE;
        }
        if(BELOW_NORMAL.timeGapIsInRange(gap)){
            return BELOW_NORMAL;
        }
        return NORMAL;
    }

    public boolean timeGapIsInRange(long gap) {

        if (gap == 0 && NO_RESPONSE_ABOVE.equals(this)) {
            return true;
        }
        if (toT != null && fromT != null && gap <= toT && gap > fromT) {
            return true;
        }

        return false;
    }

    public int chooseValueByRecordTime(long gap) {
        if(NORMAL.equals(this)){
            return fromV;
        }
        if(NO_RESPONSE_ABOVE.equals(this)){
            return 0;
        }
        double gapWithFromT = gap-fromT;
        double totalT = toT-fromT;
        double m = gapWithFromT/ totalT;
        double range = toV - fromV;
        double dx =  m * range;
        return Double.valueOf(fromV + dx).intValue();
    }
}
