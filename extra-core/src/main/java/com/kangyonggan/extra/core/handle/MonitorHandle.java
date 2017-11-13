package com.kangyonggan.extra.core.handle;


import com.kangyonggan.extra.core.model.MonitorInfo;

/**
 * @author kangyonggan
 * @since 11/10/17
 */
public interface MonitorHandle {

    void error(String errMsg, Exception e, MonitorInfo monitorInfo);

}
