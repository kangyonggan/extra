package com.kangyonggan.extra.annotation;

import com.kangyonggan.extra.handle.ValidHandle;
import com.kangyonggan.extra.handle.impl.ConsoleValidHandle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author kangyonggan
 * @since 11/7/17
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Valid {

    boolean required() default false;

    int minLength() default -1;

    int maxLength() default Integer.MAX_VALUE;

    int length() default -1;

    double min() default -Double.MIN_VALUE;

    double max() default Double.MAX_VALUE;

    boolean number() default false;

    String regex() default "";

    /**
     * when valid failure, is interrupt
     *
     * @return
     */
    boolean interrupt() default false;

    /**
     * valid handle, default is console valid handle
     *
     * @return
     */
    Class<? extends ValidHandle> handle() default ConsoleValidHandle.class;

}
