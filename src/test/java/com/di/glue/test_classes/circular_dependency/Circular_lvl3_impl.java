package com.di.glue.test_classes.circular_dependency;

import com.di.glue.context.annotation.Component;
import com.di.glue.context.annotation.Inject;
import com.di.glue.context.annotation.Singleton;

/**
 * created by: andrei
 * date: 09.10.2018
 **/
@Component
@Singleton
public class Circular_lvl3_impl implements Circular_lvl3{

    Circular circular;

    @Inject
    public Circular_lvl3_impl(Circular circular) {
        this.circular = circular;
    }
}
