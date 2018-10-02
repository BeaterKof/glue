package com.di.glue.context;

import com.di.glue.context.annotation.*;
import com.di.glue.context.data.*;
import com.di.glue.context.util.LogUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.stream.Collectors;

public class GlueApplicationContext implements ApplicationContext {

    private static final Logger log = Logger.getLogger(GlueApplicationContext.class);
    //todo: beautify config
    private static final String ROOT_PATH = "com";

    private Binder binder;
    private BindingConfigurer configurer;
    private boolean annotationScanEnabled;

    public GlueApplicationContext() {
        binder = new DefaultBinder();
        annotationScanEnabled = true;
        BindingConfigurer configurer = new DefaultBindingConfigurer();
        initContext();
    }

    public GlueApplicationContext(BindingConfigurer externalConfigurer, boolean annotationScanEnabled) {
        if(externalConfigurer==null) throw new IllegalArgumentException();
        this.annotationScanEnabled = annotationScanEnabled;
        BasicConfigurator.configure();
        this.binder = new DefaultBinder();
        this.configurer = externalConfigurer;
        initContext();
    }

    private void initContext() {
        if(annotationScanEnabled) scanForAnnotatedClasses();
        addConfigurer(this.configurer);
        binder.init();
    }

    private void scanForAnnotatedClasses() {
        Reflections refInterfaces = new Reflections(ROOT_PATH);
        Set<Class<?>> allInterfaces = refInterfaces.getTypesAnnotatedWith(GlueBean.class, true);

        Reflections refComponents = new Reflections(ROOT_PATH);
        Set<Class<?>> componentClasses = refComponents.getTypesAnnotatedWith(Component.class);

        BindingConfigurer configurer = new DefaultBindingConfigurer();
        for(Class<?> interf : allInterfaces) {
            for(Class<?> componentClass : componentClasses) {
                if(interf.isAssignableFrom(componentClass)) {
                    Scope scope = componentClass.isAnnotationPresent(Singleton.class) ? Scope.SINGLETON : Scope.PROTOTYPE;
                    String qualifier = componentClass.isAnnotationPresent(Qualifier.class) ?
                            componentClass.getDeclaredAnnotation(Qualifier.class).name() : null;
                    binder.bind(interf, componentClass, scope, qualifier);
                }
            }
        }
    }

    public void addConfigurer(BindingConfigurer configurer) {
        if(configurer == null)
            throw new IllegalArgumentException("Configurer passed to GlueApplicationContext is null.");
        configurer.getBindings()
                .forEach(item -> {
                    binder.bind(item.getAbstraction(), item.getImplementation(), item.getScope(), item.getName());
                });
        //todo: chech if necessary wait for bindings to end
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Binder getBinder() {
        return binder;
    }

    public void setBinder(Binder binder) {
        this.binder = binder;
    }

    public Object getBean(Class<?> clazz) {
        return binder.getBean(clazz);
    }

    public Object getBean(Class<?> clazz, Scope scope) {
        return binder.getBean(clazz, scope, null, null);
    }

    public Object getBean(Class<?> clazz, Scope scope, String qualifier) {
        return binder.getBean(clazz, scope, qualifier, null);
    }

    public boolean isAnnotationScanEnabled() {
        return annotationScanEnabled;
    }

    public void setAnnotationScanEnabled(boolean annotationScanEnabled) {
        this.annotationScanEnabled = annotationScanEnabled;
    }

    @Override
    public void logBindings() {
        log.info("\n\nLOGGING BINDINGS:\n");
        this.getBinder().getBindings().stream()
                .forEach(e -> log.info(LogUtils.getComplexBindingString(e)));
    }
}
