package com.di.glue.context.data;

import java.util.ArrayList;
import java.util.List;

public class DefaultBindingConfigurer implements BindingConfigurer{

    private List<BindingUnit> bindings;

    public DefaultBindingConfigurer() {
        bindings = new ArrayList<>();
    }

    //TODO: method chaining
    public void addBinding(Class<?> clazz, Class<?> clazz2, java.lang.String name, String beanType) {
        bindings.add(BindingUnit.of(clazz, clazz2, name, beanType));
    }

    @Override
    public void addBinding(Class<?> clazz, Class<?> clazz2, String name) {
        bindings.add(BindingUnit.of(clazz, clazz2, name, null));
    }

    public List<BindingUnit> getBindings() {
        return new ArrayList<>(bindings);
    }
}
