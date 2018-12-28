package com.di.glue.context;

import com.di.glue.context.annotation.*;
import com.di.glue.context.data.*;
import com.di.glue.context.util.LogUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.reflections.Reflections;

import java.util.Set;

public class GlueApplicationContext implements ApplicationContext {

    private static final Logger log = Logger.getLogger(GlueApplicationContext.class);

    private BeanFactory beanFactory;
    private String rootPath;
    private BindingConfigurer configurer;
    private boolean annotationScanEnabled;

    public GlueApplicationContext() {
        beanFactory = new DefaultBeanFactory();
        this.rootPath = "com";
        annotationScanEnabled = true;
        BindingConfigurer configurer = new DefaultBindingConfigurer();
        initContext();
    }

    public GlueApplicationContext(Class beanFactory) {
        try {
            this.beanFactory = (BeanFactory) beanFactory.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        this.rootPath = "com";
        annotationScanEnabled = true;
        BindingConfigurer configurer = new DefaultBindingConfigurer();
        initContext();
    }

    public GlueApplicationContext(BindingConfigurer externalConfigurer, String rootPath, boolean annotationScanEnabled) {
        if(externalConfigurer==null) throw new IllegalArgumentException();
        this.rootPath = rootPath;
        this.annotationScanEnabled = annotationScanEnabled;
        BasicConfigurator.configure();
        this.beanFactory = new DefaultBeanFactory();
        this.configurer = externalConfigurer;
        initContext();
    }

    private void initContext() {
        if(annotationScanEnabled) scanForAnnotatedClasses();
        addConfigurer(this.configurer);
        beanFactory.init();
    }

    private void scanForAnnotatedClasses() {
        Reflections refInterfaces = new Reflections(rootPath);
        Set<Class<?>> allInterfaces = refInterfaces.getTypesAnnotatedWith(GlueBean.class, true);

        Reflections refComponents = new Reflections(rootPath);
        Set<Class<?>> componentClasses = refComponents.getTypesAnnotatedWith(Component.class);

        BindingConfigurer configurer = new DefaultBindingConfigurer();
        for(Class<?> interf : allInterfaces) {
            findAndBindAllAnnotatedClasses(componentClasses, interf);
        }
    }

    private void findAndBindAllAnnotatedClasses(Set<Class<?>> componentClasses, Class<?> interf) {
        for(Class<?> componentClass : componentClasses) {
            bindAnnotatedClass(interf, componentClass);
        }
    }

    private void bindAnnotatedClass(Class<?> interf, Class<?> componentClass) {
        if(interf.isAssignableFrom(componentClass)) {
            Scope scope = componentClass.isAnnotationPresent(Singleton.class) ? Scope.SINGLETON : Scope.PROTOTYPE;
            String qualifier = componentClass.isAnnotationPresent(Qualifier.class) ?
                    componentClass.getDeclaredAnnotation(Qualifier.class).name() : null;
            beanFactory.bind(interf, componentClass, scope, qualifier);
        }
    }

    public void addConfigurer(BindingConfigurer configurer) {
        if(configurer == null)
            throw new IllegalArgumentException("Configurer passed to GlueApplicationContext is null.");
        configurer.getBindings()
                .forEach(item -> {
                    beanFactory.bind(item.getAbstraction(), item.getImplementation(), item.getScope(), item.getName());
                });
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setDefaultBeanFactory(DefaultBeanFactory defaultBeanFactory) {
        this.beanFactory = defaultBeanFactory;
    }

    @Override
    public Object getBean(Class<?> clazz) {
        return beanFactory.getBean(clazz);
    }

    @Override
    public Object getBean(Class<?> clazz, Scope scope) {
        return beanFactory.getBean(clazz, scope);
    }

    @Override
    public Object getBean(Class<?> clazz, Scope scope, String qualifier) {
        return beanFactory.getBean(clazz, scope, qualifier);
    }

    public boolean isAnnotationScanEnabled() {
        return annotationScanEnabled;
    }

    public void setAnnotationScanEnabled(boolean annotationScanEnabled) {
        this.annotationScanEnabled = annotationScanEnabled;
    }

    public String getRootPath() {
        return this.rootPath;
    }

    @Override
    public void logBindings() {
        log.info("\n\nLOGGING BINDINGS:\n");
        this.getBeanFactory().getBindings().forEach(e -> log.info(LogUtils.getComplexBindingString(e)));
    }
}
