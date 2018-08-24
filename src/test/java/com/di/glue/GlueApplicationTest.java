package com.di.glue;

import com.di.glue.context.GlueApplicationContext;
import com.di.glue.context.data.BindingConfigurer;
import com.di.glue.context.data.DefaultBindingConfigurer;
import com.di.glue.context.data.Scope;
import com.di.glue.test_classes.*;
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
        bindingConfigurer.addBinding(Simple.class, SimpleImpl_Singleton.class, null, null);
        bindingConfigurer.addBinding(Simple.class, SimpleImpl_Singleton.class, null, null);
        bindingConfigurer.addBinding(Simple.class, SimpleImpl_1.class, null, null);
        bindingConfigurer.addBinding(Simple.class, SimpleImpl_2.class, null, null);
        bindingConfigurer.addBinding(Example.class, ExampleImpl_1.class, "Exaple_prototype_1", Scope.PROTOTYPE);
        bindingConfigurer.addBinding(Example.class, ExampleImpl_1.class, "Exaple_prototype_2", Scope.PROTOTYPE);
        bindingConfigurer.addBinding(Example.class, ExampleImpl_1.class, "Exaple_prototype_3", Scope.PROTOTYPE);

        // add PROTOTYPE instance of a type already binded to a SINGLETON instance.
        bindingConfigurer.addBinding(Simple.class, SimpleImpl_Singleton.class, "Simple_prototype_1", Scope.PROTOTYPE);

        appContext = new GlueApplicationContext(bindingConfigurer);
        appContext.logBindings();
    }
}
