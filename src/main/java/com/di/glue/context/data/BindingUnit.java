package com.di.glue.context.data;

public class BindingUnit {

    private Class<?> abstraction;
    private Class<?> implementation;
    private String name;
    private BeanType beanType;

    private BindingUnit(Class<?> abstraction, Class<?> implementation, String name, BeanType beanType) {
        this.abstraction = abstraction;
        this.implementation = implementation;
        this.name = name;
        this.beanType = beanType;
    }

    public Class<?> getAbstraction() {
        return abstraction;
    }

    public Class<?> getImplementation() {
        return implementation;
    }

    public String getName() {
        return name;
    }

    public BeanType getBeanType() {
        return beanType;
    }

    public static BindingUnit of(Class<?> abstraction, Class<?> implementation, String name, BeanType beanType) {
        return new BindingUnit(abstraction, implementation, name, beanType);
    }
}
