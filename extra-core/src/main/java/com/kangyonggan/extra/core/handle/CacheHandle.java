package com.kangyonggan.extra.core.handle;

/**
 * @author kangyonggan
 * @since 10/31/17
 */
public interface CacheHandle {

    Object set(String key, Object value, Long expire);

    Object get(String key);

    void delete(String key);
}
