package com.kangyonggan.extra.test;

import com.kangyonggan.extra.core.annotation.Handle;
import com.kangyonggan.extra.core.handle.EnumHandle;
import com.kangyonggan.extra.core.model.EnumInfo;

@Handle(type = Handle.Type.ENUM)
public class MyEnumHandle extends EnumHandle {

    private static MyEnumHandle instance = new MyEnumHandle();

    private MyEnumHandle() {
    }

    public static EnumInfo getEnumInfo(String key) {
        return instance.get(key);
    }

    private static void collectionEnumInfo(String key, String code, String name, String clazz) {
        try {
            instance.put(key, new EnumInfo(key, code, name, Class.forName(clazz)));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
