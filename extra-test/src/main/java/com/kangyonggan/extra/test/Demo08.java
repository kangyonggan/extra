package com.kangyonggan.extra.test;

import com.kangyonggan.extra.core.annotation.LimitCount;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author kangyonggan
 * @since 11/6/17
 */
public class Demo08 {

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * 10秒内只能调用5次
     *
     * @param count
     */
    @LimitCount(interval = 1000 * 10, count = 5)
    public static char hello(int count) {
        System.out.println(String.format("时间：%s, 第%d次调用", format.format(new Date()), count));
        return 0;
    }

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 6; i++) {
            hello(i);
            Thread.sleep(1000);
        }
    }

}
