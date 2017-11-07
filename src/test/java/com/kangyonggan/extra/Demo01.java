package com.kangyonggan.extra;

import com.kangyonggan.extra.annotation.Cache;

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
