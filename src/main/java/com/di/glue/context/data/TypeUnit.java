package com.di.glue.context.data;

import javax.annotation.Nullable;
import java.util.Objects;

public class TypeUnit {

    // interface to be binded
    private Class clazz;
    // field used for multiple binding to the same interface
    // todo multiple binding
    @Nullable
    private String bindName;

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public String getBindName() {
        return bindName;
    }

    public void setBindName(String bindName) {
        this.bindName = bindName;
    }

    public static final TypeUnit of(Class<?> clazz, String beanType) {
        TypeUnit typeUnit = new TypeUnit();
        typeUnit.setBindName(beanType);
        typeUnit.setClazz(clazz);
        return typeUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeUnit typeUnit = (TypeUnit) o;
        return Objects.equals(clazz, typeUnit.clazz) &&
                Objects.equals(bindName, typeUnit.bindName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, bindName);
    }

    @Override
    public String toString() {
        return "TypeUnit{" +
                "clazz=" + clazz.getSimpleName() +
                ", bindName='" + bindName + '\'' +
                '}';
    }
}
