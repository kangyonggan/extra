package com.kangyonggan.extra.core.util;

import java.util.Date;

/**
 * @author kangyonggan
 * @since 2017/11/4 0004
 */
public class StringUtil {

    public static final String EXPTY = "";

    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static boolean isNotEmpty(String str) {
        return str != null && str.trim().length() > 0;
    }

    public static String firstToLowerCase(String str) {
        if (str == null || str.trim().length() == 0) {
            return str;
        }
        if (str.length() == 1) {
            return str.toLowerCase();
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }
}
