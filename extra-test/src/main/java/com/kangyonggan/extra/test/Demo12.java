package com.kangyonggan.extra.test;

import com.kangyonggan.extra.core.annotation.Serial;
import com.kangyonggan.extra.core.model.EnumInfo;
import com.kangyonggan.extra.core.util.StringUtil;

import java.util.LinkedHashMap;

@Serial
public class Demo12 {

    public static void main(String[] args) throws Exception {
        String key = StringUtil.firstToLowerCase(PreferenceType.class.getSimpleName());
        EnumInfo enumInfo = MyEnumHandle.getEnumInfo(key);

        LinkedHashMap<Object, Object> map = enumInfo.map();
        for (Object keys : map.keySet()) {
            System.out.println(keys);
            System.out.println(map.get(keys));
        }
    }

}
