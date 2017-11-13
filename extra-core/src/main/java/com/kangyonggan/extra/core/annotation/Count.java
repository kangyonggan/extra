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
     * method called count druing the interval time
     *
     * @return
     */
    int count();

    /**
     * when over count, is interrupt
     *
     * @return
     */
    boolean interrupt() default false;

    /**
     * count handle, default is memory count handle
     *
     * @return
     */
    Class<? extends CountHandle> handle() default MemoryCountHandle.class;

}
