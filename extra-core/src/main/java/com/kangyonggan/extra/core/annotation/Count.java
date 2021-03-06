package com.kangyonggan.extra.core.annotation;

import com.kangyonggan.extra.core.handle.CountHandle;
import com.kangyonggan.extra.core.handle.impl.MemoryCountHandle;
import com.kangyonggan.extra.core.util.StringUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * method called count
 *
 * @author kangyonggan
 * @since 11/6/17
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Count {

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
     * method called count druing the interval time
     *
     * @return none
     */
    int count();

    /**
     * when over count, is interrupt
     *
     * @return none
     */
    boolean interrupt() default false;

    /**
     * count handle, default is memory count handle
     *
     * @return none
     */
    Class<? extends CountHandle> handle() default MemoryCountHandle.class;

}
