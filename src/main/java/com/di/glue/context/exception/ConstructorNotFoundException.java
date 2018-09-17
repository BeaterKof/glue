package com.di.glue.context.exception;

/**
 * created by: andrei
 * date: 15.09.2018
 **/
public class ConstructorNotFoundException extends RuntimeException {

    private static final String MSG = "Constructor not found for class: %s";

    public ConstructorNotFoundException(Class<?> clazz) {
        super(String.format(MSG, clazz.getSimpleName()));
    }
}
