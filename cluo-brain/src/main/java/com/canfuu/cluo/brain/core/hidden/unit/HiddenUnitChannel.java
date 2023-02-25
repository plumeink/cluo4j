package com.canfuu.cluo.brain.core.hidden.unit;

import com.canfuu.cluo.brain.common.CommonConstants;
import com.canfuu.cluo.brain.common.CommonEntity;
import com.canfuu.cluo.brain.common.signal.SignalFeature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
public class HiddenUnitChannel extends CommonEntity {

    private final static Executor executor = Executors.newCachedThreadPool();

    private final Map<HiddenUnitChannel, Integer> channels = new HashMap<>();

    public HiddenUnitChannel() {
    }

    /**
     * 异步执行的优点：不会有差异性
     * 同步执行的优点：省资源
     *
     * @param value
     * @param signalFeature
     * @return
     */
    public void transValue(int value, SignalFeature signalFeature){
        Runnable trans = () -> {
            if(channels.size()>0) {
                // 接收到信号，有有效通路，则根据比例向下传递
                channels.forEach((channel, percentage) -> {
                    channel.transValue(value * percentage / 100, signalFeature);
                });
            }
        };
        Runnable grow = () -> {
            if(value == 0){
                // 没有接收到信息，需要退化一点点
                wilt();
            } else if(SignalFeature.AXON_GROW.equals(signalFeature)){
                grow();
            } else if(SignalFeature.AXON_WILT.equals(signalFeature)){
                wilt();
            }
        };
        if(CommonConstants.asyncChannel) {
            executor.execute(trans);
            executor.execute(grow);
        } else {
            trans.run();
            grow.run();
        }

    }

    /**
     * 一个是长度生长
     *   一般情况下，如果channels是0的时候会选择长度生长，由于真实情况存在物理位置信息，因此channel和下一个unit的是依靠距离。
     *   但是程序中没有距离的概念，是否可以认为长度生长在程序中就是一个对某个unit的连接完成度？
     */
    public void grow() {

    }

    public void wilt() {

    }
}