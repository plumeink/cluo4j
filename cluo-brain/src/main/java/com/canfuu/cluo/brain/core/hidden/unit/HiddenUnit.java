package com.canfuu.cluo.brain.core.hidden.unit;

import com.alibaba.fastjson.annotation.JSONField;
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



    private double savedValue = 0;

    private long lastAboveThresholdTime = System.currentTimeMillis();

    private long lastTransSecond = TimeUtil.currentSecondsInCache();

    @JSONField(deserialize = false,serialize = false)
    private final Lock active = new ReentrantLock();

    public HiddenUnit(){
    }



    @Override
    public void accept(Signal signal) {
        if(preHandle(signal)){
            return;
        }

        trans();
    }



    private void trans() {
        // 缓存区，暂存当前unit对其他unit的连接
        Map<String, List<HiddenUnitLink>> map = new HashMap<>();
        int[] total = {0};
        // 统计连接
        HiddenUnitManager.getAllLinks(getId()).forEach((unitId, links) -> {
            int countInt = links.size();
            map.put(unitId, new ArrayList<>(links));
            total[0] = total[0] + countInt;
        });


        // 遍历统计信息
        map.forEach((unitId, links) -> {
            // 拿到对某个unit的连接数量
            int count = links.size();
            // 算unit数量所占的比例
            // 60 / 100 = 0.6
            double percentage = count*1.0/total[0];
            // 计算应该分配多少value
            // 1500*0.6 = 900
            // 兴奋的传递，是由于达到valueThreshold后，快速吸收周围K+，然后通过轴突向下传递
            // 可是由于是机器，我们可以假设外界的钾离子是无限的也就是，向下传递的离子数量在1000-2000之间
            double realTrans =  CommonConstants.hiddenUnitTransValueCount * percentage;

            // 计算link所包含的囊泡数量总数
            long[] totalLinkCount = {0L};
            // 暂存link的囊泡数量
            List<Integer> countList = new ArrayList<>();
            links.forEach(link -> {
                int temp = link.getCount();
                countList.add(temp);
                totalLinkCount[0]+=temp;
            });
            // 计算每单位囊泡可以被分配多少value
            double valueOfPerRealLink = realTrans/totalLinkCount[0];
            for (int i = 0; i < links.size(); i++) {
                HiddenUnitLink link = links.get(i);
                int realLinkCount = countList.get(i);
                // 如果每单位囊泡包含的value数量大于激活value，则认为囊泡溶解
                if(valueOfPerRealLink > link.getActiveValue()){
                    // 囊泡溶解，向下传递(囊泡数量 * value基本单位)
                    HiddenUnitManager.transToUnit(unitId, new Signal(realLinkCount *  CommonConstants.transValue,link.getFeature()));
                }
            }
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

        SignalFeature features = signal.getFeature();

        if(realValue>0 && features.equals(SignalFeature.INHIBITION)){
            realValue = realValue *-1;
        }else if(realValue<0 && features.equals(SignalFeature.EXCITATION)){
            realValue = realValue*-1;
        }


        if(features.equals(SignalFeature.INHIBITION)){
            active.lock();
            savedValue = savedValue + realValue;
            active.unlock();
            if(signal.isStop()) {
                return false;
            }
        }

        if (HiddenUnitStatus.NO_RESPONSE_ABOVE == status) {
            return false;
        }


        int choosingValue = status.chooseValueByRecordTime(gap);

        active.lock();

        double result = savedValue + realValue;

        if ((result + choosingValue) >= CommonConstants.hiddenUnitValueThreshold) {
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