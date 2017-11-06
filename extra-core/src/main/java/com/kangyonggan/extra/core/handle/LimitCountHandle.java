package com.kangyonggan.extra.core.handle;

/**
 * @author kangyonggan
 * @since 11/6/17
 */
public interface LimitCountHandle {

    boolean limit(String key, long interval, int count);

    void alarm(String key, long interval, int count);

}
