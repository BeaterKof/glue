package com.di.glue.context.data;

import com.di.glue.context.BindingMapper;

import java.util.List;

public interface BindingConfigurer {

    void addBinding(Class<?> clazz, Class<?> clazz2, String name, BeanType beanType);

    List<BindingUnit> getBindings();
}
