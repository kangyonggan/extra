package com.kangyonggan.extra.core.handle;

/**
 * @author kangyonggan
 * @since 11/6/17
 */
public interface FrequencyHandle {

    boolean limit(String key, long interval, boolean interrupt);

}
