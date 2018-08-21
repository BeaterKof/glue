package com.di.glue.context.util;

import com.di.glue.context.data.ImplUnit;
import com.di.glue.context.data.MultiMapEntry;
import com.di.glue.context.data.TypeUnit;

public class LogUtils {

    private static final String SINGLETON = "SINGLETON";
    private static final String PROTOTYPE = "PROTOTYPE";

    public static String getSimpleBindingString(MultiMapEntry<TypeUnit, ImplUnit, Object> entry) {
        String bindingType = getType(entry.getSubKey().getQualifier());
        return "(" + entry.getKey().getClazz().getSimpleName() + " -> " + entry.getSubKey().getClazz().getSimpleName() + ") " + bindingType;
    }

    public static String getComplexBindingString(MultiMapEntry<TypeUnit, ImplUnit, Object> entry) {
        String bindingType = getType(entry.getSubKey().getQualifier());
        return entry.getKey().getClazz().getSimpleName() + " -> (" + entry.getSubKey().getClazz().getSimpleName() + ", " + entry.getValue() + ", " + bindingType + ")";
    }

    private static String getType(String qualifier) {
        return qualifier == null ? SINGLETON : PROTOTYPE;
    }
}
