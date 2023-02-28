package com.canfuu.cluo.brain.core.hidden;

import com.canfuu.cluo.brain.common.CommonConstants;
import com.canfuu.cluo.brain.common.Link;
import com.canfuu.cluo.brain.common.Node;
import com.canfuu.cluo.brain.common.signal.Signal;
import com.canfuu.cluo.brain.common.signal.SignalFeature;
import com.canfuu.cluo.brain.core.hidden.unit.HiddenUnitChannel;
import com.canfuu.cluo.brain.core.hidden.unit.HiddenUnitOutputChannel;
import com.canfuu.cluo.brain.support.UnitCenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class HiddenUnitManager {

    private static HiddenLinksManager hiddenLinksManager = new HiddenLinksManager();


    public static void init(){

    }
    public static void transToUnit(String unitId, Signal signal) {
        UnitCenter.findById(unitId).accept(signal);
    }

    public static Map<Link<String,String>, AtomicInteger> getAllLinks(){
        return hiddenLinksManager.getAllLinks();
    }

    public static void removeLink(String fromId, String toId) {
        hiddenLinksManager.removeLink(fromId, toId);
    }

    public static List<HiddenUnitOutputChannel> wantToLinkOther(HiddenUnitChannel channel){
        String myUnitId = channel.getMyUnitId();
        String chooseUnitId = UnitCenter.chooseUnit(myUnitId);

        if(chooseUnitId==null){
            return null;
        }

        hiddenLinksManager.incrementLinkable(myUnitId, chooseUnitId, CommonConstants.linkSpeed);

        List<String> new100PercentLink = hiddenLinksManager.findNew100PercentLinkByUnitId(myUnitId);
        List<HiddenUnitOutputChannel> result = new ArrayList<>();
        if(new100PercentLink!=null){
            new100PercentLink.forEach(targetUnitId -> {
                SignalFeature[] features = hiddenLinksManager.findFeature(myUnitId, targetUnitId);
                HiddenUnitOutputChannel hiddenUnitOutputChannel = new HiddenUnitOutputChannel(
                        channel,
                        targetUnitId,
                        hiddenLinksManager.findActiveValue(myUnitId, targetUnitId),
                        hiddenLinksManager.findTransValue(myUnitId,targetUnitId),
                        new AtomicLong(0),
                        features
                );
                result.add(hiddenUnitOutputChannel);
                hiddenLinksManager.createLink(myUnitId, targetUnitId);
            });
            if(result.isEmpty()){
                return null;
            }
            return result;
        }
        return null;
    }

    public static void notWantToLinkOther(HiddenUnitChannel channel) {
        String myUnitId = channel.getMyUnitId();
        String chooseUnitId = UnitCenter.chooseUnit(myUnitId);
        if(chooseUnitId==null){
            return;
        }
        hiddenLinksManager.incrementLinkable(myUnitId, chooseUnitId, CommonConstants.breakLinkSpeed);
    }
}
