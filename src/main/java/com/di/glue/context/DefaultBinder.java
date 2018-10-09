package com.di.glue.context;

import com.di.glue.context.annotation.Inject;
import com.di.glue.context.annotation.Prototype;
import com.di.glue.context.annotation.Qualifier;
import com.di.glue.context.annotation.Singleton;
import com.di.glue.context.data.*;
import com.di.glue.context.exception.*;
import com.di.glue.context.util.LogUtils;
import org.apache.log4j.Logger;
import org.reflections.ReflectionUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * Class used to contain all binding objects for SINGLETON and NAMED PROXY types.
 *
 */
public class DefaultBinder implements Binder {

    static final String ILLEGAL_BINDING = "Illegal binding for class: %s . The interface, its implementation and scope must be non null.";
    static final Logger log = Logger.getLogger(DefaultBinder.class);

    private MultiMap<Class<?>, BindIdentifier, ImplUnit> beanMap;

    public DefaultBinder() {
        beanMap = new HashMultiMap<>();
    }

    @Override
    public void bind(Class<?> abstr, Class<?> impl, Scope scope, String qualifier) throws NotASuperclassException {
        if(abstr == null || impl == null || scope == null)
            throw new IllegalArgumentException(String.format(ILLEGAL_BINDING, abstr));

        try {
            beanMap.put(abstr, BindIdentifier.of(scope, qualifier), ImplUnit.of(impl, null));
        } catch (DuplicateEntryException e) {
            log.error(e.getMessage());
        }
    }

    // must be called after all the binds have been added
    // creates all singleton instances and adds them into the beanMap
    @Override
    public void init() {
        for(MultiMapEntry<Class<?>, BindIdentifier, ImplUnit> entry : beanMap.entrySet()) {
            Scope scope = entry.getSubKey().getScope();
            if(scope == Scope.SINGLETON)
                getBean(entry.getKey(), scope, entry.getSubKey().getQualifier(), null);
        }
    }

    // default scope is Singleton
    public Object getBean(Class<?> clazz) { return getBean(clazz, Scope.SINGLETON, null, null); }

    public Object getBean(Class<?> clazz, Scope scope) { return getBean(clazz, scope, null, null); }

    private Object getBean(Class<?> clazz, Type genericType) {
        return getBean(clazz, null, null, genericType);
    }

    public Object getBean(Class<?> clazz, Scope scope, String qualifier) {
        return getBean(clazz, scope, qualifier, null);
    }

    public Object getBean(Class<?> clazz, Scope scope, String qualifier, Type genericType) {
        if(scope == null)
            scope = Scope.SINGLETON;
        if(clazz == null)
            throw new IllegalArgumentException("Class is null.");

        Object result = null;
        try {
            if(beanMap.containsKey(clazz)) {
                Map<BindIdentifier,ImplUnit> subMap = beanMap.getSubmap(clazz);
                BindIdentifier bindIdentifier = BindIdentifier.of(scope, qualifier);
                if(subMap.containsKey(bindIdentifier)) {
                    if(scope == Scope.SINGLETON) {
                        result = subMap.get(bindIdentifier).getInstance();
                        if (result == null) {
                            Class<?> implClazz = subMap.get(bindIdentifier).getImplClazz();
                            result = createNewObject(implClazz);
                            subMap.put(bindIdentifier, ImplUnit.of(implClazz, result));
                        }
                    } else {
                        result = createNewObject(subMap.get(bindIdentifier).getImplClazz());
                    }
                }
            } else if(clazz.isAssignableFrom(List.class)) {
                return getListBeans(genericType);
            } else {
                log.info("Bean does not exist: " + clazz.getSimpleName());
            }
        } catch (IllegalAccessException|InstantiationException e) {
            e.printStackTrace();
        }

        return result;
    }

    private Object getListBeans(Type genericType) throws IllegalAccessException, InstantiationException {

        if(genericType == null) throw new RuntimeException("List has no generic type defined.");

        ParameterizedTypeImpl generic = null;
        if(genericType instanceof ParameterizedTypeImpl)
            generic = (ParameterizedTypeImpl)genericType;

        List<Object> listBeans = new ArrayList<>();
        if(generic != null) {
            Class<?> genClass = (Class<?>) generic.getActualTypeArguments()[0];
            if(beanMap.containsKey(genClass)) {
                Map<BindIdentifier,ImplUnit> subMap = beanMap.getSubmap(genClass);
                for(ImplUnit implUnit : subMap.values()) {
                    if(implUnit.getInstance() != null)
                        listBeans.add(implUnit.getInstance());
                    else
                        listBeans.add(createNewObject(implUnit.getImplClazz()));
                }
            } else {
                throw new NotABeanException(genClass, " - during list binding.");
            }
        }
        return listBeans;
    }

    private Object createNewObject(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        InjectionType injectionType = getInjectionType(clazz);
        if(injectionType == InjectionType.CONSTRUCTOR){
            return createNewObjectByConstructor(clazz);
        } else if(injectionType == InjectionType.FIELD) {
            return createNewObjectByFields(clazz);
        } else {
            return createNewObjectDefault(clazz);
        }
    }

    private InjectionType getInjectionType(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for(int i=0; i<constructors.length; i++) {
            if(constructors[i].isAnnotationPresent(Inject.class))
                return InjectionType.CONSTRUCTOR;
        }
        Field[] fields = clazz.getDeclaredFields();
        for(int i=0; i<fields.length; i++) {
            if(fields[i].isAnnotationPresent(Inject.class))
                return InjectionType.FIELD;
        }
        return InjectionType.NONE;
    }

    private Object createNewObjectByConstructor(Class<?> clazz) {
        log.info("Creating new object by constructor injection: " + clazz);
        Object object = null;
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        Constructor<?> constr = null;
        // find constructor and check if multiple constructors have @Inject
        for(int i=0; i<constructors.length; i++) {
            if(constructors[i].isAnnotationPresent(Inject.class)) {
                if(constr != null) throw new MultipleConstructorsWithInjectException(clazz);
                constr = constructors[i];
            }
        }

        List<Object> paramInstancesList = new ArrayList<>();
        if(constr != null) {
            Class<?>[] fieldClass = constr.getParameterTypes();
            Type[] fieldGeneric = constr.getGenericParameterTypes();
            Annotation[][] annot = constr.getParameterAnnotations();
            for(int i=0; i < fieldClass.length; i++) {
                // default scope is Singleton
                Scope scope = Scope.SINGLETON;
                String qualifier = null;
                Annotation[] fieldAnnotations = annot[i];

                for(Annotation a : fieldAnnotations) {
                    if(a instanceof Prototype)
                        scope = Scope.PROTOTYPE;
                    else if(a instanceof Qualifier) {
                        Qualifier q = (Qualifier) a;
                        qualifier = q.name();
                    }
                }

                paramInstancesList.add(getBean(fieldClass[i], scope, qualifier, fieldGeneric[i]));
            }
        } else {
            throw new RuntimeException("No constructor annotated with @Inject for class: " + clazz.getSimpleName());
        }
        return createObjectByConstrAndParams(constr, paramInstancesList);
    }

    private Object createNewObjectByFields(Class<?> clazz) {
        log.info("Creating new object by field injection: " + clazz);
        Object object = null;
        Field[] fields = clazz.getDeclaredFields();
        List<Class> annotatedFields = new ArrayList<>();
        List<Annotation[]> annotations = new ArrayList<>();

        //find annotated fields
        for( Field field : fields) {
            if(field.isAnnotationPresent(Inject.class)) {
                annotatedFields.add(field.getType());
                Annotation annotation_1 = field.getAnnotation(Singleton.class);
                if(annotation_1 == null)
                    annotation_1 = field.getAnnotation(Prototype.class);
                Annotation annotation_2 = field.getAnnotation(Qualifier.class);

                Annotation[] fieldAnnotations = new Annotation[2];
                fieldAnnotations[0] = annotation_1;
                fieldAnnotations[1] = annotation_2;
                annotations.add(fieldAnnotations);
            }
        }
        //find constructor that only contains the annontated fields
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        List<Class<?>> constrFields = null;
        Constructor<?> constr = null;
        for(Constructor<?> constructor : constructors) {
            if(constructor.getParameterCount() == annotatedFields.size()){
                constrFields = new ArrayList<>();
                for(Class<?> field : constructor.getParameterTypes()) {
                    constrFields.add(field);
                }
                if(constrFields.size()!=0 && constrFields.equals(annotatedFields)) {
                    constr = constructor;
                }
            }
        }
        if(constr == null) throw new NoValidConstructorException(clazz);
        List<Object> paramInstancesList = new ArrayList<>();

        Class<?>[] fieldClass = constr.getParameterTypes();
        Type[] fieldGeneric = constr.getGenericParameterTypes();
        for(int i=0; i < fieldClass.length; i++) {
            Scope scope = Scope.SINGLETON;
            String qualifier = null;
            for(Annotation a : annotations.get(i)) {
                if(a instanceof Prototype)
                    scope = Scope.PROTOTYPE;
                else if(a instanceof Qualifier) {
                    Qualifier q = (Qualifier) a;
                    qualifier = q.name();
                }
            }

            paramInstancesList.add(getBean(fieldClass[i], scope, qualifier, fieldGeneric[i]));
        }

        return createObjectByConstrAndParams(constr, paramInstancesList);
    }

    private Object createNewObjectDefault(Class<?> clazz) throws IllegalAccessException, InstantiationException {
        log.info("Creating new object (no injection): " + clazz);
        return clazz.newInstance();
    }

    private Object createObjectByConstrAndParams(Constructor<?> constructor, List<Object> paramInstancesList) {
        Object object = null;
        try {
            Object[] paramsArray = paramInstancesList.toArray();
            object = constructor.newInstance(paramsArray);
        } catch (InstantiationException|IllegalAccessException|InvocationTargetException e) {
            e.printStackTrace();
        }
        return object;
    }

    @Override
    public List<MultiMapEntry<Class<?>, BindIdentifier, ImplUnit>> getBindings() {
        return beanMap.getEntries();
    }

}
