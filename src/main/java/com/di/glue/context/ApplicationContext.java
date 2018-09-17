package com.di.glue.context;

import com.di.glue.context.data.BindingConfigurer;

public interface ApplicationContext {

    void addConfigurer(BindingConfigurer configurer);

    void logBindings();
}
