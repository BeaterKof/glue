package com.di.glue.context.exception;

public class NotABeanException extends RuntimeException {

    private static final String MSG = "Class: %s is not a bean.";

    public NotABeanException(Class<?> clazz) {
        super(String.format(MSG, clazz.getName()));
    }

    public NotABeanException(Class<?> clazz, String mesage) {
        super(String.format(MSG, clazz.getName()) + mesage);
    }
}
