package com.kangyonggan.extra.handle.impl;

import com.kangyonggan.extra.handle.FrequencyHandle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kangyonggan
 * @since 11/6/17
 */
public class MemoryFrequencyHandle implements FrequencyHandle {

    private static Map<String, Long> map = new HashMap();
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public boolean limit(String key, long interval, boolean interrupt) {
        boolean isLimited = isLimited(key, interval);
        if (isLimited) {
            System.out.println(String.format("[%s] Method %s %dms times can called one times.interrupt is %s, next code will be %s", format.format(new Date()), key, interval, interrupt, interrupt ? "interrupt" : "continue"));
        }

        return isLimited;
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
