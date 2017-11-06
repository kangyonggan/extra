package com.kangyonggan.extra.core.handle.impl;

import com.kangyonggan.extra.core.handle.LimitCountHandle;
import com.kangyonggan.extra.core.help.LimitCountHelp;

/**
 * @author kangyonggan
 * @since 11/6/17
 */
public class MemoryLimitCountHandle implements LimitCountHandle {

    @Override
    public boolean limit(String key, long interval, int count) {
        boolean isLimited = LimitCountHelp.isLimited(key, interval, count);
        if (isLimited) {
            alarm(key, interval, count);
        }
        return isLimited;
    }

    @Override
    public void alarm(String key, long interval, int count) {
        System.err.println(String.format("Method %s during %dms was called %d times.", key, interval, count));
    }

}
