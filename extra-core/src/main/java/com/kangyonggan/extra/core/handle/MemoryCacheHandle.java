package com.kangyonggan.extra.core.handle;

/**
 * @author kangyonggan
 * @since 2017/11/4 0004
 */
public class MemoryCacheHandle implements CacheHandle {

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
