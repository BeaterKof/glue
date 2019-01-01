package com.di.glue.context;

import com.di.glue.context.annotation.Inject;
import com.di.glue.context.annotation.Prototype;
import com.di.glue.context.annotation.Qualifier;
import com.di.glue.context.annotation.Singleton;
import com.di.glue.context.data.*;
import com.di.glue.context.data.bean.BindIdentifier;
import com.di.glue.context.data.bean.ImplUnit;
import com.di.glue.context.data.bean.ImplUnitSingleton;
import com.di.glue.context.exception.*;
import org.apache.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Class used to contain all binding objects for SINGLETON and NAMED PROXY types.
 */
public class DefaultBeanFactory implements BeanFactory {

    private final String ILLEGAL_BINDING = "Illegal binding for class: %s, implementation: %s, scope: %s, qualifier: %s . " +
            "The interface, its implementation and scope must be non null.";
    private final Logger log = Logger.getLogger(DefaultBeanFactory.class);

    private MultiMap<Class<?>, BindIdentifier, ImplUnit> beanMap;

    // bean stack used to avoid circular binding
    private Set<Class<?>> beanSet = new HashSet<>();
    // this trigger is set to true when a List type is binded
    private boolean listTrigger = false;

    public DefaultBeanFactory() {
        beanMap = new HashMultiMap<>();
    }

    public void bind(Class<?> abstraction, Class<?> impl, Scope scope, String qualifier) throws NotASuperclassException {
        bindingArgumentsAreValid(abstraction, impl, scope, qualifier);
        try {
            beanMap.put(abstraction, BindIdentifier.of(scope, qualifier), ImplUnitSingleton.of(impl, null));
        } catch (DuplicateEntryException e) {
            log.error(e.getMessage());
        }
    }

    private void bindingArgumentsAreValid(Class<?> abstr, Class<?> impl, Scope scope, String qualifier) {
        if (abstr == null || impl == null || scope == null)
            throw new IllegalArgumentException(String.format(ILLEGAL_BINDING, abstr, impl, scope, qualifier));
    }

    public void init() {
        for (MultiMapEntry<Class<?>, BindIdentifier, ImplUnit> entry : beanMap.entrySet()) {
            Scope scope = entry.getSubKey().getScope();
            if (scope == Scope.SINGLETON) {
                getBean(entry.getKey(), scope, entry.getSubKey().getQualifier(), null);
            }
        }
    }

    // default scope is Singleton
    public Object getBean(Class<?> clazz) {
        return getBean(clazz, Scope.SINGLETON, null, null);
    }

    public Object getBean(Class<?> clazz, Scope scope) {
        return getBean(clazz, scope, null, null);
    }

    private Object getBean(Class<?> clazz, Type genericType) {
        return getBean(clazz, null, null, genericType);
    }

    public Object getBean(Class<?> clazz, Scope scope, String qualifier) {
        return getBean(clazz, scope, qualifier, null);
    }

    private void checkCircularBinding(Class<?> clazz) {
        // circular binding check
        if (beanSet.contains(clazz)) {
            log.error("CircularBindingException is thrown for: " + clazz.getSimpleName());
            log.info("Cleaning class stack for testing porpuses...");
            beanSet.clear();
            throw new CircularBindingException(clazz);
        }
        // increment circular
        beanSet.add(clazz);
    }

    private Scope checkClassAndGetScope(Class<?> clazz, Scope scope) {
        if (clazz == null)
            throw new IllegalArgumentException("Class is null.");
        return scope == null ? Scope.SINGLETON : scope;
    }

    private Object createAndAddToMapNewSingletonInstance(BindIdentifier bindIdentifier, Map<BindIdentifier, ImplUnit> subMap) throws IllegalAccessException, InstantiationException {
        Object result = null;
        Class<?> implClazz = subMap.get(bindIdentifier).getImplClazz();
        result = createNewObject(implClazz);
        subMap.put(bindIdentifier, ImplUnitSingleton.of(implClazz, result));
        return result;
    }

    private Optional<Object> getSingletonObject(BindIdentifier bindIdentifier, Map<BindIdentifier, ImplUnit> subMap) throws IllegalAccessException, InstantiationException {
        ImplUnitSingleton singletonImplUnit = (ImplUnitSingleton) subMap.get(bindIdentifier);
        Optional<Object> result = Optional.ofNullable(singletonImplUnit.getInstance());
        if (!result.isPresent()) {
            result = Optional.ofNullable(createAndAddToMapNewSingletonInstance(bindIdentifier, subMap));
        }
        return result;
    }

    private Optional<Object> getBeanImplementation(Class<?> clazz, Scope scope, String qualifier) throws IllegalAccessException, InstantiationException {
        Map<BindIdentifier, ImplUnit> subMap = beanMap.getSubmap(clazz);
        BindIdentifier bindIdentifier = BindIdentifier.of(scope, qualifier);
        if (subMap.containsKey(bindIdentifier)) {
            return scope == Scope.SINGLETON ? getSingletonObject(bindIdentifier, subMap)
                    : Optional.of(createNewObject(subMap.get(bindIdentifier).getImplClazz()));
        }
        return Optional.empty();
    }

    private Object setTriggerAndGetListBeans(Type genericType) throws InstantiationException, IllegalAccessException {
        listTrigger = true;
        Object result = getListBeans(genericType);
        listTrigger = false;
        return result;
    }

    private Object getBean(Class<?> clazz, Scope scope, String qualifier, Type genericType) {
        log.info("---> " + beanSet.size() + " Entering getBean for class: " + clazz.getSimpleName());

        scope = checkClassAndGetScope(clazz, scope);
        // in case this is a list, we skip the circular binding check
        if (!listTrigger) {
            checkCircularBinding(clazz);
        }
        Object result = null;
        try {
            if (beanMap.containsKey(clazz))
                result = getBeanImplementation(clazz, scope, qualifier).orElse(null);
            else if (clazz.isAssignableFrom(List.class))
                result = setTriggerAndGetListBeans(genericType);
            else
                log.info("Bean does not exist: " + clazz.getSimpleName());

        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        // remove class from stack
        beanSet.remove(clazz);
        log.info("<--- " + beanSet.size() + " Exiting getBean for class: " + clazz);
        return result;
    }

    private void populateListBeans(List<Object> listBeans, ParameterizedTypeImpl generic) throws IllegalAccessException, InstantiationException {
        Class<?> genClass = (Class<?>) generic.getActualTypeArguments()[0];
        if (beanMap.containsKey(genClass)) {
            addInstanceUnitsToListBeansList(listBeans, genClass);
        } else {
            throw new NotABeanException(genClass, " - during list binding.");
        }
    }

    private void addInstanceUnitsToListBeansList(List<Object> listBeans, Class<?> genClass) throws InstantiationException, IllegalAccessException {
        Map<BindIdentifier, ImplUnit> subMap = beanMap.getSubmap(genClass);
        for (ImplUnit implUnit : subMap.values()) {
            addInstanceUnitToListBeansList(listBeans, implUnit);
        }
    }

    private void addInstanceUnitToListBeansList(List<Object> listBeans, ImplUnit implUnit) throws InstantiationException, IllegalAccessException {
        if (implUnit instanceof ImplUnitSingleton)
            listBeans.add(((ImplUnitSingleton) implUnit).getInstance());
        else
            listBeans.add(createNewObject(implUnit.getImplClazz()));
    }

    private Object getListBeans(Type genericType) throws IllegalAccessException, InstantiationException {
        if (genericType == null) throw new RuntimeException("List has no generic type defined.");

        ParameterizedTypeImpl generic = null;
        if (genericType instanceof ParameterizedTypeImpl)
            generic = (ParameterizedTypeImpl) genericType;

        List<Object> listBeans = new ArrayList<>();
        if (generic != null) {
            populateListBeans(listBeans, generic);
        }
        return listBeans;
    }

    private Object createNewObject(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        InjectionType injectionType = getInjectionType(clazz);
        if (injectionType == InjectionType.CONSTRUCTOR) {
            return createNewObjectByConstructor(clazz);
        } else if (injectionType == InjectionType.FIELD) {
            return createNewObjectByFields(clazz);
        } else {
            return createNewObjectDefault(clazz);
        }
    }

    private InjectionType getInjectionType(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Inject.class))
                return InjectionType.CONSTRUCTOR;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Inject.class))
                return InjectionType.FIELD;
        }
        return InjectionType.NONE;
    }

    private Constructor<?> getConstructorIfAnnotIsPresent(Class<?> clazz) {
        Constructor<?> resultConstructor = null;
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            resultConstructor = getConstructorIfAnnotPresent(clazz, resultConstructor, constructor);
        }
        return resultConstructor;
    }

    private Constructor<?> getConstructorIfAnnotPresent(Class<?> clazz, Constructor<?> constr, Constructor<?> constructor) {
        if (constructor.isAnnotationPresent(Inject.class)) {
            if (constr != null) throw new MultipleConstructorsWithInjectException(clazz);
            constr = constructor;
        }
        return constr;
    }

    private Object createNewObjectByConstructor(Class<?> clazz) {
        log.info("Creating new object by constructor injection: " + clazz);

        // find constructor and check if multiple constructors have @Inject
        Constructor<?> constr = getConstructorIfAnnotIsPresent(clazz);
        List<Object> paramInstancesList = new ArrayList<>();
        if (constr != null) {
            processAnnotatedParams(constr, paramInstancesList);
        } else {
            throw new RuntimeException("No constructor annotated with @Inject for class: " + clazz.getSimpleName());
        }
        return createObjectByConstrAndParams(constr, paramInstancesList);
    }

    private void processAnnotatedParams(Constructor<?> constr, List<Object> paramInstancesList) {
        Class<?>[] fieldClass = constr.getParameterTypes();
        Type[] fieldGeneric = constr.getGenericParameterTypes();
        Annotation[][] annotations = constr.getParameterAnnotations();
        for (int i = 0; i < fieldClass.length; i++) {
            processAnnotationsForConstructor(paramInstancesList, fieldClass[i], fieldGeneric[i], annotations[i]);

        }
    }

    private void processAnnotationsForConstructor(List<Object> paramInstancesList, Class<?> fieldClass, Type genericType, Annotation[] fieldAnnotations1) {
        // default scope is Singleton
        Scope scope = Scope.SINGLETON;
        String qualifier = null;
        Annotation[] fieldAnnotations = fieldAnnotations1;
        // for each annotation, check if it matches Singleton, Prototype or Qualifier
        for (Annotation a : fieldAnnotations) {
            if (a instanceof Prototype)
                scope = Scope.PROTOTYPE;
            else if (a instanceof Qualifier) {
                Qualifier q = (Qualifier) a;
                qualifier = q.name();
            }
        }
        paramInstancesList.add(getBean(fieldClass, scope, qualifier, genericType));
    }

    private Object createNewObjectByFields(Class<?> clazz) {
        log.info("Creating new object by field injection: " + clazz);
        Object object = null;
        Field[] fields = clazz.getDeclaredFields();
        List<Class> annotatedFields = new ArrayList<>();
        List<Annotation[]> annotations = new ArrayList<>();

        populateAnnotatedFieldsList(fields, annotatedFields, annotations);
        Constructor<?> constr = getConstructorMatchingAnnotatedFields(clazz, annotatedFields);
        List<Object> paramInstancesList = getParamInstancesList(clazz, annotations, constr);

        return createObjectByConstrAndParams(constr, paramInstancesList);
    }

    private List<Object> getParamInstancesList(Class<?> clazz, List<Annotation[]> annotations, Constructor<?> constr) {
        if (constr == null) throw new NoValidConstructorException(clazz);
        List<Object> paramInstancesList = new ArrayList<>();

        Class<?>[] fieldClass = constr.getParameterTypes();
        Type[] fieldGeneric = constr.getGenericParameterTypes();
        for (int i = 0; i < fieldClass.length; i++) {
            checkAndAddNewInstanceToList(annotations.get(i), paramInstancesList, fieldClass[i], fieldGeneric[i]);
        }
        return paramInstancesList;
    }

    private Constructor<?> getConstructorMatchingAnnotatedFields(Class<?> clazz, List<Class> annotatedFields) {
        //find constructor that only contains the annontated fields
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        List<Class<?>> constrFields = null;
        Constructor<?> constr = null;
        for (Constructor<?> constructor : constructors) {
            constr = getConstructorMatchingAnnotatedFieldsList(annotatedFields, constr, constructor);
        }
        return constr;
    }

    private void populateAnnotatedFieldsList(Field[] fields, List<Class> annotatedFields, List<Annotation[]> annotations) {
        //find annotated fields
        for (Field field : fields) {
            if (field.isAnnotationPresent(Inject.class)) {
                addFieldsAnnotationsToList(annotatedFields, annotations, field);
            }
        }
    }

    private void checkAndAddNewInstanceToList(Annotation[] annotations, List<Object> paramInstancesList, Class<?> fieldClass, Type genericType) {
        Scope scope = Scope.SINGLETON;
        String qualifier = null;
        for (Annotation a : annotations) {
            if (a instanceof Prototype)
                scope = Scope.PROTOTYPE;
            else if (a instanceof Qualifier) {
                Qualifier q = (Qualifier) a;
                qualifier = q.name();
            }
        }
        paramInstancesList.add(getBean(fieldClass, scope, qualifier, genericType));
    }

    private Constructor<?> getConstructorMatchingAnnotatedFieldsList(List<Class> annotatedFields, Constructor<?> constr, Constructor<?> constructor) {
        List<Class<?>> constrFields;
        if (constructor.getParameterCount() == annotatedFields.size()) {
            constr = getValidConstructor(annotatedFields, constr, constructor);
        }
        return constr;
    }

    private Constructor<?> getValidConstructor(List<Class> annotatedFields, Constructor<?> constr, Constructor<?> constructor) {
        List<Class<?>> constrFields;
        constrFields = new ArrayList<>();
        for (Class<?> field : constructor.getParameterTypes()) {
            constrFields.add(field);
        }
        if (constrFields.size() != 0 && constrFields.equals(annotatedFields)) {
            constr = constructor;
        }
        return constr;
    }

    private void addFieldsAnnotationsToList(List<Class> annotatedFields, List<Annotation[]> annotations, Field field) {
        annotatedFields.add(field.getType());
        Annotation annotation_1 = field.getAnnotation(Singleton.class);
        if (annotation_1 == null)
            annotation_1 = field.getAnnotation(Prototype.class);
        Annotation annotation_2 = field.getAnnotation(Qualifier.class);

        Annotation[] fieldAnnotations = new Annotation[2];
        fieldAnnotations[0] = annotation_1;
        fieldAnnotations[1] = annotation_2;
        annotations.add(fieldAnnotations);
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
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return object;
    }

    public List<MultiMapEntry<Class<?>, BindIdentifier, ImplUnit>> getBindings() {
        return beanMap.getEntries();
    }

}
