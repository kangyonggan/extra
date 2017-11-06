package com.kangyonggan.extra.test;

import com.kangyonggan.extra.core.handle.LimitCountHandle;

/**
 * @author kangyonggan
 * @since 11/6/17
 */
public class RedisLimitCountHandle implements LimitCountHandle {

    @Override
    public boolean limit(String key, long interval, int count) {
        return false;
    }

    @Override
    public void alarm(String key, long interval, int count) {

    }
}
