package com.kangyonggan.extra.core.util;

import com.kangyonggan.extra.core.handle.impl.*;
import com.kangyonggan.extra.core.model.Constants;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author kangyonggan
 * @since 2017/11/4 0004
 */
public class PropertiesUtil {

    private static Properties props;

    /**
     * load resources properties
     *
     * @param resourceName
     */
    public static void init(String resourceName) {
        props = new Properties();
        try {
            InputStream in = PropertiesUtil.class.getClassLoader().getResourceAsStream(resourceName);
            props.load(in);
        } catch (Exception e) {
        }

        if (StringUtil.isEmpty(props.getProperty(Constants.CACHE_PREFIX))) {
            props.setProperty(Constants.CACHE_PREFIX, StringUtil.EXPTY);
        }

        if (StringUtil.isEmpty(props.getProperty(Constants.CACHE_EXPIRE))) {
            props.setProperty(Constants.CACHE_EXPIRE, String.valueOf(315360000000L));
        }

        if (StringUtil.isEmpty(props.getProperty(Constants.CACHE_HANDLE))) {
            props.setProperty(Constants.CACHE_HANDLE, MemoryCacheHandle.class.getName());
        }

        if (StringUtil.isEmpty(props.getProperty(Constants.LOG_HANDLE))) {
            props.setProperty(Constants.LOG_HANDLE, ConsoleLogHandler.class.getName());
        }

        if (StringUtil.isEmpty(props.getProperty(Constants.COUNT_PREFIX))) {
            props.setProperty(Constants.COUNT_PREFIX, StringUtil.EXPTY);
        }

        if (StringUtil.isEmpty(props.getProperty(Constants.COUNT_INTERRUPT))) {
            props.setProperty(Constants.COUNT_INTERRUPT, String.valueOf(false));
        }

        if (StringUtil.isEmpty(props.getProperty(Constants.COUNT_HANDLE))) {
            props.setProperty(Constants.COUNT_HANDLE, MemoryCountHandle.class.getName());
        }

        if (StringUtil.isEmpty(props.getProperty(Constants.FREQUENCY_PREFIX))) {
            props.setProperty(Constants.FREQUENCY_PREFIX, StringUtil.EXPTY);
        }

        if (StringUtil.isEmpty(props.getProperty(Constants.FREQUENCY_INTERRUPT))) {
            props.setProperty(Constants.FREQUENCY_INTERRUPT, String.valueOf(false));
        }

        if (StringUtil.isEmpty(props.getProperty(Constants.FREQUENCY_HANDLE))) {
            props.setProperty(Constants.FREQUENCY_HANDLE, MemoryFrequencyHandle.class.getName());
        }

        if (StringUtil.isEmpty(props.getProperty(Constants.VALID_INTERRUPT))) {
            props.setProperty(Constants.VALID_INTERRUPT, String.valueOf(false));
        }

        if (StringUtil.isEmpty(props.getProperty(Constants.VALID_HANDLE))) {
            props.setProperty(Constants.VALID_HANDLE, ConsoleValidHandle.class.getName());
        }

        if (StringUtil.isEmpty(props.getProperty(Constants.MONITOR_APP))) {
            props.setProperty(Constants.MONITOR_APP, StringUtil.EXPTY);
        }

        if (StringUtil.isEmpty(props.getProperty(Constants.MONITOR_TYPE))) {
            props.setProperty(Constants.MONITOR_TYPE, StringUtil.EXPTY);
        }

        if (StringUtil.isEmpty(props.getProperty(Constants.MONITOR_HANDLE))) {
            props.setProperty(Constants.MONITOR_HANDLE, ConsoleMonitorHandle.class.getName());
        }
    }

    public static String getCachePrefix() {
        return props.getProperty(Constants.CACHE_PREFIX);
    }

    public static Long getCacheExpire() {
        return Long.parseLong(props.getProperty(Constants.CACHE_EXPIRE));
    }

    public static String getCacheHandle() {
        return props.getProperty(Constants.CACHE_HANDLE);
    }

    public static String getLogHandle() {
        return props.getProperty(Constants.LOG_HANDLE);
    }

    public static String getCountPrefix() {
        return props.getProperty(Constants.COUNT_PREFIX);
    }

    public static boolean getCountInterrupt() {
        return Boolean.parseBoolean(props.getProperty(Constants.COUNT_INTERRUPT));
    }

    public static String getCountHandle() {
        return props.getProperty(Constants.COUNT_HANDLE);
    }

    public static String getFrequencyPrefix() {
        return props.getProperty(Constants.FREQUENCY_PREFIX);
    }

    public static boolean getFrequencyInterrupt() {
        return Boolean.parseBoolean(props.getProperty(Constants.FREQUENCY_INTERRUPT));
    }

    public static String getFrequencyHandle() {
        return props.getProperty(Constants.FREQUENCY_HANDLE);
    }

    public static boolean getValidInterrupt() {
        return Boolean.parseBoolean(props.getProperty(Constants.VALID_INTERRUPT));
    }

    public static String getValidHandle() {
        return props.getProperty(Constants.VALID_HANDLE);
    }

    public static String getMonitorApp() {
        return props.getProperty(Constants.MONITOR_APP);
    }

    public static String getMonitorType() {
        return props.getProperty(Constants.MONITOR_TYPE);
    }

    public static String getMonitorHandle() {
        return props.getProperty(Constants.MONITOR_HANDLE);
    }

    public static String getMonitorServers() {
        return props.getProperty(Constants.MONITOR_SERVERS);
    }

}
