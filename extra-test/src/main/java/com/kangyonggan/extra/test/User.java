package com.kangyonggan.extra.test;


import com.kangyonggan.extra.core.annotation.Valid;

/**
 * @author kangyonggan
 * @since 11/7/17
 */
public class User extends Person {

    @Valid(required = true)
    private Long id;

    @Valid(required = true, minLength = 5, maxLength = 20)
    private String username;

    @Valid(max = 100)
    private int age;

    @Valid(required = true, minLength = 8, maxLength = 20, regex = "\\w+")
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", age=" + age +
                ", password='" + password + '\'' +
                '}';
    }
}
