package com.kangyonggan.extra.core.util;

import com.kangyonggan.extra.core.handle.impl.ConsoleLogHandler;
import com.kangyonggan.extra.core.handle.impl.MemoryCacheHandle;
import com.kangyonggan.extra.core.handle.impl.MemoryLimitCountHandle;
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

        if (StringUtil.isEmpty(props.getProperty(Constants.LIMIT_COUNT_HANDLE))) {
            props.setProperty(Constants.LIMIT_COUNT_HANDLE, MemoryLimitCountHandle.class.getName());
        }
    }

    public static String getCachePrefix() {
        return props.getProperty(Constants.CACHE_PREFIX);
    }

    public static String getCacheExpire() {
        return props.getProperty(Constants.CACHE_EXPIRE);
    }

    public static String getCacheHandle() {
        return props.getProperty(Constants.CACHE_HANDLE);
    }

    public static String getLogHandle() {
        return props.getProperty(Constants.LOG_HANDLE);
    }

    public static String getLimitCountHandle() {
        return props.getProperty(Constants.LIMIT_COUNT_HANDLE);
    }

}
