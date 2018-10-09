package com.di.glue.context.data;

import java.util.ArrayList;
import java.util.List;

public class DefaultBindingConfigurer implements BindingConfigurer{

    private List<BindingUnit> bindings;

    public DefaultBindingConfigurer() {
        bindings = new ArrayList<>();
    }

    @Override
    public void addBinding(Class<?> clazz, Class<?> clazz2, java.lang.String name, String beanType, Scope scope) {
        bindings.add(BindingUnit.of(clazz, clazz2, name, beanType, scope));
    }

    @Override
    public void addBinding(Class<?> clazz, Class<?> clazz2, String name, Scope scope) {
        if(clazz==null || clazz2==null) throw new IllegalArgumentException();
        bindings.add(BindingUnit.of(clazz, clazz2, name, null, scope));
    }

    public List<BindingUnit> getBindings() {
        return new ArrayList<>(bindings);
    }

}
