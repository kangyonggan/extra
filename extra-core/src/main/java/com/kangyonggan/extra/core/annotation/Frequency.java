package com.kangyonggan.extra.core.annotation;

import com.kangyonggan.extra.core.handle.FrequencyHandle;
import com.kangyonggan.extra.core.handle.impl.MemoryFrequencyHandle;
import com.kangyonggan.extra.core.util.StringUtil;

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
     * @return none
     */
    String prefix() default StringUtil.EXPTY;

    /**
     * key
     *
     * @return none
     */
    String key() default StringUtil.EXPTY;

    /**
     * method called interval, unit is ms
     *
     * @return none
     */
    long interval();

    /**
     * when over frequency, is interrupt
     *
     * @return none
     */
    boolean interrupt() default false;

    /**
     * limit frequency handle, default is memory limit frequency handle
     *
     * @return none
     */
    Class<? extends FrequencyHandle> handle() default MemoryFrequencyHandle.class;

}
