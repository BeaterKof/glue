package com.di.glue.context.data;

public class BindingUnit {

    private Class<?> abstraction;
    private Class<?> implementation;
    private java.lang.String name;
    private String beanType;

    private BindingUnit(Class<?> abstraction, Class<?> implementation, java.lang.String name, String beanType) {
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

    public java.lang.String getName() {
        return name;
    }

    public String getBeanType() {
        return beanType;
    }

    public static BindingUnit of(Class<?> abstraction, Class<?> implementation, java.lang.String name, String beanType) {
        return new BindingUnit(abstraction, implementation, name, beanType);
    }
}
