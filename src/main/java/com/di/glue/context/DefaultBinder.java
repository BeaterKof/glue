package com.di.glue.context;

import com.di.glue.context.annotation.Inject;
import com.di.glue.context.data.*;
import com.di.glue.context.exception.DuplicateEntryException;
import com.di.glue.context.exception.NotASuperclassException;
import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

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
        if(implUnit == null || implUnit.getClazz() == null)
            throw new IllegalArgumentException("ImplUnit to be binded is/contains null.");

        try {
            beanMap.put(typeUnit, implUnit, null);
        } catch (DuplicateEntryException e) {
            e.printStackTrace();
        }
    }

    // goes through the map and creates all the implemented objects
    @Override
    public void createDependecies() {
        Set<MultiMapEntry<TypeUnit, ImplUnit, Object>> entrySet = beanMap.entrySet();
        for(MultiMapEntry<TypeUnit, ImplUnit, Object> entry : entrySet) {
            makeDependency(entry);
        }
    }

    //todo: multiple bindings
    //todo: entry pair to string
    private void makeDependency(MultiMapEntry<TypeUnit, ImplUnit, Object> entry) {
        // a new obj will be created if entry is not a nameless Prototype (ImplUnit.qualifier.isEmpty() == true)
        // and the obj does not already exist
        try {
            // if Singleton / named Prototype -> create new instance
            if(entry.getSubKey().getQualifier() == null || !entry.getSubKey().getQualifier().isEmpty()) {
                InjectionType injectionType = getInjectionType(entry.getSubKey().getClazz());
                if(injectionType == InjectionType.CONSTRUCTOR){
                    createObjectByConstructor(entry);
                } else if(injectionType == InjectionType.FIELD) {
                    createObjectByFields(entry);
                } else {
                    createSimpleObject(entry);
                }
            }
            else if(entry.getValue() != null) {
                log.info("Object for: (" + entry.getKey().getClazz().getSimpleName() + " -> " +
                        entry.getSubKey().getClazz().getSimpleName() + ") already exists.");
            } else {
                log.info("Entry (" + entry.getKey().getClazz().getSimpleName() + " -> " +
                        entry.getSubKey().getClazz().getSimpleName() + ") is fit for a prototype factory.");
            }
        } catch (IllegalAccessException|InstantiationException e) {
            e.printStackTrace();
        } catch (DuplicateEntryException e) {
            log.error(e.getMessage());
        }
    }

    public InjectionType getInjectionType(Class<?> clazz) {
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

    private void createObjectByConstructor(MultiMapEntry<TypeUnit, ImplUnit, Object> entry) {
        log.info("Creating new object by constructor injection: " + entry.getSubKey().getClazz());
        Class<?> clazz = entry.getSubKey().getClazz();
        Constructor<?>[] constructors = clazz.getConstructors();
        Constructor<?> constr = null;
        for(int i=0; i<constructors.length; i++) {
            if(constructors[i].isAnnotationPresent(Inject.class))
                constr = constructors[i];
        }
        for(Class<?> param : constr.getParameterTypes()) {
            //todo
        }

    }

    private void createObjectByFields(MultiMapEntry<TypeUnit, ImplUnit, Object> entry) {
        log.info("Creating new object by field injection: " + entry.getSubKey().getClazz());
        Class<?> clazz = entry.getSubKey().getClazz();
        //todo
    }

    private void createSimpleObject(MultiMapEntry<TypeUnit, ImplUnit, Object> entry) throws IllegalAccessException, InstantiationException, DuplicateEntryException {
        log.info("Creating new object (no injection): " + entry.getSubKey().getClazz());
        Class<?> clazz = entry.getSubKey().getClazz();
        beanMap.put(entry.getKey(), entry.getSubKey(), clazz.newInstance());
    }

    @Override
    public List<MultiMapEntry<TypeUnit, ImplUnit, Object>> getBindings() {
        return beanMap.getEntries();
    }

}
