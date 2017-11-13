package com.kangyonggan.extra.core.handle.impl;

import com.kangyonggan.extra.core.exception.MethodCalledFrequencyException;
import com.kangyonggan.extra.core.handle.FrequencyHandle;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kangyonggan
 * @since 11/6/17
 */
public class MemoryFrequencyHandle implements FrequencyHandle {

    private static Map<String, Long> map = new HashMap();

    @Override
    public void limit(String key, long interval, boolean interrupt) {
        boolean isLimited = isLimited(key, interval);
        if (isLimited) {
            String msg = String.format("Method %s called frequency during %sms times.", key, interval);
            System.out.println(msg);
            if (interrupt) {
                throw new MethodCalledFrequencyException(msg);
            }
        }
    }

    private synchronized boolean isLimited(String key, Long interval) {
        Long lastTime = getLastTime(key);
        Long currentTime = System.currentTimeMillis();
        if (interval > currentTime - lastTime) {
            return true;
        }

        map.put(key, currentTime);
        return false;
    }

    private Long getLastTime(String key) {
        Long lastTime = map.get(key);
        if (lastTime == null) {
            lastTime = 0L;
            map.put(key, lastTime);
        }

        return lastTime;
    }

}
