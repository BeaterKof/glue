package com.di.glue.context.exception;

/**
 * created by: andrei
 * date: 10.10.2018
 **/
public class CircularBindingException extends RuntimeException {

    private static final String MSG = "Circular binding found for class: %s";

    public CircularBindingException(Class<?> clazz) {
        super(String.format(MSG, clazz.getName()));
    }
}
