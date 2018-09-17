package com.di.glue.context.data;

import java.util.Objects;

/**
 * created by: andrei
 * date: 02.09.2018
 **/
public class ImplUnit {
    private Class<?> implClazz;
    private Object instance;

    private ImplUnit(Class<?> implClazz, Object instance) {
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

    public static ImplUnit of(Class<?> implClazz, Object instance) {
        return new ImplUnit(implClazz, instance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImplUnit implUnit = (ImplUnit) o;
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
