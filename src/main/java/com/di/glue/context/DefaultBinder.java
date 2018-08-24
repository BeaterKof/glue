package com.di.glue.context;

import com.di.glue.context.annotation.GlueBean;
import com.di.glue.context.annotation.Inject;
import com.di.glue.context.data.*;
import com.di.glue.context.exception.DuplicateEntryException;
import com.di.glue.context.exception.NotASuperclassException;
import com.di.glue.context.util.LogUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Class used to contain all binding objects for SINGLETON and NAMED PROXY types.
 *
 */
public class DefaultBinder implements Binder {

    static final Logger log = Logger.getLogger(DefaultBinder.class);

    private MultiMap<TypeUnit, ImplUnit, Object> beanMap;

    public DefaultBinder() {
        beanMap = new HashMultiMap<>();
    }

    @Override
    public void bind(TypeUnit typeUnit, ImplUnit implUnit) throws NotASuperclassException {
        if(typeUnit == null || typeUnit.getClazz() == null)
            throw new IllegalArgumentException("TypeUnit to be binded is/contains null.");
        if(implUnit == null || implUnit.getClazz() == null || implUnit.getScope() == null)
            throw new IllegalArgumentException("ImplUnit to be binded is/contains null.");

        try {
            beanMap.put(typeUnit, implUnit, null);
        } catch (DuplicateEntryException e) {
            log.error(e.getMessage());
        }
    }

    // must be called after all the binds have been added
    @Override
    public void init() {
        for(MultiMapEntry<TypeUnit, ImplUnit, Object> entry : beanMap.entrySet()) {
            if(entry.getSubKey().getScope() == Scope.SINGLETON)
                createSingletonInstance(entry);
        }
    }

    //todo: multiple bindings
    //todo: entry pair to string
    private void createSingletonInstance(MultiMapEntry<TypeUnit, ImplUnit, Object> entry) {
        try {
            if(entry.getSubKey().getScope() == Scope.SINGLETON) {
                if(entry.getValue() == null){
                    beanMap.getSubmap(entry.getKey()).put(entry.getSubKey(), createNewObject(entry));
                } else {
                    log.info("Object for:" + LogUtils.getSimpleBindingString(entry) + "already exists.");
                }
            }
        } catch (IllegalAccessException|InstantiationException e) {
            e.printStackTrace();
        }
    }

    // todo: overloaded versions
    public Object getBean(Class<?> clazz) {

        return null;
    }

    // gets the bindName=null, qualifier=null instance
    private Object getDefaultSingletonInstance(Class<?> clazz) {
        TypeUnit typeUnit = TypeUnit.of(clazz, null);
        // get submap for typeUnit, check if it has less than
        return null;
    }

    private Object createNewObject(MultiMapEntry<TypeUnit, ImplUnit, Object> entry) throws InstantiationException, IllegalAccessException {
        InjectionType injectionType = getInjectionType(entry.getSubKey().getClazz());
        if(injectionType == InjectionType.CONSTRUCTOR){
            return createNewObjectByConstructor(entry);
        } else if(injectionType == InjectionType.FIELD) {
            return createNewObjectByFields(entry);
        } else {
            return createNewObjectDefault(entry);
        }
    }

    private InjectionType getInjectionType(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getConstructors();
        for(int i=0; i<constructors.length; i++) {
            if(constructors[i].isAnnotationPresent(Inject.class))
                return InjectionType.CONSTRUCTOR;
        }
        Field[] fields = clazz.getFields();
        for(int i=0; i<fields.length; i++) {
            if(fields[i].isAnnotationPresent(Inject.class))
                return InjectionType.FIELD;
        }
        return InjectionType.NONE;
    }

    private Object createNewObjectByConstructor(MultiMapEntry<TypeUnit, ImplUnit, Object> entry) {
        log.info("Creating new object by constructor injection: " + entry.getSubKey().getClazz());
        Object object = null;
        Class<?> clazz = entry.getSubKey().getClazz();
        Constructor<?>[] constructors = clazz.getConstructors();
        Constructor<?> constr = null;
        for(int i=0; i<constructors.length; i++) {
            if(constructors[i].isAnnotationPresent(Inject.class))
                constr = constructors[i];
        }
        for(Class<?> param : constr.getParameterTypes()) {
            if(param.isAnnotationPresent(GlueBean.class)) {
                //search the map


//                beanMap.containsKey(TypeUnit.of())
                //create new obj
            }
        }
        return object;
    }

    private Object createNewObjectByFields(MultiMapEntry<TypeUnit, ImplUnit, Object> entry) {
        log.info("Creating new object by field injection: " + entry.getSubKey().getClazz());
        Object object = null;
        Class<?> clazz = entry.getSubKey().getClazz();
        //todo

        return object;
    }

    private Object createNewObjectDefault(MultiMapEntry<TypeUnit, ImplUnit, Object> entry) throws IllegalAccessException, InstantiationException {
        log.info("Creating new object (no injection): " + entry.getSubKey().getClazz());
        Class<?> clazz = entry.getSubKey().getClazz();
        return clazz.newInstance();
    }

    @Override
    public List<MultiMapEntry<TypeUnit, ImplUnit, Object>> getBindings() {
        return beanMap.getEntries();
    }

}
