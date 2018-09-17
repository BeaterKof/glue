package com.di.glue.context.util;

import com.di.glue.context.data.BindIdentifier;
import com.di.glue.context.data.ImplUnit;
import com.di.glue.context.data.MultiMapEntry;
import com.di.glue.context.data.TypeUnit;

public class LogUtils {

    public static String getSimpleBindingString(MultiMapEntry<Class<?>, BindIdentifier, ImplUnit> entry) {
        return "(" + entry.getKey().getSimpleName() + " -> " + entry.getValue().getImplClazz().getSimpleName() +
                ") " + entry.getSubKey().getScope();
    }

    public static String getComplexBindingString(MultiMapEntry<Class<?>, BindIdentifier, ImplUnit> entry) {
        return "(" + entry.getKey().getSimpleName() + " -> " + entry.getValue().getImplClazz().getSimpleName() + ", " +
                entry.getSubKey().getQualifier() + ") " + entry.getSubKey().getScope();
    }
}
