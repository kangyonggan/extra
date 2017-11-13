package com.kangyonggan.extra.test;


import com.kangyonggan.extra.core.handle.LogHandle;

/**
 * @author kangyonggan
 * @since 2017/11/4 0004
 */
public class Log4j2LogHandle implements LogHandle {

    private String packageName;

    public Log4j2LogHandle(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public void logBefore(String methodName, Object... params) {

    }

    @Override
    public Object logAfter(String methodName, Long startTime, Object returnValue) {
        return null;
    }

    @Override
    public void log(String methodName, String msg) {

    }
}
