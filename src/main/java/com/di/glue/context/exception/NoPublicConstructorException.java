package com.di.glue.context.exception;

public class NoPublicConstructorException extends RuntimeException {

    private static final String MSG = "No public constructor available for class: %s";

    public NoPublicConstructorException(Class<?> clazz) {
        super(String.format(MSG, clazz.getName()));
    }

}
