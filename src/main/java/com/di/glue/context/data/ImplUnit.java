package com.di.glue.context.data;

import com.sun.istack.internal.Pool;

import java.util.Objects;

public class ImplUnit {

    // implemente
    private Class<?> clazz;

    // scope of bean
    private Scope scope;

    /**
     * used for named proxies (by qualifier annotation)
     */
    private String qualifier;

    private ImplUnit(Class<?> clazz, String qualifier, Scope scope) {
        this.clazz = clazz;
        this.qualifier = qualifier;
        this.scope = scope;
    }

    public static final ImplUnit of(Class<?> clazz, String qualifier, Scope scope) {
        return new ImplUnit(clazz, qualifier, scope);
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

    public Scope getScope() { return scope; }

    public void setScope(Scope scope) { this.scope = scope; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImplUnit implUnit = (ImplUnit) o;
        return Objects.equals(clazz, implUnit.clazz) &&
                scope == implUnit.scope &&
                Objects.equals(qualifier, implUnit.qualifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, scope, qualifier);
    }

    @Override
    public String toString() {
        return "ImplUnit{" +
                "clazz=" + clazz +
                ", scope=" + scope +
                ", qualifier='" + qualifier + '\'' +
                '}';
    }
}
