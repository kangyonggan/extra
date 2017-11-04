package com.kangyonggan.extra.test;

import com.kangyonggan.extra.core.handle.CacheHandle;

/**
 * @author kangyonggan
 * @since 2017/11/4 0004
 */
public class RedisCacheHandle implements CacheHandle {

    @Override
    public Object set(String key, Object value, Long expire) {
        return value;
    }

    @Override
    public Object get(String key) {
        return null;
    }

    @Override
    public void delete(String key) {

    }
}
