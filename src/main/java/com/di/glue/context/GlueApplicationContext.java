package com.di.glue.context;

import com.di.glue.context.data.BindingConfigurer;
import com.di.glue.context.data.TypeUnit;
import javafx.util.Pair;
import org.apache.log4j.BasicConfigurator;

import java.util.List;

public class GlueApplicationContext implements ApplicationContext {

    private Binder binder;
    private BindingMapper bindingMapper;

    public GlueApplicationContext() {
        binder = new DefaultBinder();
        bindingMapper = new BindingMapper();
    }

    public GlueApplicationContext(BindingConfigurer configurer) {
        BasicConfigurator.configure();
        binder = new DefaultBinder();
        addConfigurer(configurer);
        bindingMapper = new BindingMapper();
    }

    public void addConfigurer(BindingConfigurer configurer) {
        if(configurer == null)
            throw new IllegalArgumentException("Configurer passed to GlueApplicationContext is null.");
        configurer.getBindings()
                .forEach(item -> {
                    binder.bind(TypeUnit.of(item.getAbstraction(), item.getBeanType()), item.getName(), item.getImplementation());
                });
    }

    public Binder getBinder() {
        return binder;
    }

    public void setBinder(Binder binder) {
        this.binder = binder;
    }

    public BindingMapper getBindingMapper() {
        return bindingMapper;
    }

    public void setBindingMapper(BindingMapper bindingMapper) {
        this.bindingMapper = bindingMapper;
    }

    public List<Pair<Class<?>, Class<?>>> getBindingsList() {
        return null;
    }

    @Override
    public void printBindings() {
        this.getBinder().getBindings().stream()
                .forEach(e -> System.out.println(e.getKey().getClazz().getName() + " -> (" + e.getSubKey() + " , " + e.getValue() + ")"));
    }
}
