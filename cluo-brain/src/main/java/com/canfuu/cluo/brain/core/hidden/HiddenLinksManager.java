package com.canfuu.cluo.brain.core.hidden;

import com.canfuu.cluo.brain.common.CommonConstants;
import com.canfuu.cluo.brain.common.Link;
import com.canfuu.cluo.brain.common.Node;
import com.canfuu.cluo.brain.common.util.LoggerUtil;
import com.canfuu.cluo.brain.core.hidden.unit.HiddenUnitLink;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class HiddenLinksManager {

    private Set<String> ignoreFiles = new HashSet<>();

    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private ExecutorService taskExecutor = Executors.newCachedThreadPool();

    private Map<Path, ScheduledFuture<?>> monitorDir = new HashMap<>();

    private static final int TASK_UNIT_GROW = 1;
    private static final int TASK_UNIT_WILT = 1;

    public HiddenLinksManager() {
        File file = new File(CommonConstants.unitLinksDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        ignoreFiles.add(".DS_Store");

        scheduledExecutorService.scheduleWithFixedDelay(() ->{
            for (File subFile : file.listFiles()) {
                if(ignoreFiles.contains(subFile.getName())){
                    continue;
                }
                monitorDir.computeIfAbsent(subFile.toPath(), path -> scheduledExecutorService.scheduleWithFixedDelay(new RefFileReader(path), 0L, 1L, TimeUnit.MILLISECONDS));
            }
        },0L, 1L, TimeUnit.MILLISECONDS);

    }

    public void incrementLinkable(String unitId){
        taskExecutor.submit(new UnitGrow(unitId));
    }

    public Map<String, List<HiddenUnitLink>> getAllLinks(String unitId){
        return new HashMap<>();
    }

    public void  decrementLinkable(String unitId, long seconds) {
        taskExecutor.submit(new UnitWilt(unitId, seconds));
    }

    private static class UnitGrow implements Runnable{

        private final String unitId;

        public UnitGrow(String unitId){
            this.unitId = unitId;
        }

        @Override
        public void run() {

        }
    }

    private static class UnitWilt implements Runnable{

        private final String unitId;
        private final long seconds;

        public UnitWilt(String unitId, long seconds){
            this.unitId = unitId;
            this.seconds = seconds;
        }

        @Override
        public void run() {

        }
    }

    private static class RefFileReader implements Runnable{

        private Path path;

        public RefFileReader(Path path) {
            this.path = path;
        }

        @Override
        public void run() {
            try {
                Files.walk(path).forEach(subPath -> {
                    try {
                        if(subPath.toString().endsWith(CommonConstants.refFileSuffix)){
                            try {
                                String s = new String(Files.readAllBytes(subPath), StandardCharsets.UTF_8).trim();
                                String fileName = subPath.getFileName().toString();

                                if(s.length()>0) {

                                }
                            } catch (IOException e) {
                                LoggerUtil.error("read links error.", e, this);
                                return;
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                });
            } catch (IOException e) {
                LoggerUtil.error("walk error." , e, this);
            }
        }
    }

}
