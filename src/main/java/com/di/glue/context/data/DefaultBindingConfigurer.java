package com.di.glue.context.data;

import com.di.glue.context.BindingMapper;

import java.util.ArrayList;
import java.util.List;

public class DefaultBindingConfigurer implements BindingConfigurer{

    private List<BindingUnit> bindings;

    public DefaultBindingConfigurer() {
        bindings = new ArrayList<>();
    }

    //TODO: method chaining
    public void addBinding(Class<?> clazz, Class<?> clazz2, String name, BeanType beanType) {
        bindings.add(BindingUnit.of(clazz, clazz2, name, beanType));
    }

    public List<BindingUnit> getBindings() {
        return new ArrayList<>(bindings);
    }
}
