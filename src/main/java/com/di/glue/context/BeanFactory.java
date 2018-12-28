package com.di.glue.context;

import com.di.glue.context.data.MultiMapEntry;
import com.di.glue.context.data.Scope;
import com.di.glue.context.data.bean.BindIdentifier;
import com.di.glue.context.data.bean.ImplUnit;

import java.util.List;

/**
 * created by: andrei
 * date: 28.12.2018
 **/
public interface BeanFactory {
    void init();
    Object getBean(Class<?> clazz);
    Object getBean(Class<?> clazz, Scope scope);
    Object getBean(Class<?> clazz, Scope scope, String qualifier);
    void bind(Class<?> abstr, Class<?> impl, Scope scope, String qualifier);
    List<MultiMapEntry<Class<?>, BindIdentifier, ImplUnit>> getBindings();
}
