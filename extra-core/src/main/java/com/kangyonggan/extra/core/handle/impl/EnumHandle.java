package com.kangyonggan.extra.core.handle.impl;

import com.kangyonggan.extra.core.model.EnumInfo;

import java.util.HashMap;

/**
 * @author kangyonggan
 * @date 2018/4/27 0027
 */
public class EnumHandle extends HashMap<String, EnumInfo> {

    private static EnumHandle instance = new EnumHandle();

    static {
        instance.put("xxx", new EnumInfo("xxx", "11", "22", "33"));
        instance.put("yyy", new EnumInfo("xxx", "11", "22", "33"));
    }

    private EnumHandle() {
    }

    public static EnumInfo getEnumInfo(String key) {
        return instance.get(key);
    }

}
