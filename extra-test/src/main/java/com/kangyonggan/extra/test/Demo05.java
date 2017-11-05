package com.kangyonggan.extra.test;

import com.kangyonggan.extra.core.annotation.Log;

/**
 * @author kangyonggan
 * @since 2017/11/5 0005
 */
public class Demo05 {

    @Log
    public void helloxxx() {
        new Thread(){
            @Override
            @Log
            public void run() {
                System.out.println("hello");
            }
        }.start();
    }

}
