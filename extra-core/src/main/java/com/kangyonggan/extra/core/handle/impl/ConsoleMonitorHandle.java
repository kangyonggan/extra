package com.kangyonggan.extra.core.handle.impl;

import com.kangyonggan.extra.core.handle.MonitorHandle;
import com.kangyonggan.extra.core.model.MonitorInfo;

/**
 * @author kangyonggan
 * @since 11/10/17
 */
public class ConsoleMonitorHandle implements MonitorHandle {

    @Override
    public void error(String errMsg, Exception e, MonitorInfo monitorInfo) {
        if (e == null) {
            System.out.println(errMsg + "\n" + monitorInfo);
        } else {
            System.out.println(errMsg + ", " + e.getMessage() + "\n" + monitorInfo);
        }
    }
}
