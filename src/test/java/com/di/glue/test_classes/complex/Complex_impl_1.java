package com.di.glue.test_classes.complex;

import com.di.glue.context.annotation.Component;
import com.di.glue.context.annotation.Inject;
import com.di.glue.context.annotation.Prototype;
import com.di.glue.context.annotation.Singleton;
import com.di.glue.test_classes.complex.simple.Test_1;
import com.di.glue.test_classes.complex.simple.Test_2;

/**
 * created by: andrei
 * date: 05.10.2018
 **/
@Component
@Singleton
public class Complex_impl_1 implements Complex {

    @Inject
    @Singleton
    private Test_1 test_1;

    @Inject
    @Prototype
    private Test_2 test_2;

    public Complex_impl_1(Test_1 test_1, Test_2 test_2) {
        this.test_1 = test_1;
        this.test_2 = test_2;
    }


}
