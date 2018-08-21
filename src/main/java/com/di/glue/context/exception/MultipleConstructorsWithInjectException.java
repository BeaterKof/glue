package com.di.glue.context.exception;

import java.lang.reflect.Constructor;

public class MultipleConstructorsWithInjectException extends RuntimeException {

    private static final String MSG = "Multiple @Inject annotations found for constructor constructor: %s";

    public MultipleConstructorsWithInjectException(Constructor<?> constructor) {
        super(String.format(MSG, constructor.getName()));
    }
}
