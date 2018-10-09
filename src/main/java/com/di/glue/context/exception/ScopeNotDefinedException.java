package com.di.glue.context.exception;

/**
 * created by: andrei
 * date: 03.10.2018
 **/
public class ScopeNotDefinedException extends RuntimeException {

    private static final String MSG = "No scope was defined for class: %s";

    public ScopeNotDefinedException(Class<?> clazz) {
        super(String.format(MSG, clazz.getName()));
    }
}
