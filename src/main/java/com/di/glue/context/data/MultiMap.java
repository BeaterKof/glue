package com.di.glue.context.data;

import com.di.glue.context.exception.DuplicateEntryException;
import com.di.glue.context.exception.NoSuchKeyException;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
    void put(K key, C subKey, V value) throws DuplicateEntryException;

    Map<C, V> getSubmap(K key);

    V getValue(K key, C subKey) throws NoSuchKeyException;

    void removeKey(K key);

    void removeSubKey(K key, C subKey);

    boolean containsKey(K key);

    List<MultiMapEntry<K, C, V>> getEntries();

    Set<MultiMapEntry<K, C, V>> entrySet();
}
