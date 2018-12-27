package com.di.glue.context.data.bean;

import java.util.Objects;

/**
 * created by: andrei
 * date: 27.12.2018
 **/
public class ImplUnitSingleton implements ImplUnit {

    private Class<?> implClazz;
    private Object instance;

    private ImplUnitSingleton(Class<?> implClazz, Object instance) {
        this.implClazz = implClazz;
        this.instance = instance;
    }

    public Class<?> getImplClazz() {
        return implClazz;
    }

    public void setImplClazz(Class<?> implClazz) {
        this.implClazz = implClazz;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public static ImplUnitSingleton of(Class<?> implClazz, Object instance) {
        return new ImplUnitSingleton(implClazz, instance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImplUnitSingleton implUnit = (ImplUnitSingleton) o;
        return Objects.equals(implClazz, implUnit.implClazz) &&
                Objects.equals(instance, implUnit.instance);
    }

    @Override
    public int hashCode() {

        return Objects.hash(implClazz, instance);
    }

    @Override
    public String toString() {
        return "ImplUnit{" +
                "implClazz=" + implClazz +
                ", instance=" + instance +
                '}';
    }
}
