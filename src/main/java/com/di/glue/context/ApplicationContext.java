package com.di.glue.context;

import com.di.glue.context.data.BindingConfigurer;
import com.di.glue.context.data.Scope;

public interface ApplicationContext {

    void addConfigurer(BindingConfigurer configurer);

    void logBindings();

    Object getBean(Class<?> clazz);

    Object getBean(Class<?> clazz, Scope scope);

    Object getBean(Class<?> clazz, Scope scope, String qualifier);
}
