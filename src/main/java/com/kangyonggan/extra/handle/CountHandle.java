package com.kangyonggan.extra.handle;

/**
 * @author kangyonggan
 * @since 11/6/17
 */
public interface CountHandle {

    void limit(String key, long interval, int count, boolean interrupt);

}
