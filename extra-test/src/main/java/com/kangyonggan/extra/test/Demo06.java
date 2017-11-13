package com.kangyonggan.extra.test;

/**
 * @author kangyonggan
 * @since 2017/11/5 0005
 */
public class Demo06 {

//    @Log
    public void helloNull(String name) {
        if (name == null) {
            for (int i = 0; i < 10; i++) {
                System.out.println(i);
            }
            return ;
        }

        if (name.length() == 0) {
            return;
        }

        return;
    }

}
