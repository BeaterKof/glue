package com.di.glue.context.data;

public class MultiMapEntry<K, C, V> {

    private K key;
    private C subKey;
    private V value;

    public MultiMapEntry(K key, C subKey, V value) {
        this.key = key;
        this.subKey = subKey;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public C getSubKey() {
        return subKey;
    }

    public void setSubKey(C subKey) {
        this.subKey = subKey;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
