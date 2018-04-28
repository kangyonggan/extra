package com.kangyonggan.extra.test;


import com.kangyonggan.extra.core.annotation.Enum;

/**
 * @author kangyonggan
 * @date 4/23/18
 */
@Enum(code = "type")
public enum PreferenceType {

    /**
     * ace偏好
     */
    ACE("ace", "ace"),
    USER("user", "user");

    private String type;

    private String name;

    PreferenceType() {

    }

    PreferenceType(String type, String name) {
        this.type = type;
        this.name = name;
    }

    static {
        PreferenceType.ACE.getName();
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
