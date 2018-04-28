package com.kangyonggan.extra.core.model;

import com.kangyonggan.extra.core.util.StringUtil;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class EnumInfo implements Serializable {

    private String key;
    private String code;
    private String name;
    private Class<?> clazz;

    public EnumInfo(String key, String code, String name, Class<?> clazz) {
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

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public LinkedHashMap<Object, Object> map() throws Exception {
        LinkedHashMap<Object, Object> map = new LinkedHashMap<>();
        Object[] objs = clazz.getEnumConstants();
        for (Object obj : objs) {
            Object codeValue = clazz.getDeclaredMethod("get" + StringUtil.firstToUpperCase(code)).invoke(obj);
            Object nameValue = clazz.getDeclaredMethod("get" + StringUtil.firstToUpperCase(name)).invoke(obj);
            map.put(codeValue, nameValue);
        }

        return map;
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
