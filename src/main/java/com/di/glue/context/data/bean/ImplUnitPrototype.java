package com.di.glue.context.data.bean;

/**
 * created by: andrei
 * date: 27.12.2018
 **/
public class ImplUnitPrototype implements ImplUnit {

    private Class<?> implClazz;

    public Class<?> getImplClazz() {
        return implClazz;
    }

    public void setImplClazz(Class<?> implClazz) {
        this.implClazz = implClazz;
    }
}
