package com.di.glue.context.factory;

public class BeanFactory {

    public static Object createInstance(Class<?> clazz) throws IllegalAccessException, InstantiationException {
        return clazz.newInstance();
    }
}
