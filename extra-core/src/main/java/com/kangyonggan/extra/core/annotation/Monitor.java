package com.kangyonggan.extra.core.annotation;

import com.kangyonggan.extra.core.handle.MonitorHandle;
import com.kangyonggan.extra.core.util.StringUtil;

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
     * monitor app
     *
     * @return none
     */
    String app() default StringUtil.EXPTY;

    /**
     * monitor type
     *
     * @return none
     */
    String type() default StringUtil.EXPTY;

    /**
     * description the monitor
     *
     * @return none
     */
    String description() default StringUtil.EXPTY;

    /**
     * monitor handle, default is console monitor
     *
     * @return none
     */
    Class<? extends MonitorHandle> handle() default MonitorHandle.class;

}
