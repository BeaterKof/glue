package com.di.glue.context.data;

import com.sun.istack.internal.Pool;

import java.util.Objects;

public class ImplUnit {

    // implemente
    private Class<?> clazz;

    /**
     * used for named proxies (by qualifier annotation)
     */
    private String qualifier;

    private ImplUnit(Class<?> clazz, String qualifier) {
        this.clazz = clazz;
        this.qualifier = qualifier;
    }

    public static final ImplUnit of(Class<?> clazz, String qualifier) {
        return new ImplUnit(clazz, qualifier);
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImplUnit implUnit = (ImplUnit) o;
        return Objects.equals(clazz, implUnit.clazz) &&
                Objects.equals(qualifier, implUnit.qualifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, qualifier);
    }

    @Override
    public String toString() {
        return "ImplUnit{" +
                "clazz=" + clazz.getSimpleName() +
                ", qualifier='" + qualifier + '\'' +
                '}';
    }
}
