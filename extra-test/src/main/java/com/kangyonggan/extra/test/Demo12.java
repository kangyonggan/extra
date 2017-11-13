package com.kangyonggan.extra.test;


import com.kangyonggan.extra.core.annotation.Monitor;

/**
 * @author kangyonggan
 * @since 11/13/17
 */
public class Demo12 {

//    @Log
//    @Cache(key = "xxx")
    @Monitor
    public static String hello(String name) {
        if (name == null) {
            System.out.println("xx");
            System.out.println("yy");
            return "xxx";
        }

        if (name != null) return "0";
        return name;

    }

    public static void main(String[] args) {
        hello("111");
    }

}
