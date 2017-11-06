package com.kangyonggan.extra.test;

import com.kangyonggan.extra.core.handle.CountHandle;

/**
 * @author kangyonggan
 * @since 11/6/17
 */
public class RedisCountHandle implements CountHandle {

    @Override
    public boolean limit(String key, long interval, int count, boolean interrupt) {
        return false;
    }

}
