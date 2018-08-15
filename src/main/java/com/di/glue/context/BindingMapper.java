package com.di.glue.context;

import com.di.glue.context.data.*;
import com.di.glue.context.exception.DuplicateEntryException;
import org.apache.log4j.Logger;

/**
 * Will hold all the mappings ment for @Prototype classes.
 * Its purpose is to be used by the factories in order to generate new objects.
 *
 */

public class BindingMapper {

    final static Logger log = Logger.getLogger(BindingMapper.class);

    /**
     * Maps a Class (Prototype) to a map of
     * class names and their corresponding class.
     */
    private MultiMap<TypeUnit, String, Class<?>> map;

    public BindingMapper() {
        map = new HashMultiMap<TypeUnit, String, Class<?>>();
    }

    private void addBinding(TypeUnit typeUnit, String name, Class<?> clazz) {
        try {
            map.put(typeUnit, name, clazz);
        } catch (DuplicateEntryException e) {
            log.error(e.getMessage());
        }
    }

}
