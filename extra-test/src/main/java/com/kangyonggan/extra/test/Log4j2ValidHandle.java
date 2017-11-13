package com.kangyonggan.extra.test;


import com.kangyonggan.extra.core.handle.ValidHandle;

/**
 * @author kangyonggan
 * @since 11/7/17
 */
public class Log4j2ValidHandle implements ValidHandle {

    @Override
    public void failure(RuntimeException e) {
        System.err.println("参数校验异常:" + e);
    }
}
