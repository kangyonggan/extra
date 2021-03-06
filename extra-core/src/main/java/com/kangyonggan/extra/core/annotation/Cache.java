package com.kangyonggan.extra.core.annotation;

import com.kangyonggan.extra.core.handle.CacheHandle;
import com.kangyonggan.extra.core.handle.impl.MemoryCacheHandle;
import com.kangyonggan.extra.core.util.StringUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * cache get or save
 *
 * @author kangyonggan
 * @since 10/31/17
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Cache {

    /**
     * cache key
     *
     * @return none
     */
    String key();

    /**
     * cache prefix
     *
     * @return none
     */
    String prefix() default StringUtil.EXPTY;

    /**
     * expire time，unit is milliseconds， default is 10 years
     *
     * @return none
     */
    long expire() default 315360000000L;

    /**
     * cache handle, default is memory cache
     *
     * @return none
     */
    Class<? extends CacheHandle> handle() default MemoryCacheHandle.class;

}
