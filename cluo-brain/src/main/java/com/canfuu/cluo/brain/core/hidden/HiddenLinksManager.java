package com.canfuu.cluo.brain.core.hidden;

import com.canfuu.cluo.brain.common.CommonConstants;
import com.canfuu.cluo.brain.common.Link;
import com.canfuu.cluo.brain.common.Node;
import com.canfuu.cluo.brain.common.Unit;
import com.canfuu.cluo.brain.common.signal.SignalFeature;
import com.canfuu.cluo.brain.common.util.IdUtil;
import com.canfuu.cluo.brain.common.util.LoggerUtil;
import com.canfuu.cluo.brain.core.hidden.unit.HiddenUnit;
import com.canfuu.cluo.brain.core.hidden.unit.HiddenUnitLink;
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
import java.util.concurrent.atomic.AtomicInteger;

public class HiddenLinksManager {

    private Map<Link<String,String>, AtomicInteger> linksMap = new ConcurrentHashMap<>();

    private Map<String, Map<String, Double>> linksRefMap = new ConcurrentHashMap<>();

    private Map<String, LinkedBlockingQueue<String>> new100PercentLinkMap = new ConcurrentHashMap<>();

    private Set<String> ignoreFiles = new HashSet<>();

    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private Map<Path, ScheduledFuture<?>> monitorDir = new HashMap<>();

    private LinkedBlockingQueue<Node<String, Integer>> percentageTaskQueue = new LinkedBlockingQueue<>();

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
                monitorDir.computeIfAbsent(subFile.toPath(), path -> scheduledExecutorService.scheduleWithFixedDelay(new HiddenFileReader(path),0L, 1L, TimeUnit.MILLISECONDS));
            }
        },0L, 1L, TimeUnit.MILLISECONDS);


        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                Node<String, Integer> node = percentageTaskQueue.take();
                String path = node.getKey();
                File tempFile = new File(path);
                File parentFile = tempFile.getParentFile();
                if (!parentFile.exists()) {
                    try {
                        parentFile.mkdirs();
                    } finally {
                    }
                }
                if (!tempFile.exists()) {
                    try {
                        tempFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Path tempPath = tempFile.toPath();
                byte[] bytes = Files.readAllBytes(tempPath);
                String str = new String(bytes, StandardCharsets.UTF_8).trim();
                if(str.length()==0){
                    str = "0";
                }
                Files.write(tempPath, ((Integer.parseInt(str) + node.getValue()) + "").trim().getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                e.printStackTrace();
            }
        },0L, 1L , TimeUnit.MILLISECONDS);
    }
    public void incrementLinkable(String fromUnit, String toUnit, int percentage){
        String path = CommonConstants.unitLinksDir+ IdUtil.idToPath(fromUnit)+toUnit+".ref";
        percentageTaskQueue.offer(new Node<>(path, percentage));
    }

    public Map<Link<String,String>, AtomicInteger> getAllLinks(){
        return linksMap;
    }

    public void createLink(String fromUnit, String toUnit){
        linksMap.computeIfAbsent(new Link<>(fromUnit, toUnit), f->new AtomicInteger(0)).addAndGet(1);
    }

    public void removeLink(String fromUnit, String toUnit) {
        linksMap.remove(new Link<>(fromUnit, toUnit));
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

    public HiddenUnitLink createLink(){

    }
    private class HiddenFileReader implements Runnable{

        private Path path;

        public HiddenFileReader(Path path) {
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
                                    String parentName = subPath.getParent().getFileName().toString();
                                    Map<String, Double> linkMap = linksRefMap.computeIfAbsent(parentName, (key) -> new ConcurrentHashMap<>());

                                    Double newValue = Double.valueOf(s);
                                    String keyName = fileName.substring(0, fileName.length() - CommonConstants.refFileSuffix.length());
                                    Double oldValue = linkMap.put(keyName, newValue);
                                    if((oldValue ==null || oldValue<100) && newValue>=100){
                                        new100PercentLinkMap.computeIfAbsent(parentName, (key) -> new LinkedBlockingQueue<>()).offer(keyName);
                                        Files.deleteIfExists(subPath);
                                        linkMap.remove(keyName);
                                    }
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
