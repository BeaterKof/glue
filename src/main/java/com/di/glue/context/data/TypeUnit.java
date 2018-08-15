package com.di.glue.context.data;

import com.di.glue.context.data.BeanType;

import java.util.Objects;

public class TypeUnit {

    private Class clazz;
    private BeanType beanType;

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public BeanType getBeanType() {
        return beanType;
    }

    public void setBeanType(BeanType beanType) {
        this.beanType = beanType;
    }

    public static final TypeUnit of(Class<?> clazz, BeanType beanType) {
        TypeUnit typeUnit = new TypeUnit();
        typeUnit.setBeanType(beanType);
        typeUnit.setClazz(clazz);
        return typeUnit;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        TypeUnit typeUnit = (TypeUnit) obj;
        return (this.clazz != null && this.beanType != null &&
                this.clazz.equals(typeUnit.clazz) && this.beanType.equals(typeUnit.beanType));

    }

    @Override
    public int hashCode() {
        return Objects.hash(this.clazz, this.beanType);
    }
}
