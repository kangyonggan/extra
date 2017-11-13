package com.kangyonggan.extra.core.handle.impl;

import com.kangyonggan.extra.core.handle.ValidHandle;

/**
 * @author kangyonggan
 * @since 11/7/17
 */
public class ConsoleValidHandle implements ValidHandle {

    @Override
    public void failure(RuntimeException e) {
        System.out.println("Method Arguments Valid Failure: " + e.getMessage());
    }
}
