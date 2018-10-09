package com.di.glue;

import com.di.glue.context.GlueApplicationContext;
import com.di.glue.context.data.BindingConfigurer;
import com.di.glue.context.data.DefaultBindingConfigurer;
import com.di.glue.context.data.Scope;
import com.di.glue.test_classes.*;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
    }

    @Test(expected = IllegalArgumentException.class)
    public void basicTest_1() {
        bindingConfigurer.addBinding(null, null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void basicTest_2() {
        bindingConfigurer.addBinding(null, null, null, null);
    }

    @Test
    public void simpleClassesTest() {
        //two singletons of same interface
        bindingConfigurer.addBinding(Simple.class, SimpleImpl_1.class, null, null);
        bindingConfigurer.addBinding(Simple.class, SimpleImpl_1.class, null, Scope.SINGLETON);
        //two prototypes of same interface
        bindingConfigurer.addBinding(Example.class, ExampleImpl_1.class, null, Scope.PROTOTYPE);
        bindingConfigurer.addBinding(Example.class, ExampleImpl_1.class, null, Scope.PROTOTYPE);
        // add PROTOTYPE instance of a type already binded to a SINGLETON instance and vice versa, with qualifier
        bindingConfigurer.addBinding(Simple.class, SimpleImpl_1.class, "Simple_prototype_1", Scope.PROTOTYPE);
        bindingConfigurer.addBinding(Example.class, ExampleImpl_1.class, "Example_singleton_1", Scope.PROTOTYPE);

        appContext = new GlueApplicationContext(bindingConfigurer, true);
        appContext.logBindings();

        Object simple_1 = appContext.getBean(Simple.class);
        Object simple_2 = appContext.getBean(Simple.class);
        Assert.assertEquals(simple_1, simple_2);

        Object example_1 = appContext.getBean(Example.class, Scope.PROTOTYPE, null);
        Object example_2 = appContext.getBean(Example.class, Scope.PROTOTYPE, null);
        Assert.assertNotEquals(example_1, example_2);

        Object simple_3 = appContext.getBean(Simple.class, Scope.PROTOTYPE, "Simple_prototype_1");
        Object simple_4 = appContext.getBean(Simple.class, Scope.PROTOTYPE, "Simple_prototype_1");
        Assert.assertNotEquals(simple_3, simple_4);

        Object example_3 = appContext.getBean(Example.class, Scope.SINGLETON, "Example_singleton_1");
        Object example_4 = appContext.getBean(Example.class, Scope.SINGLETON, "Example_singleton_2");
        Assert.assertEquals(example_3, example_4);
    }

    @Test
    public void getIsNotBindedTest() {
        appContext = new GlueApplicationContext(bindingConfigurer, true);
        appContext.logBindings();

        Assert.assertNotNull(appContext.getBean(ExampleNotBinded.class));
    }

    @Test
    public void complexClassesTest_1() {

        appContext = new GlueApplicationContext(bindingConfigurer, true);
        appContext.logBindings();

        //test multiple inheritance singletons
        Test_1 test_1 = (Test_1) appContext.getBean(Test_1.class);
        Test_3 test_3 = (Test_3) appContext.getBean(Test_3.class, Scope.SINGLETON, "Test3_impl3_sing");
        Assert.assertNotNull(test_3);
        Assert.assertEquals(test_3.getInternalComponent(), test_1);

        //test prototypes
        Test_2 test_2 = (Test_2) appContext.getBean(Test_2.class, Scope.PROTOTYPE);
        Test_2 test_2_2 = (Test_2) appContext.getBean(Test_2.class, Scope.PROTOTYPE);
        Assert.assertNotEquals(test_2, test_2_2);

    }

    @Test
    public void specialCasesTest_Lists() {

        appContext = new GlueApplicationContext(bindingConfigurer, true);
        appContext.logBindings();

        ListTest_1 result = (ListTest_1) appContext.getBean(ListTest_1.class);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getList().size(), 3);

    }

    @Test
    public void specialCasesTest_SingProtFields() {

        appContext = new GlueApplicationContext(bindingConfigurer, true);
        appContext.logBindings();

        Complex result = (Complex) appContext.getBean(Complex.class, Scope.PROTOTYPE);
        Assert.assertNotNull(result);

    }

    /*
    Tests:
    0. Basic test
    - bind null values
    - bind interf to null / impl to null
    - get bind of class that is not binded

    1. Simple classes
    - bind singleton twice, bind prototype twice, bind singleton and prototype to same interface
    - get bindings twice for each and test if they are the same
    - bind singleton and prototype by qualifier

    2. Complex classes
    - bind classes with one dependency that can be created via a new
    - bind classes with one dep that is marked with annot to a singleton
    - bind classes with one dep that is marked with annot to a prototype
    - bind classes with multiple dep that is marked with annot to a singleton/prot and first lever is also annot

    3. Special cases
    - bind lists
    - inject singleton/prototype and by qualifier
    - bind circular dependency
     */
}
