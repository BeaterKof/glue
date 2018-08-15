package com.di.glue.context;

import com.di.glue.context.data.BindingConfigurer;
import com.di.glue.context.data.TypeUnit;
import javafx.util.Pair;

import java.util.List;

public interface ApplicationContext {

    List<Pair<Class<?>, Class<?>>> getBindingsList();

    void addConfigurer(BindingConfigurer configurer);

    void printBindings();
}
