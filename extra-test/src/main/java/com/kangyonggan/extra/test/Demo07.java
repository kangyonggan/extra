package com.kangyonggan.extra.test;


import com.kangyonggan.extra.core.annotation.CacheDel;

/**
 * @author kangyonggan
 * @since 2017/11/4 0004
 */
public class Demo07 {

    @CacheDel(key = {"hello:${name}", "hello:user:${name}"})
    public static String hello(String name) {
        if (name == null) {
            return name;
        }
        return name;
    }

}
