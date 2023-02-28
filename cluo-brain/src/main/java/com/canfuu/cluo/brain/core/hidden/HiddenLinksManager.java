package com.canfuu.cluo.brain.core.hidden;

import com.canfuu.cluo.brain.common.CommonConstants;
import com.canfuu.cluo.brain.common.Node;
import com.canfuu.cluo.brain.common.Unit;
import com.canfuu.cluo.brain.common.signal.SignalFeature;
import com.canfuu.cluo.brain.common.util.IdUtil;
import com.canfuu.cluo.brain.common.util.LoggerUtil;
import com.canfuu.cluo.brain.core.hidden.unit.HiddenUnit;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

public class HiddenLinksManager {

    private Map<String, Map<String, Double>> linksMap = new ConcurrentHashMap<>();

    private Map<String, LinkedBlockingQueue<String>> new100PercentLinkMap = new ConcurrentHashMap<>();

    private Set<String> ignoreFiles = new HashSet<>();

    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private LinkedBlockingQueue<Node<String, Integer>> percentageTaskQueue = new LinkedBlockingQueue<>();

    public HiddenLinksManager() {
        File file = new File(CommonConstants.unitLinksDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        for (File listFile : file.listFiles()) {
            scheduledExecutorService.scheduleAtFixedRate(new HiddenFileReader(listFile.getAbsolutePath()),0L, 0L, TimeUnit.MILLISECONDS);
        }
        ignoreFiles.add(".DS_Store");

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            Node<String, Integer> node = percentageTaskQueue.poll();
            String path = node.getKey();
            File tempFile = new File(path);
            File parentFile = tempFile.getParentFile();
            if(!parentFile.exists()){
                try {
                    parentFile.mkdirs();
                }finally {
                }
            }
            if(!tempFile.exists()){
                try {
                    tempFile.createNewFile();
                } catch (IOException ignore) {
                }
            }
            Path tempPath = tempFile.toPath();
            try {
                byte[] bytes = Files.readAllBytes(tempPath);
                String str = new String(bytes,StandardCharsets.UTF_8).trim();
                Files.write(tempPath,((Integer.parseInt(str)+ node.getValue())+"").getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        },0 , 0 , TimeUnit.MILLISECONDS);
    }
    public void incrementLinkable(String fromUnit, String toUnit, int percentage){
        String path = CommonConstants.unitLinksDir+ IdUtil.idToPath(fromUnit)+toUnit+".ref";
        percentageTaskQueue.offer(new Node<>(path, percentage));
    }


    public List<String> findNew100PercentLinkByUnitId(String unitId){
        LinkedBlockingQueue<String> queue = new100PercentLinkMap.get(unitId);
        if(queue==null){
            return new ArrayList<>();
        }
        List<String> result = new ArrayList<>();
        String elem = null;
        while ((elem=queue.poll())!=null){
            result.add(elem);
        }
        return result;
    }

    public int findActiveValue(String myUnitId, String targetUnitId) {
        return 10;
    }

    public int findTransValue(String myUnitId, String targetUnitId) {
        return 4;
    }

    public SignalFeature[] findFeature(String myUnitId, String targetUnitId) {
        Random random = new Random();
        List<SignalFeature> features = new ArrayList<>();
        for (SignalFeature feature : SignalFeature.values()) {
            if(random.nextInt(100)< feature.getPercentage()){
                features.add(feature);
            }
        }
        return features.toArray(new SignalFeature[0]);
    }

    private class HiddenFileReader implements Runnable{

        private String path;

        public HiddenFileReader(String path) {
            this.path = path;
        }

        @Override
        public void run() {
            try {
                Files.walk(Paths.get(path)).forEach(subPath -> {
                    if(subPath.endsWith(Paths.get(CommonConstants.refFileSuffix))){
                        try {
                            String s = new String(Files.readAllBytes(subPath), StandardCharsets.UTF_8);
                            String fileName = subPath.getFileName().toString();

                            if(s.length()>0) {
                                String parentName = subPath.getParent().getFileName().toString();
                                Map<String, Double> linkMap = linksMap.computeIfAbsent(parentName, (key) -> new ConcurrentHashMap<>());

                                Double newValue = Double.valueOf(s);
                                String keyName = fileName.substring(0, fileName.length() - CommonConstants.refFileSuffix.length());
                                Double oldValue = linkMap.put(keyName, newValue);

                                if((oldValue ==null || oldValue<100) && newValue>=100){
                                    new100PercentLinkMap.computeIfAbsent(parentName, (key) -> new LinkedBlockingQueue<>()).offer(keyName);
                                }
                            }
                        } catch (IOException e) {
                            LoggerUtil.error("read links error.", e, this);
                            return;
                        }
                    }
                });
            } catch (IOException e) {
                LoggerUtil.error("walk error." , e, this);
            }
        }
    }

}
