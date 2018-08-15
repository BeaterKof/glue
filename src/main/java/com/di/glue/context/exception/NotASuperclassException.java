package com.di.glue.context.exception;

public class NotASuperclassException extends RuntimeException {

    private final static String MSG = "%s does not implement: %s";

    public NotASuperclassException(Class<?> interf, Class<?> subclass) {
        super(String.format(MSG, subclass.toString(), interf.toString()));
    }
}
