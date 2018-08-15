package com.di.glue.context.data;

import com.di.glue.context.exception.DuplicateEntryException;

import java.util.List;

public interface MultiMap<K, C, V> {

    /**
     * Method used to insert a value into the MultiMap.
     *
     * In case the key already exists, the subKey is checked.
     * If the subKey already exists an {@Link DuplicateEntryException} will be thrown
     *
     * @param key
     * @param subKey
     * @param value
     * @throws DuplicateEntryException
     */
    void put(K key, C subKey, V value);

    void removeKeyset(K key);

    boolean removeValue(K key, V value);

    boolean containsKey(K key);

    List<MultiMapEntry<K, C, V>> getEntries();
}
