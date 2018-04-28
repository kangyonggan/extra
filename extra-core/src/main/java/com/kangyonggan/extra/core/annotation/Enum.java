package com.kangyonggan.extra.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * collect enum info
 *
 * @author kangyonggan
 * @since 10/31/17
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Enum {

    /**
     * enum's key, can't repetition
     *
     * @return none
     */
    String key() default "";

    /**
     * enum's code
     *
     * @return none
     */
    String code() default "code";

    /**
     * enum's name
     *
     * @return none
     */
    String name() default "name";

    /**
     * enum's description
     *
     * @return none
     */
    String description() default "";

}
