package com.kangyonggan.extra.core.handle;

/**
 * @author kangyonggan
 * @since 11/6/17
 */
public interface CountHandle {

    boolean limit(String key, long interval, int count, boolean interrupt);

}
