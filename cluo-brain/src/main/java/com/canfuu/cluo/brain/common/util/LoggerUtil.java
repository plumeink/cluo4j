package com.canfuu.cluo.brain.common.util;

public class LoggerUtil {

    public static void log(){

    }

    public static void error(String msg, Throwable t, Object target){
        System.out.println(target+" "+msg);
        if(t!=null){
            t.printStackTrace();
        }
    }
}
