package com.kangyonggan.extra.test;

import com.kangyonggan.extra.core.handle.impl.EnumHandle;
import com.kangyonggan.extra.core.util.StringUtil;

/**
 * @author kangyonggan
 * @date 2018/4/27 0027
 */
public class Demo12 {

    public static void main(String[] args) {
        String key = StringUtil.firstToLowerCase(PreferenceType.class.getSimpleName());
        System.out.println(key);
        System.out.println(EnumHandle.getEnum(key));
        System.out.println(PreferenceType.ACE.getName());
        System.out.println(EnumHandle.getEnum(key));
    }

}
