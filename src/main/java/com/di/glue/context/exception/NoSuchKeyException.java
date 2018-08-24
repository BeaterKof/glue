package com.di.glue.context.exception;

public class NoSuchKeyException extends Exception {

    private static final String MSG = "No key found with value: ";

    public NoSuchKeyException(String message) {
        super(message);
    }

    public NoSuchKeyException(Object key) {
        super(MSG + key.toString());
    }
}
