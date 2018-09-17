package com.di.glue.context.exception;

import java.lang.reflect.Constructor;

public class MultipleConstructorsWithInjectException extends RuntimeException {

    private static final String MSG = "Multiple constructors with @Inject annotations on found for class: %s";

    public MultipleConstructorsWithInjectException(Class<?> clazz) {
        super(String.format(MSG, clazz.getSimpleName()));
    }
}
