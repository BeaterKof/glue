package com.di.glue.context.util;

public class Validator {

    /**
     * TODO
     * @param name
     * @return
     */
    public static boolean isValidClassName(String name) {
        if(name == null) return false;
        return true;
    }

    public static boolean notNull(Object... objects){
        for(Object obj : objects) {
            if (obj == null) return false;
        }
        return true;
    }
}
