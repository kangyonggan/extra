package com.kangyonggan.extra;

import com.kangyonggan.extra.handle.FrequencyHandle;

/**
 * @author kangyonggan
 * @since 11/6/17
 */
public class RedisFrequencyHandle implements FrequencyHandle {

    @Override
    public boolean limit(String key, long interval, boolean interrupt) {
        return false;
    }

}
