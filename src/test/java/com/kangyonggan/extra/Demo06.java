package com.kangyonggan.extra;

import com.kangyonggan.extra.annotation.Log;

/**
 * @author kangyonggan
 * @since 2017/11/5 0005
 */
public class Demo06 {

    @Log
    public void helloNull(String name) {
        if (name == null) {
            return;
        }

        if (name.length() == 0) {
            return;
        }

        return;
    }

}
