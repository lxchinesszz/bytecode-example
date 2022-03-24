package com.example.test.javassist;

/**
 * @author liuxin
 * 2022/3/24 11:29 PM
 */
public class User {

    private String name;

    public User() {
    }

    public User(String name) {
        this.name = name;
    }

    public String sayJavassist() {
        return "Hello Javassist";
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }
}
