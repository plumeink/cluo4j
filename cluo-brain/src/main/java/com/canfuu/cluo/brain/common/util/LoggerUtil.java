package com.canfuu.cluo.brain.common.util;

public class LoggerUtil {

    public static void log(Object obj, String info){

    }

    public static void print(String info) {
        System.out.println(info);
    }

    public static void error(String msg, Throwable t, Object target){
        System.out.println(target+" "+msg);
        if(t!=null){
            t.printStackTrace();
        }
    }
}
