package com.kangyonggan.extra.annotation;

import com.kangyonggan.extra.handle.CacheHandle;
import com.kangyonggan.extra.handle.impl.MemoryCacheHandle;
import com.kangyonggan.extra.util.StringUtil;

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
     * @return
     */
    String key();

    /**
     * cache prefix
     *
     * @return
     */
    String prefix() default StringUtil.EXPTY;

    /**
     * expire time，unit is milliseconds， default is 10 years
     *
     * @return
     */
    long expire() default 315360000000L;

    /**
     * cache handle, default is memory cache
     *
     * @return
     */
    Class<? extends CacheHandle> handle() default MemoryCacheHandle.class;

}
