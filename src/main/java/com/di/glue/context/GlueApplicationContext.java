package com.di.glue.context;

import com.di.glue.context.data.BindingConfigurer;
import com.di.glue.context.data.ImplUnit;
import com.di.glue.context.data.TypeUnit;
import javafx.util.Pair;
import org.apache.log4j.BasicConfigurator;

import java.util.List;

public class GlueApplicationContext implements ApplicationContext {

    private Binder binder;

    public GlueApplicationContext() {
        binder = new DefaultBinder();
    }

    public GlueApplicationContext(BindingConfigurer configurer) {
        BasicConfigurator.configure();
        binder = new DefaultBinder();
        addConfigurer(configurer);
        binder.createDependecies();
    }

    public void addConfigurer(BindingConfigurer configurer) {
        if(configurer == null)
            throw new IllegalArgumentException("Configurer passed to GlueApplicationContext is null.");
        configurer.getBindings()
                .forEach(item -> {
                    // todo:
                    binder.bind(TypeUnit.of(item.getAbstraction(), item.getBeanType()), ImplUnit.of(item.getImplementation(), item.getName()));
                });
    }

    public Binder getBinder() {
        return binder;
    }

    public void setBinder(Binder binder) {
        this.binder = binder;
    }

    public List<Pair<Class<?>, Class<?>>> getBindingsList() {
        return null;
    }

    @Override
    public void printBindings() {
        this.getBinder().getBindings().stream()
                .forEach(e -> System.out.println(e.getKey().getClazz().getSimpleName() + " -> (" + e.getSubKey() + " , " + e.getValue() + ")"));
    }
}
