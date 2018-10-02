package com.di.glue.context;

import com.di.glue.context.annotation.Inject;
import com.di.glue.context.data.*;
import com.di.glue.context.exception.DuplicateEntryException;
import com.di.glue.context.exception.MultipleConstructorsWithInjectException;
import com.di.glue.context.exception.NoValidConstructorException;
import com.di.glue.context.exception.NotASuperclassException;
import com.di.glue.context.util.LogUtils;
import org.apache.log4j.Logger;
import org.reflections.ReflectionUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class used to contain all binding objects for SINGLETON and NAMED PROXY types.
 *
 */
public class DefaultBinder implements Binder {

    static final Logger log = Logger.getLogger(DefaultBinder.class);

    private MultiMap<Class<?>, BindIdentifier, ImplUnit> beanMap;

    public DefaultBinder() {
        beanMap = new HashMultiMap<>();
    }

    @Override
    public void bind(Class<?> abstr, Class<?> impl, Scope scope, String qualifier) throws NotASuperclassException {
        if(abstr == null)
            throw new IllegalArgumentException("Abstraction to be binded is null.");
        if(impl == null)
            throw new IllegalArgumentException("Implementation to be binded is null.");

        try {
            beanMap.put(abstr, BindIdentifier.of(scope, qualifier), ImplUnit.of(impl, null));
        } catch (DuplicateEntryException e) {
            log.error(e.getMessage());
        }
    }

    // must be called after all the binds have been added
    // creates all singleton instances
    @Override
    public void init() {
        for(MultiMapEntry<Class<?>, BindIdentifier, ImplUnit> entry : beanMap.entrySet()) {
            if(entry.getSubKey().getScope() == Scope.SINGLETON)
                getBean(entry.getKey(), entry.getSubKey().getScope(), entry.getSubKey().getQualifier(), null);
        }
    }

    public Object getBean(Class<?> clazz) {
        return getBean(clazz, null, null, null);
    }

    private Object getBean(Class<?> clazz, Type genericType) {
        return getBean(clazz, null, null, genericType);
    }

    public Object getBean(Class<?> clazz, Scope scope, String qualifier, Type genericType) {
        Object result = null;
        try {
            if(scope == null)
                scope = Scope.SINGLETON;
            if(clazz.isAssignableFrom(List.class))
                return getListBeans(genericType);
            if(beanMap.containsKey(clazz)) {
                Map<BindIdentifier,ImplUnit> subMap = beanMap.getSubmap(clazz);
                BindIdentifier bindIdentifier = BindIdentifier.of(scope, qualifier);
                if(subMap.containsKey(bindIdentifier)) {
                    if(scope == Scope.SINGLETON) {
                        result = subMap.get(bindIdentifier).getInstance();
                        if(result == null) {
                            Class<?> implClazz = subMap.get(bindIdentifier).getImplClazz();
                            result = createNewObject(implClazz);
                            subMap.put(bindIdentifier, ImplUnit.of(implClazz, result));
                        }
                    } else {
                        result = createNewObject(subMap.get(bindIdentifier).getImplClazz());
                    }
                }
            } else {
                log.info("Bean does not exist: " + clazz.getSimpleName());
            }
        } catch (IllegalAccessException|InstantiationException e) {
            e.printStackTrace();
        }

        return result;
    }

    public Object getListBeans(Type genericType) throws IllegalAccessException, InstantiationException {

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
            }
        }
        return listBeans;
    }

    // gets the bindName=null, qualifier=null instance
    private Object getDefaultSingletonInstance(Class<?> clazz) {
        TypeUnit typeUnit = TypeUnit.of(clazz, null);
        // get submap for typeUnit, check if it has less than
        return null;
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
            for(int i=0; i < fieldClass.length; i++) {
                paramInstancesList.add(getBean(fieldClass[i], fieldGeneric[i]));
            }
        }
        return createObjectByConstrAndParams(constr, paramInstancesList);
    }

    private Object createNewObjectByFields(Class<?> clazz) {
        log.info("Creating new object by field injection: " + clazz);
        Object object = null;
        Field[] fields = clazz.getDeclaredFields();
        List<Class> annotatedFields = new ArrayList<>();
        //find annotated fields
        for( Field field : fields) {
            if(field.isAnnotationPresent(Inject.class)) {
                annotatedFields.add(field.getType());
            }
        }
        //find constructor that only contains the annontated fields
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        List<Class> constrFields = null;
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
            paramInstancesList.add(getBean(fieldClass[i], fieldGeneric[i]));
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
