package com.di.glue.test_classes.complex;

import com.di.glue.context.annotation.*;
import com.di.glue.test_classes.complex.simple.Test_1;
import com.di.glue.test_classes.complex.simple.Test_2;
import com.di.glue.test_classes.complex.simple.Test_3;

/**
 * created by: andrei
 * date: 05.10.2018
 **/
@Component
@Prototype
public class Complex_impl_2 implements Complex {

    private Test_1 test_1;

    private Test_2 test_2;

    private Test_3 test_3;

    @Inject
    public Complex_impl_2(@Singleton Test_1 test_1, @Prototype Test_2 test_2, @Singleton @Qualifier(name = "Test3_impl3_sing")Test_3 test_3) {
        this.test_1 = test_1;
        this.test_2 = test_2;
        this.test_3 = test_3;
    }

    public Test_1 getTest_1() {
        return test_1;
    }

    public void setTest_1(Test_1 test_1) {
        this.test_1 = test_1;
    }

    public Test_2 getTest_2() {
        return test_2;
    }

    public void setTest_2(Test_2 test_2) {
        this.test_2 = test_2;
    }

    public Test_3 getTest_3() {
        return test_3;
    }

    public void setTest_3(Test_3 test_3) {
        this.test_3 = test_3;
    }
}
