package com.di.glue.test_classes;

import com.di.glue.context.annotation.Component;
import com.di.glue.context.annotation.Inject;
import com.di.glue.context.annotation.Qualifier;
import com.di.glue.context.annotation.Singleton;

/**
 * created by: andrei
 * date: 25.09.2018
 **/
@Component
@Singleton
@Qualifier(name="Test3_impl3_sing")
public class Test3_impl3 implements Test_3 {

    @Inject
    Test_1 test_1;

    public Test3_impl3() {}

    public Test3_impl3(Test_1 test_1) {
        this.test_1 = test_1;
    }

    public Test_1 getInternalComponent() {
        return test_1;
    }
}
