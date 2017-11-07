package com.kangyonggan.extra.exception;

/**
 * @author kangyonggan
 * @since 11/7/17
 */
public class MethodCalledOutOfCountException extends RuntimeException {

    private static String defaultMessage = "Method called out of count during interval times.";

    public MethodCalledOutOfCountException() {
        super(defaultMessage);
    }

    public MethodCalledOutOfCountException(String message) {
        super(message);
    }

    public MethodCalledOutOfCountException(String message, Exception e) {
        super(message, e);
    }

    public MethodCalledOutOfCountException(Exception e) {
        super(defaultMessage, e);
    }

}
