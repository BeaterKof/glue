package com.di.glue.context;

import com.di.glue.context.data.*;
import com.di.glue.context.exception.NotASuperclassException;

import java.util.List;

public interface Binder {

    void bind(TypeUnit typeUnit, String name, Class<?> impl);

    List<MultiMapEntry<TypeUnit, String, Object>> getBindings();

}
