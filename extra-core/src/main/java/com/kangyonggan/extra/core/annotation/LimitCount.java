package com.kangyonggan.extra.core.annotation;

import com.kangyonggan.extra.core.handle.LimitCountHandle;
import com.kangyonggan.extra.core.handle.impl.MemoryLimitCountHandle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * limit method called count
 *
 * @author kangyonggan
 * @since 11/6/17
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface LimitCount {

    /**
     * method called interval, unit is ms
     *
     * @return
     */
    long interval();

    /**
     * method called count druing the interval time
     *
     * @return
     */
    int count();

    /**
     * limit count handle, default is memory limit count handle
     *
     * @return
     */
    Class<? extends LimitCountHandle> handle() default MemoryLimitCountHandle.class;

}
