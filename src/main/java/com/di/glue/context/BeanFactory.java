package com.di.glue.context;

import com.di.glue.context.data.MultiMapEntry;
import com.di.glue.context.data.Scope;
import com.di.glue.context.data.bean.BindIdentifier;
import com.di.glue.context.data.bean.ImplUnit;

import java.util.List;

/**
 * created by: andrei
 * date: 28.12.2018
 **/
public interface BeanFactory {

    /**
     * This method must be called after all the binds have been added
     * creates all singleton instances and adds them into the beanMap
     */
    void init();

    /**
     * Returns a bean for the given class.
     * @param clazz
     * @return
     */
    Object getBean(Class<?> clazz);

    /**
     * Returns a bean for the given class and scope.
     * @param clazz
     * @param scope
     * @return
     */
    Object getBean(Class<?> clazz, Scope scope);

    /**
     * Returns a bean for the given class, scope and qualifier.
     * @param clazz
     * @param scope
     * @param qualifier
     * @return
     */
    Object getBean(Class<?> clazz, Scope scope, String qualifier);

    /**
     * Binds a class to a implementation.
     * @param abstr
     * @param impl
     * @param scope
     * @param qualifier
     */
    void bind(Class<?> abstr, Class<?> impl, Scope scope, String qualifier);

    /**
     * Returns a list of binding entries.
     * @return
     */
    List<MultiMapEntry<Class<?>, BindIdentifier, ImplUnit>> getBindings();
}
