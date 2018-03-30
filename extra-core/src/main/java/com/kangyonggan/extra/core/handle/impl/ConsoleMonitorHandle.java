package com.kangyonggan.extra.core.handle.impl;

import com.kangyonggan.extra.core.handle.MonitorHandle;
import com.kangyonggan.extra.core.model.MonitorInfo;

/**
 * @author kangyonggan
 * @since 3/30/18
 */
public class ConsoleMonitorHandle implements MonitorHandle {

    @Override
    public Object handle(MonitorInfo monitorInfo) {

        System.out.println("monitorInfo: " + monitorInfo);

        return monitorInfo.getReturnValue();
    }
}
