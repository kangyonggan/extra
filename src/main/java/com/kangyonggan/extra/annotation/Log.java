package com.kangyonggan.extra.annotation;

import com.kangyonggan.extra.handle.impl.ConsoleLogHandler;
import com.kangyonggan.extra.handle.LogHandle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author kangyonggan
 * @since 2017/11/4 0004
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Log {

    /**
     * log handle, default is console log handle
     *
     * @return
     */
    Class<? extends LogHandle> handle() default ConsoleLogHandler.class;

}
