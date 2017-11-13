package com.kangyonggan.extra.test;


import com.kangyonggan.extra.core.annotation.Valid;

/**
 * @author kangyonggan
 * @since 11/7/17
 */
public class Demo10 {

    @Valid(interrupt = true)
    public static void login(User user) {
        System.out.println(user);
    }

    public static void main(String[] args) {
        User user = new User();
        user.setId(1L);
        user.setUsername("asdasdasdasd");
        user.setPassword("12345678");
        login(user);
    }

}
