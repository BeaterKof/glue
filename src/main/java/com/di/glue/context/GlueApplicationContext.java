package com.di.glue.context;

import com.di.glue.context.data.BindingConfigurer;
import com.di.glue.context.data.ImplUnit;
import com.di.glue.context.data.TypeUnit;
import com.di.glue.context.util.LogUtils;
import javafx.util.Pair;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.util.List;

public class GlueApplicationContext implements ApplicationContext {

    private static final Logger log = Logger.getLogger(GlueApplicationContext.class);

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
    public void logBindings() {
        log.info("\n\nLOGGING BINDINGS:\n");
        this.getBinder().getBindings().stream()
                .forEach(e -> log.info(LogUtils.getComplexBindingString(e)));
    }
}
