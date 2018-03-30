package com.kangyonggan.extra.test;


import com.kangyonggan.extra.core.annotation.Monitor;

/**
 * @author kangyonggan
 * @since 11/7/17
 */
public class Demo11 {

    @Monitor(type = "type:${user.id}", description = "desc:${user.id}")
    public static void login2(User user) {
        System.out.println(user);
    }

    public static void main(String[] args) {
        User user = new User();
        user.setId(1001L);
        login2(user);
    }
}
