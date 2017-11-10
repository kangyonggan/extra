package com.kangyonggan.extra.annotation;

import com.kangyonggan.extra.handle.FrequencyHandle;
import com.kangyonggan.extra.handle.impl.MemoryFrequencyHandle;
import com.kangyonggan.extra.util.StringUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * limit method called frequency
 *
 * @author kangyonggan
 * @since 11/6/17
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Frequency {

    /**
     * key prefix
     *
     * @return
     */
    String prefix() default StringUtil.EXPTY;

    /**
     * key
     *
     * @return
     */
    String key() default StringUtil.EXPTY;

    /**
     * method called interval, unit is ms
     *
     * @return
     */
    long interval();

    /**
     * when over frequency, is interrupt
     *
     * @return
     */
    boolean interrupt() default false;

    /**
     * limit frequency handle, default is memory limit frequency handle
     *
     * @return
     */
    Class<? extends FrequencyHandle> handle() default MemoryFrequencyHandle.class;

}
