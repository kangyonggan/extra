package com.kangyonggan.extra.exception;

import java.lang.reflect.Field;

/**
 * @author kangyonggan
 * @since 11/7/17
 */
public class GetterNotFoundException extends RuntimeException {

    private static String defaultMessage = "Field's getter method not found.";

    public GetterNotFoundException() {
        super(defaultMessage);
    }

    public GetterNotFoundException(Class clazz, Field field) {
        super(String.format("%s's getter method not found on class %s.", field.getName(), clazz.getName()));
    }

    public GetterNotFoundException(String message) {
        super(message);
    }

    public GetterNotFoundException(String message, Exception e) {
        super(message, e);
    }

    public GetterNotFoundException(Exception e) {
        super(defaultMessage, e);
    }

}
