package com.kangyonggan.extra.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * enum's handle
 *
 * @author kangyonggan
 * @since 10/31/17
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Handle {

    /**
     * type of handle
     *
     * @return
     */
    Type type();

    enum Type {
        ENUM
    }

}
