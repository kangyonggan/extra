package com.kangyonggan.extra.test;

import com.kangyonggan.extra.core.annotation.Cache;

/**
 * @author kangyonggan
 * @since 2017/11/4 0004
 */
public class Demo01 {

    @Cache(key = "hello:${name}")
    public static String hello(String name) {
        if (name == null) {
            return name;
        }
        return name;
    }

}
