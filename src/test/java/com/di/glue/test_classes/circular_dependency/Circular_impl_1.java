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
public class Circular_impl_1 implements Circular {

    Circular_lvl2 circular_lvl2;

    @Inject
    public Circular_impl_1(Circular_lvl2 circular_lvl2 ) {
        this.circular_lvl2 = circular_lvl2;
    }
}
