package com.kangyonggan.extra.handle.impl;

import com.kangyonggan.extra.handle.ValidHandle;

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
