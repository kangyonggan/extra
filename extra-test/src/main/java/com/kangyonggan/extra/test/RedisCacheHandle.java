package com.kangyonggan.extra.test;


import com.kangyonggan.extra.core.annotation.Handle;
import com.kangyonggan.extra.core.handle.CacheHandle;

/**
 * @author kangyonggan
 * @since 2017/11/4 0004
 */
@Handle(type = Handle.Type.CACHE)
public class RedisCacheHandle implements CacheHandle {

    @Override
    public Object set(String key, Object returnValue, Long expire) {
        return returnValue;
    }

    @Override
    public Object get(String key) {
        return null;
    }

    @Override
    public void delete(String... keys) {

    }
}
