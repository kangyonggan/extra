package com.kangyonggan.extra.test;


import com.kangyonggan.extra.core.annotation.Monitor;

/**
 * @author kangyonggan
 * @since 11/7/17
 */
public class Demo11 {

    @Monitor(app = "app:${user.id}", type = "type:${user.id}", description = "desc:${user.id}")
    public static User login(User user) {
        System.out.println(user);
        return user;
    }

    public static void main(String[] args) {
        User user = new User();
        user.setId(1001L);
        login(user);
    }
}
