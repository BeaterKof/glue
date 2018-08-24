package com.di.glue.context.data;

import java.util.List;

public interface BindingConfigurer {

    void addBinding(Class<?> clazz, Class<?> clazz2, java.lang.String name, String beanType, Scope scope);

    void addBinding(Class<?> clazz, Class<?> clazz2, String name, Scope scope);

    List<BindingUnit> getBindings();
}

