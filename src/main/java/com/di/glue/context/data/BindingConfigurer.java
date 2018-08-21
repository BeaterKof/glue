package com.di.glue.context.data;

import java.util.List;

public interface BindingConfigurer {

    void addBinding(Class<?> clazz, Class<?> clazz2, java.lang.String name, String beanType);

    void addBinding(Class<?> clazz, Class<?> clazz2, java.lang.String name);

    List<BindingUnit> getBindings();
}

