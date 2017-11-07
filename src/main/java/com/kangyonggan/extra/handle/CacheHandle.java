package com.kangyonggan.extra.handle;

/**
 * @author kangyonggan
 * @since 10/31/17
 */
public interface CacheHandle {

    Object set(String key, Object returnValue, Long expire);

    Object get(String key);

    void delete(String... keys);
}
