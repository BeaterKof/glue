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
public class Circular_lvl2_impl implements Circular_lvl2{

    Circular_lvl3 circular_lvl3;

    @Inject
    public Circular_lvl2_impl(Circular_lvl3 circular_lvl3) {
        this.circular_lvl3 = circular_lvl3;
    }
}
