package com.canfuu.cluo.brain.core.hidden.unit;

import com.canfuu.cluo.brain.common.CommonConstants;
import com.canfuu.cluo.brain.common.CommonEntity;
import com.canfuu.cluo.brain.common.Unit;
import com.canfuu.cluo.brain.common.signal.Signal;
import com.canfuu.cluo.brain.common.signal.SignalFeature;
import com.canfuu.cluo.brain.common.util.TimeUtil;
import com.canfuu.cluo.brain.core.hidden.HiddenUnitManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 神经元
 */
public class HiddenUnit extends CommonEntity implements Unit{

    /**
     * 通道应该具备:
     *   每个通道都有自己所占上层组织的百分比
     *   对于root通道来说，所占100%
     *   对于以下所有叶子节点的百分比是可调控的，调控依据叶子节点所
     * 通道是可以被扩张和缩减的，具体体现为unit接收到什么样的信号，如果是扩张信号，就需要扩张；如果是缩减信号，就需要缩减
     * 实际情况中，扩张和缩减体现在长度和宽度上
     * 但是，由于我们的unit的每次动作电位所带的电量是一定的（正常情况人脑中的+离子是有限的，但是我们的程序可以是无限的）
     * 因此，虽然宽度代表着允许通行的电位的最大值，这里不会体现
     *
     * 扩张和缩减仅体现在长度上
     * 长度代表着可能具有的channels数量，即允许的channels的最大值
     *
     * 扩张:
     *   扩张信号会传递给下层channels
     *   若
     */
    private Map<String, List<HiddenUnitLink>> linkMap = new ConcurrentHashMap<>();

    //达到这个电位，就会开始传递信息
    private int valueThreshold = CommonConstants.hiddenUnitValueThreshold;

    // 兴奋的传递，是由于达到valueThreshold后，快速吸收周围K+，然后通过轴突向下传递
    // 可是由于是机器，我们可以假设外界的钾离子是无限的也就是，向下传递的离子数量在1000-2000之间
    private int transValue = CommonConstants.hiddenUnitTransValueCount;

    private double savedValue = 0;

    private long lastAboveThresholdTime = System.currentTimeMillis();

    private long lastTransSecond = TimeUtil.currentSecondsInCache();

    private final Lock active = new ReentrantLock();

    public HiddenUnit(){
    }

    public void addLink(HiddenUnitLink link){
        linkMap.computeIfAbsent(this.getId(), uId-> Collections.synchronizedList(new ArrayList<>())).add(link);
    }

    @Override
    public void removeLink(String toId) {
        linkMap.get(this.getId()).removeIf(link -> toId.equals(link.getToUnitId()));
    }


    @Override
    public void accept(Signal signal) {
        if(preHandle(signal)){
            return;
        }

        trans();
    }



    private void trans() {
        Map<String, List<HiddenUnitLink>> map = new HashMap<>();
        int[] total = {0};
        linkMap.forEach((unitId, links) -> {
            int countInt = links.size();
            map.put(unitId, new ArrayList<>(links));
            total[0] = total[0] + countInt;
        });

        map.forEach((unitId, links) -> {

            int count = links.size();
            double percentage = count*1.0/total[0];
            double realTrans =  transValue * percentage;
            long[] useTotal = {0};
            Map<Long, HiddenUnitLink> tempLinkMap = new HashMap<>();
            for (int i = 0; i < links.size(); i++) {
                HiddenUnitLink hiddenUnitLink = links.get(i);
                long useCount = hiddenUnitLink.getUseCount();
                tempLinkMap.put(useCount, hiddenUnitLink);
                useTotal[0]=useTotal[0]+useCount;
            }

            tempLinkMap.forEach((useCount, link)->{
                double transCount = (useCount*realTrans)/useTotal[0];
                double times = transCount/link.getSignal().value();
                if(times<1){
                    HiddenUnitManager.transValueLessThanOne(link.getFromUnitId(), link.getToUnitId());
                    return;
                }
                for (int i = 0; i < times; i++) {
                    HiddenUnitManager.transToUnit(link.getToUnitId(), link.createSignal());
                }
            });

        });
    }

    private void wiltBySeconds(long gapSeconds) {
        HiddenUnitManager.notWantToLinkOther(this.getId(), gapSeconds);
    }

    private void grow() {
        HiddenUnitManager.wantToLinkOther(this.getId());
    }

    private boolean preHandle(Signal signal) {

        if(signal.isAxonGrow()){
            grow();
            return false;
        }

        long currentTime = TimeUtil.currentTime();

        long gap = currentTime - lastAboveThresholdTime;

        HiddenUnitStatus status = HiddenUnitStatus.chooseByRecordTime(gap);

        double realValue = signal.value();

        if (HiddenUnitStatus.INSENSITIVE_ABOVE == status) {

            // 系数还需要调研
            realValue = realValue * 0.8;

        } else if (HiddenUnitStatus.BELOW_NORMAL == status) {

            // 系数还需要调研
            realValue = realValue * 0.9;
        }else if (HiddenUnitStatus.SENSITIVE_ABOVE == status) {

            // 系数还需要调研
            realValue = realValue * 1.2;
        }

        HashSet<SignalFeature> features = signal.getFeatures();

        if(realValue>0 && features.contains(SignalFeature.INHIBITION_CALCULATE)){
            realValue = realValue *-1;
        }else if(realValue<0 && features.contains(SignalFeature.EXCITATION)){
            realValue = realValue*-1;
        }


        if(features.contains(SignalFeature.INHIBITION_STOP) && features.contains(SignalFeature.INHIBITION_CALCULATE)){
            active.lock();
            savedValue = savedValue + realValue;
            active.unlock();
            return false;
        }

        if (HiddenUnitStatus.NO_RESPONSE_ABOVE == status) {
            return false;
        }


        int choosingValue = status.chooseValueByRecordTime(gap);

        active.lock();

        double result = savedValue + realValue;

        if ((result + choosingValue) >= valueThreshold) {
            savedValue = 0;

            lastAboveThresholdTime = currentTime;

            active.unlock();
        } else {
            savedValue = result;

            active.unlock();
            return false;
        }

        long currentSeconds = TimeUtil.currentSecondsInCache();

        long transGap = currentSeconds-lastTransSecond;

        if(transGap>CommonConstants.forgetSecondsToTriggerWilt){
           wiltBySeconds(transGap);
        }

        lastTransSecond = currentSeconds;
        return true;
    }


    @Override
    public String toString() {

        long currentTime = TimeUtil.currentTime();

        long gap = currentTime - lastAboveThresholdTime;

        return getId() + " "+HiddenUnitStatus.chooseByRecordTime(gap) + " " + savedValue+" link: ";
    }
}