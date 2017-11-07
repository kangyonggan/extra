package com.kangyonggan.extra.util;

import com.kangyonggan.extra.annotation.Valid;
import com.kangyonggan.extra.exception.GetterNotFoundException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author kangyonggan
 * @since 11/7/17
 */
public class ValidUtil {

    public static void valid(boolean interrupt, String handlePackage, Object... args) {
        try {
            for (Object arg : args) {
                List<Field> fields = getAllField(arg.getClass());
                for (Field field : fields) {
                    validField(field, arg);
                }
            }
        } catch (RuntimeException e) {
            try {
                Class clazz = Class.forName(handlePackage);
                clazz.getDeclaredMethod("failure", RuntimeException.class).invoke(clazz.newInstance(), e);
            } catch (Exception e1) {
            }
            if (interrupt) {
                throw e;
            }
        }
    }

    private static void validField(Field field, Object obj) {
        Valid valid = field.getAnnotation(Valid.class);
        if (valid == null) {
            return;
        }

        Object value = validRequired(field, obj, valid);
        if (!valid.required() && value == null) {
            return;
        }

        validMinLength(field, valid, value);
        validMaxLength(field, valid, value);
        validLength(field, valid, value);
        validNumber(field, valid, value);
        validMin(field, valid, value);
        validMax(field, valid, value);
        validRegex(field, valid, value);

    }

    private static void validRegex(Field field, Valid valid, Object value) {
        if (value instanceof String) {
            if (StringUtil.isNotEmpty(valid.regex())) {
                String val = (String) value;
                if (!val.matches(valid.regex())) {
                    throw new IllegalArgumentException(String.format("%s not match regular expression %s", field.getName(), valid.regex()));
                }
            }
        }
    }

    private static void validMax(Field field, Valid valid, Object value) {
        if (value instanceof Number) {
            Number number = (Number) value;
            if (Double.valueOf(valid.max()).compareTo(number.doubleValue()) < 0) {
                throw new IllegalArgumentException(String.format("%s is greater than %f", field.getName(), valid.max()));
            }
        }
    }

    private static void validMin(Field field, Valid valid, Object value) {
        if (value instanceof Number) {
            Number number = (Number) value;
            if (Double.valueOf(valid.min()).compareTo(number.doubleValue()) > 0) {
                throw new IllegalArgumentException(String.format("%s is less than %f", field.getName(), valid.min()));
            }
        }
    }

    private static void validNumber(Field field, Valid valid, Object value) {
        if (valid.number() && !(value instanceof Number)) {
            throw new IllegalArgumentException(String.format("%s is not a number", field.getName()));
        }
    }

    private static void validLength(Field field, Valid valid, Object value) {
        if (value instanceof String) {
            String val = (String) value;
            if (valid.length() != -1 && val.length() != valid.length()) {
                throw new IllegalArgumentException(String.format("%s length is not equal to %d", field.getName(), valid.length()));
            }
        }
    }

    private static void validMaxLength(Field field, Valid valid, Object value) {
        if (value instanceof String) {
            String val = (String) value;
            if (val.length() > valid.maxLength()) {
                throw new IllegalArgumentException(String.format("%s length is greater than %d", field.getName(), valid.minLength()));
            }
        }
    }

    private static void validMinLength(Field field, Valid valid, Object value) {
        if (value instanceof String) {
            String val = (String) value;
            if (val.length() < valid.minLength()) {
                throw new IllegalArgumentException(String.format("%s length is less than %d", field.getName(), valid.minLength()));
            }
        }
    }

    private static Object validRequired(Field field, Object obj, Valid valid) {
        Object value;

        Method method = getGetterMethod(obj.getClass(), field);
        try {
            value = method.invoke(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (valid.required() && value == null) {
            throw new IllegalArgumentException(String.format("%s can be null.", field.getName()));
        }

        return value;
    }

    private static Method getGetterMethod(Class clazz, Field field) {
        try {
            return getMethod(clazz, "get" + StringUtil.firstToUpperCase(field.getName()));
        } catch (Exception e) {
            throw new GetterNotFoundException(clazz, field);
        }
    }

    private static List<Field> getAllField(Class clazz) {
        List<Field> fields = new ArrayList();
        while (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }

        return fields;
    }

    private static Method getMethod(Class clazz, String methodName) throws Exception {
        Method method = null;
        while (method == null) {
            try {
                method = clazz.getDeclaredMethod(methodName);
            } catch (Exception e) {
            }
            clazz = clazz.getSuperclass();
        }

        return method;
    }

}
