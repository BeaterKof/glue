package com.di.glue.context;

import com.di.glue.context.data.*;
import com.di.glue.context.exception.DuplicateEntryException;
import com.di.glue.context.exception.NotASuperclassException;
import com.di.glue.context.factory.BeanFactory;
import com.di.glue.context.util.Validator;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Class used to contain all binding objects for SINGLETON and NAMED PROXY types.
 *
 */
public class DefaultBinder implements Binder {

    static final Logger log = Logger.getLogger(DefaultBinder.class);

    private MultiMap<TypeUnit, String, Object> beanMap;

    public DefaultBinder() {
        beanMap = new HashMultiMap<>();
    }

    //TODO: bind classes with inheritance
    @Override
    public void bind(TypeUnit typeUnit, String name, Class<?> impl) throws NotASuperclassException {
        if (!Validator.notNull(typeUnit, impl, typeUnit.getBeanType(), typeUnit.getClazz())
                && !typeUnit.getClazz().isInterface() && impl.isInterface() )
            throw new IllegalArgumentException();
        if(!impl.getClass().isAssignableFrom(typeUnit.getClazz().getClass()))
            throw new NotASuperclassException(impl, typeUnit.getClazz());

        try {
            if (typeUnit.getBeanType().equals(BeanType.SINGLETON)) {
                if (beanMap.containsKey(typeUnit))
                    throw new DuplicateEntryException(typeUnit.getClazz(), impl);
                else {
                    beanMap.put(typeUnit, null, BeanFactory.createInstance(impl));
                }
            } else if (typeUnit.getBeanType().equals(BeanType.PROTOTYPE)){
                beanMap.put(typeUnit, name, BeanFactory.createInstance(impl));
            }

        } catch (IllegalAccessException|InstantiationException e) {
            e.printStackTrace();
        } catch (DuplicateEntryException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public List<MultiMapEntry<TypeUnit, String, Object>> getBindings() {
        return beanMap.getEntries();
    }
}
