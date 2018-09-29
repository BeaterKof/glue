package com.di.glue.test_classes;

import com.di.glue.context.annotation.GlueBean;
import com.di.glue.context.annotation.Inject;

/**
 * created by: andrei
 * date: 25.09.2018
 **/
@GlueBean
public interface Test_3 {
    Test_1 getInternalComponent();
}
