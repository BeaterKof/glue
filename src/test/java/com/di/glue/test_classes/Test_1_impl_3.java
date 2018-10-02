package com.di.glue.test_classes;

import com.di.glue.context.annotation.Component;
import com.di.glue.context.annotation.Prototype;
import com.di.glue.context.annotation.Qualifier;

/**
 * created by: andrei
 * date: 01.10.2018
 **/
@Component
@Prototype
@Qualifier(name = "test1_impl3")
public class Test_1_impl_3 implements Test_1{
}
