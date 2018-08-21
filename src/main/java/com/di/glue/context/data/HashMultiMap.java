package com.di.glue.context.data;

import com.di.glue.context.exception.DuplicateEntryException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This implementation is a Map of K values that may each contain multiple C/V pairs, meaning that the submap
 * will always contain one value for a given key.
 * For the C/V pairs, the Map type was used in order to get a faster key search.
 *
 * @param <K>
 * @param <C>
 * @param <V>
 */
public class HashMultiMap<K, C, V> implements MultiMap<K, C, V> {

    private Map<K, Map<C, V>> map;

    public HashMultiMap() {
        map = new HashMap<>();
    }

    public HashMultiMap(HashMultiMap<K, C, V> hashMultiMap) {
        this.map = new HashMap<>(hashMultiMap.getMap());
    }

    public Map<K, Map<C, V>> getMap() {
        return map;
    }

    @Override
    public void put(K key, C subKey, V value) throws DuplicateEntryException {
        Map<C, V> subMap;
        if(map.containsKey(key)) {
            if(map.get(key).containsKey(subKey)) {
                throw new DuplicateEntryException(key, subKey, value);
            }
            subMap = map.get(key);
            subMap.put(subKey, value);
        } else {
            subMap = new HashMap<>();
            subMap.put(subKey, value);
            map.put(key, new HashMap<>(subMap));
        }
    }

    @Override
    public void removeKey(K key) {
        map.remove(key);
    }

    @Override
    public void removeSubKey(K key, C subKey) {
        map.get(key).remove(subKey);
    }

    @Override
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    @Override
    public List<MultiMapEntry<K, C, V>> getEntries() {
        List<MultiMapEntry<K, C, V>> list = new ArrayList<>();
        for(Map.Entry<K, Map<C, V>> mapEntry : map.entrySet()) {
            for(Map.Entry<C, V> subMapEntry : mapEntry.getValue().entrySet()) {
                list.add(new MultiMapEntry(mapEntry.getKey(), subMapEntry.getKey(), subMapEntry.getValue()));
            }
        }
        return list;
    }

    @Override
    public Set<MultiMapEntry<K, C, V>> entrySet() {
        Set<MultiMapEntry<K, C, V>> set = new HashSet<>();
        for(Map.Entry<K, Map<C, V>> mapEntry : map.entrySet()) {
            for(Map.Entry<C, V> subMapEntry : mapEntry.getValue().entrySet()) {
                set.add(new MultiMapEntry(mapEntry.getKey(), subMapEntry.getKey(), subMapEntry.getValue()));
            }
        }
        return set;
    }
}
