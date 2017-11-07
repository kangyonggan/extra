package com.kangyonggan.extra.handle.impl;

import com.alibaba.fastjson.JSON;
import com.kangyonggan.extra.handle.LogHandle;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author kangyonggan
 * @since 2017/11/4 0004
 */
public class ConsoleLogHandler implements LogHandle {

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private String packageName;

    public ConsoleLogHandler(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public void logBefore(String methodName, Object... params) {
        log(methodName, String.format("method args：%s", JSON.toJSONString(params)));
    }

    @Override
    public Object logAfter(String methodName, Long startTime, Object returnValue) {
        log(methodName, String.format("method return：%s", JSON.toJSONString(returnValue)));
        log(methodName, String.format("method used time：%dms", System.currentTimeMillis() - startTime));
        return returnValue;
    }

    @Override
    public void log(String methodName, String msg) {
        System.out.println(String.format("[INFO ] %s [%s]<%s> - %s", format.format(new Date()), packageName, methodName, msg));
    }
}
