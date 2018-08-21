package com.di.glue;

import com.di.glue.context.GlueApplicationContext;
import com.di.glue.context.data.BindingConfigurer;
import com.di.glue.context.data.DefaultBindingConfigurer;
import com.di.glue.test_classes.Example;
import com.di.glue.test_classes.ExampleImpl_1;
import com.di.glue.test_classes.Simple;
import com.di.glue.test_classes.SimpleImpl_Singleton;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GlueApplicationTest {


    GlueApplicationContext appContext;

    @Test
    public void bindingConfigurerTest() {
        BindingConfigurer bindingConfigurer = new DefaultBindingConfigurer();
//        bindingConfigurer.addBinding(Simple.class, SimpleImpl_Singleton.class, null);
        bindingConfigurer.addBinding(Simple.class, SimpleImpl_Singleton.class, null);
        bindingConfigurer.addBinding(Example.class, ExampleImpl_1.class, "Exaple_prototype_1");
        bindingConfigurer.addBinding(Example.class, ExampleImpl_1.class, "Exaple_prototype_2");
        bindingConfigurer.addBinding(Example.class, ExampleImpl_1.class, "Exaple_prototype_3");

        // add PROTOTYPE instance of a type already binded to a SINGLETON instance.
        bindingConfigurer.addBinding(Simple.class, SimpleImpl_Singleton.class, "Simple_prototype_1");

        appContext = new GlueApplicationContext(bindingConfigurer);
        appContext.printBindings();
    }
}
