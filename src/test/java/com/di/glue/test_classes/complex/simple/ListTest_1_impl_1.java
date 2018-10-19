package com.di.glue.test_classes.complex.simple;

import com.di.glue.context.annotation.Component;
import com.di.glue.context.annotation.Inject;
import com.di.glue.context.annotation.Singleton;

import java.util.List;

/**
 * created by: andrei
 * date: 01.10.2018
 **/
@Component
@Singleton
public class ListTest_1_impl_1 implements ListTest_1 {

    @Inject
    List<Test_1> list;

    @Inject
    public ListTest_1_impl_1(List<Test_1> list) {
        this.list = list;
    }

    public List<Test_1> getList() {
        return list;
    }

    public void setList(List<Test_1> list) {
        this.list = list;
    }
}
