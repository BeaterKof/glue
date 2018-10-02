package com.di.glue.test_classes;

import com.di.glue.context.annotation.GlueBean;

import java.util.List;

/**
 * created by: andrei
 * date: 01.10.2018
 **/
@GlueBean
public interface ListTest_1 {

    List<Test_1> getList();
}
