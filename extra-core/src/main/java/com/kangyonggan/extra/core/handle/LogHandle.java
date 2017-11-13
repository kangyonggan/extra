package com.kangyonggan.extra.core.handle;

/**
 * @author kangyonggan
 * @since 2017/11/4 0004
 */
public interface LogHandle {

    void logBefore(String methodName, Object... params);

    Object logAfter(String methodName, Long startTime, Object returnValue);

    void log(String methodName, String msg);
}
