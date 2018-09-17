package com.di.glue;

import com.di.glue.context.GlueApplicationContext;
import com.di.glue.context.data.BindingConfigurer;
import com.di.glue.context.data.DefaultBindingConfigurer;
import com.di.glue.context.data.Scope;
import com.di.glue.test_classes.*;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GlueApplicationTest {

    Logger log = Logger.getLogger(GlueApplicationTest.class);
    GlueApplicationContext appContext;
    BindingConfigurer bindingConfigurer;

    @Before
    public void init() {
        bindingConfigurer = new DefaultBindingConfigurer();
        //        bindingConfigurer.addBinding(Simple.class, SimpleImpl_Singleton.class, null);
        bindingConfigurer.addBinding(Simple.class, SimpleImpl_Singleton.class, null, null);
        bindingConfigurer.addBinding(Simple.class, SimpleImpl_Singleton.class, null, null);
        bindingConfigurer.addBinding(Simple.class, SimpleImpl_1.class, null, null);
        bindingConfigurer.addBinding(Simple.class, SimpleImpl_2.class, null, null);
        bindingConfigurer.addBinding(Example.class, ExampleImpl_1.class, "Exaple_prototype_1", Scope.PROTOTYPE);
        bindingConfigurer.addBinding(Example.class, ExampleImpl_1.class, "Exaple_prototype_2", Scope.PROTOTYPE);
        bindingConfigurer.addBinding(Example.class, ExampleImpl_1.class, "Exaple_prototype_3", Scope.PROTOTYPE);
    }

    @Test
    public void bindingConfigurerTest() {

        // add PROTOTYPE instance of a type already binded to a SINGLETON instance.
        bindingConfigurer.addBinding(Simple.class, SimpleImpl_Singleton.class, "Simple_prototype_1", Scope.PROTOTYPE);

        appContext = new GlueApplicationContext(bindingConfigurer);
        appContext.logBindings();

        System.out.println();
        System.out.println(appContext.getBean(Simple.class));
        System.out.println(appContext.getBean(Simple.class));

    }

    /*
    Tests:
    1. Simple classes
    - bind singleton twice, bind prototype twice, bind singleton and prototype to same interface
    - get bindings twice for each and test if they are the same

    2. Complex classes
    - bind classes with one dependency that can be created via a new
    - bind classes with one dep that is marked with annot to a singleton
    - bind classes with one dep that is marked with annot to a prototype
    - bind classes with multiple dep that is marked with annot to a singleton/prot and first lever is also annot

    3. Special cases
    - bind lists
    - bind circular dependency
     */
}
