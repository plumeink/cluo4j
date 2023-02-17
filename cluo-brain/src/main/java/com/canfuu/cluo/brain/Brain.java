package com.canfuu.cluo.brain;

import com.canfuu.cluo.brain.common.BrainCallback;
import com.canfuu.cluo.brain.signal.Signal;
import com.canfuu.cluo.brain.unit.Unit;

import javax.security.auth.callback.Callback;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Brain {

    private List<Unit> inputs = new ArrayList<>();
    private List<Unit> outputs = new ArrayList<>();
    public Brain() {
    }

    public void teach(String text, String except) {

        byte[] bytes = except.getBytes(StandardCharsets.UTF_8);

        for (int i = 0; i < bytes.length; i++) {
            Signal signal = new Signal();
            signal.data = bytes[i];
            if(outputs.size()==i){
                outputs.add(new Unit());
            }
            Unit unit = outputs.get(i);
            for (int j = 0; j < unit.outputs.size(); j++) {
                unit.outputs.get(j);
            }

        }

    }
}
