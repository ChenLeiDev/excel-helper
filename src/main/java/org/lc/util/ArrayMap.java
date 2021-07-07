package org.lc.util;


import org.apache.commons.collections4.keyvalue.DefaultMapEntry;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ArrayMap<K, V> implements Map<K, V>, Serializable, Cloneable {

    private static final int DEFAULT_INITIALIZA_SIZE = 32;
    private static final int GROW_MULTIPLE = 2;
    private int size = 0;
    private K[] keys;
    private V[] values;

    public ArrayMap(int initializaSize){
        keys = (K[])(new Object[initializaSize]);
        values = (V[])(new Object[initializaSize]);
    }

    public ArrayMap(){
        keys = (K[])(new Object[DEFAULT_INITIALIZA_SIZE]);
        values = (V[])(new Object[DEFAULT_INITIALIZA_SIZE]);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        if(size == 0){
            return true;
        }
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        for (int i = 0; i < keys.length; i++){
            if(keys[i] != null && keys[i].equals(key)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        for (int i = 0; i < values.length; i++){
            if(values[i] != null && values[i].equals(value)){
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(Object key) {
        for (int i = 0; i < keys.length; i++){
            if(keys[i] != null && keys[i].equals(key)){
                return values[i];
            }
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        key.equals(null);
        if(size == keys.length){
            K[] originKeys = keys;
            V[] originValues = values;
            keys = (K[])(new Object[originKeys.length * GROW_MULTIPLE]);
            values = (V[])(new Object[originValues.length * GROW_MULTIPLE]);
        }
        boolean putted = false;
        for (int i = 0; i < keys.length; i++){
            if(keys[i] != null && keys[i].equals(key)){
                values[i] = value;
                putted = true;
                break;
            }
        }
        if(!putted){
            for (int i = 0; i < keys.length; i++){
                if(keys[i] == null){
                    keys[i] = key;
                    values[i] = value;
                    break;
                }
            }
            size++;
        }
        return value;
    }

    @Override
    public V remove(Object key) {
        for (int i = 0; i < keys.length; i++){
            if(keys[i] != null && keys[i].equals(key)){
                keys[i] = null;
                V value = values[i];
                values[i] = null;
                size--;
                return value;
            }
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        Set<? extends Entry<? extends K, ? extends V>> entries = m.entrySet();
        for (Entry entry: entries) {
            K key = (K)entry.getKey();
            V value = (V)entry.getValue();
            put(key, value);
        }
    }

    @Override
    public void clear() {
        for (int i = 0; i < keys.length; i++){
            keys[i] = null;
            values[i] = null;
        }
    }

    @Override
    public Set<K> keySet() {
        return CollectionUtil.newHashSet(keys);
    }

    @Override
    public Collection<V> values() {
        return CollectionUtil.newArrayList(values);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        HashSet<Entry<K, V>> entries = new HashSet<>();
        for (int i = 0; i < keys.length; i++){
            if(keys[i] != null){
                DefaultMapEntry<K, V> entry = new DefaultMapEntry<>(keys[i], values[i]);
                entries.add(entry);
            }
        }
        return entries;
    }
}
