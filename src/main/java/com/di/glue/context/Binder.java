package com.di.glue.context;

import com.di.glue.context.data.*;
import com.di.glue.context.data.bean.BindIdentifier;
import com.di.glue.context.data.bean.ImplUnit;

import java.util.List;

public interface Binder {

    void bind(Class<?> abstr, Class<?> impl, Scope scope, String qualifier);

    List<MultiMapEntry<Class<?>, BindIdentifier, ImplUnit>> getBindings();

    void init();

    Object getBean(Class<?> clazz);

    Object getBean(Class<?> clazz, Scope scope);

    Object getBean(Class<?> clazz, Scope scope, String qualifier);

}
