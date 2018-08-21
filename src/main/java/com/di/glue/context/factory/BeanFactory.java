package com.di.glue.context.factory;

import com.di.glue.context.annotation.GlueBean;
import com.di.glue.context.annotation.Inject;
import com.di.glue.context.annotation.Prototype;
import com.di.glue.context.annotation.Singleton;
import com.di.glue.context.data.MultiMap;
import com.di.glue.context.data.TypeUnit;
import com.di.glue.context.exception.MultipleConstructorsWithInjectException;
import com.di.glue.context.exception.NoPublicConstructorException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BeanFactory {

    public static Object createInstance(Class<?> impl, MultiMap<TypeUnit, String, Object> beanMap) throws IllegalAccessException, InstantiationException, InvocationTargetException {

        // subclasses should not have this check
//        if(clazz.isAnnotationPresent(GlueBean.class))
//            throw new NotABeanException(clazz);

        if(impl.getConstructors().length == 0)
            throw new NoPublicConstructorException(impl);

        Constructor<?> constructor = null;
        List<Field> fields = null;

        //search for annotated constructors
        boolean hasConstrInject = false;
        for(Constructor<?> c : impl.getConstructors()) {
            if(c.isAnnotationPresent(Inject.class)) {
                if(hasConstrInject){
                    throw new MultipleConstructorsWithInjectException(c);
                } else {
                    hasConstrInject = true;
                    constructor = c;
                }

            }
        }

        //todo create object from constructors
        fields = new ArrayList<>();
        if(hasConstrInject) {
            for(Field f: impl.getFields()) {
                fields.add(f);
            }
            List<Class<?>> constructorFields = Arrays.asList(constructor.getParameterTypes());
            //todo metoda ce verifica daca fiecare param de constructor exista in MAP, daca nu, trebuie sa ne intoarcem la map.bind
            for(Class<?> field : constructorFields) {
                if(field.isAnnotationPresent(GlueBean.class)) {
                    //find annotation param NAME to see if bean is Singleton
                    if(field.isAnnotationPresent(Prototype.class)) {
                        String prototypeName = field.getAnnotation(Prototype.class).name();
                        if(prototypeName.equals("")) {
                            //todo generate new random prototype
                        } else {
                            //todo generate prototype with name and add to beanMap
                        }
                    } else if(field.isAnnotationPresent(Singleton.class)) {
                        //todo generate singleton and add to beanMap
                    }
                }
            }

        }

        //if no constructors, search for annotated fields
        if(!hasConstrInject) {
            for(Field f : impl.getFields()) {
                if(f.isAnnotationPresent(Inject.class)) {
                    fields.add(f);
                }
            }
        }

        //if no fields annotated, search for default constructor

        try {
            constructor = impl.getConstructor();
        } catch (NoSuchMethodException e) {
            //
        } finally {
            if(constructor != null) {
                return constructor.newInstance();
            } else {
                throw new NoPublicConstructorException(impl);
            }
        }
    }

    public static Object createDefaultInstance(Class<?> clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return clazz.getConstructor().newInstance();
    }

}
