package com.kangyonggan.extra.annotation;

import com.kangyonggan.extra.handle.MonitorHandle;
import com.kangyonggan.extra.handle.impl.ConsoleMonitorHandle;
import com.kangyonggan.extra.util.StringUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method Monitor
 *
 * @author kangyonggan
 * @since 11/10/17
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Monitor {

    /**
     * monitor type
     *
     * @return
     */
    String type() default StringUtil.EXPTY;

    /**
     * application name
     *
     * @return
     */
    String app() default StringUtil.EXPTY;

    /**
     * monitor handle, default is console handle
     *
     * @return
     */
    Class<? extends MonitorHandle> handle() default ConsoleMonitorHandle.class;

}
