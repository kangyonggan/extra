package com.kangyonggan.extra.core.handle.impl;

import com.kangyonggan.extra.core.handle.CacheHandle;
import com.kangyonggan.extra.core.model.CacheItem;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kangyonggan
 * @since 2017/11/4 0004
 */
public class MemoryCacheHandle implements CacheHandle {

    private volatile static Map<String, CacheItem> caches = new HashMap();

    @Override
    public Object set(String key, Object returnValue, Long expire) {
        caches.put(key, new CacheItem(returnValue, expire));
        return returnValue;
    }

    @Override
    public Object get(String key) {
        CacheItem cacheItem = caches.get(key);
        if (cacheItem == null) {
            return null;
        }

        if (cacheItem.isExpire()) {
            // remove expire cache
            caches.remove(key);
            return null;
        }

        return cacheItem.getValue();
    }

    @Override
    public void delete(String... keys) {
        for (String key : keys) {
            caches.remove(key);
        }
    }
}
