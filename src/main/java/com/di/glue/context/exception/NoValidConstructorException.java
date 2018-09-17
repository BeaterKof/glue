package com.di.glue.context.exception;

/**
 * created by: andrei
 * date: 15.09.2018
 **/
public class NoValidConstructorException extends RuntimeException {

    private static final String MSG = "Constructor not found for given fields in class: %s ";

    public NoValidConstructorException(Class<?> clazz) {
        super(String.format(MSG, clazz.getSimpleName()));
    }
}
