package com.kangyonggan.extra.core.model;

import java.io.Serializable;

/**
 * @author kangyonggan
 * @date 2018/4/27 0027
 */
public class EnumInfo implements Serializable {

    private String key;
    private String code;
    private String name;
    private String clazz;

    public EnumInfo(String key, String code, String name, String clazz) {
        this.key = key;
        this.code = code;
        this.name = name;
        this.clazz = clazz;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    @Override
    public String toString() {
        return "EnumInfo{" +
                "key='" + key + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", clazz=" + clazz +
                '}';
    }
}
