package com.canfuu.cluo.brain.common.util;

import java.util.StringJoiner;

public class IdUtil {
    public static String idToPath(String id) {
        String split1 = id.substring(0,2);
        String split2 = id.substring(2,4);
        String split3 = id.substring(4,6);
        String split4 = id.substring(6,8);
        StringJoiner sj = new StringJoiner("/");
        sj.add(split1);
        sj.add(split2);
        sj.add(split3);
        sj.add(split4);

        return sj+"/";
    }
}
