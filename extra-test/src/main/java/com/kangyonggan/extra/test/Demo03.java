package com.kangyonggan.extra.test;

import com.kangyonggan.extra.core.annotation.Log;

/**
 * @author kangyonggan
 * @since 2017/11/4 0004
 */
public class Demo03 {

    @Log
    public static String hello(String name) {
        if (name == null) {
            return name;
        }
        return name;
    }

    public static void main(String[] args) {
        hello("world");
    }

}
