package com.di.glue.context.util;

import com.di.glue.context.data.ImplUnit;
import com.di.glue.context.data.MultiMapEntry;
import com.di.glue.context.data.TypeUnit;

public class LogUtils {

    public static String getSimpleBindingString(MultiMapEntry<TypeUnit, ImplUnit, Object> entry) {
        return "(" + entry.getKey().getClazz().getSimpleName() + " -> " + entry.getSubKey().getClazz().getSimpleName() +
                ") " + entry.getSubKey().getScope();
    }

    public static String getComplexBindingString(MultiMapEntry<TypeUnit, ImplUnit, Object> entry) {
        return entry.getKey().getClazz().getSimpleName() + " -> (" + entry.getSubKey().getClazz().getSimpleName() +
                ", " + entry.getValue() + ", " + entry.getSubKey().getScope() + ")";
    }
}
