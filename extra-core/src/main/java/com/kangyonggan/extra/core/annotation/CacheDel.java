package com.kangyonggan.extra.core.annotation;

import com.kangyonggan.extra.core.handle.CacheHandle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * cache delete
 *
 * @author kangyonggan
 * @since 10/31/17
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface CacheDel {

    /**
     * cache key
     *
     * @return
     */
    String value();

    /**
     * cache channel, default is memory cache
     *
     * @return
     */
    Class<? extends CacheHandle> handle() default CacheHandle.class;

}
