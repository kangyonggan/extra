package com.kangyonggan.extra.core.exception;

/**
 * @author kangyonggan
 * @since 11/7/17
 */
public class MethodCalledFrequencyException extends RuntimeException {

    private static String defaultMessage = "Method called frequency during interval times.";

    public MethodCalledFrequencyException() {
        super(defaultMessage);
    }

    public MethodCalledFrequencyException(String message) {
        super(message);
    }

    public MethodCalledFrequencyException(String message, Exception e) {
        super(message, e);
    }

    public MethodCalledFrequencyException(Exception e) {
        super(defaultMessage, e);
    }

}
