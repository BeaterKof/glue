package com.di.glue.context.data;

public class BindingUnit {

    private Class<?> abstraction;
    private Class<?> implementation;
    private java.lang.String name;
    private String beanType;
    private Scope scope;

    private BindingUnit(Class<?> abstraction, Class<?> implementation, java.lang.String name, String beanType, Scope scope) {
        this.abstraction = abstraction;
        this.implementation = implementation;
        this.name = name;
        this.beanType = beanType;
        this.scope = scope;
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

    public Scope getScope() { return scope; }

    public static BindingUnit of(Class<?> abstraction, Class<?> implementation, java.lang.String name, String beanType, Scope scope) {
        if(scope == null) {
            scope = Scope.SINGLETON;
        }
        return new BindingUnit(abstraction, implementation, name, beanType, scope);
    }
}
