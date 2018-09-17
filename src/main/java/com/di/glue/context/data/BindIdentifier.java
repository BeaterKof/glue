package com.di.glue.context.data;

import java.util.Objects;

/**
 * created by: andrei
 * date: 02.09.2018
 **/
public class BindIdentifier {

    // scope of bean
    private Scope scope;

    // name of binding
    private String qualifier;

    private BindIdentifier(String qualifier, Scope scope) {
        this.qualifier = qualifier;
        this.scope = scope;
    }

    public static final BindIdentifier of(Scope scope, String qualifier) {
        return new BindIdentifier(qualifier, scope);
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
        BindIdentifier implUnit = (BindIdentifier) o;
        return scope == implUnit.scope &&
                Objects.equals(qualifier, implUnit.qualifier);
    }

    @Override
    public int hashCode() {

        return Objects.hash(scope, qualifier);
    }

    @Override
    public String toString() {
        return "ImplUnit{" +
                "scope=" + scope +
                ", qualifier='" + qualifier + '\'' +
                '}';
    }
}