package muc;

import java.util.HashMap;

import domain.DomainObject;

/**
 * Represents wrapper around {@link HashMap} with {@link String} keys
 * 
 * @author tillias
 * 
 * @param <T>
 *            the type of values maintained by this string hash map
 */
public class StringHashMap<T extends DomainObject> {

    /**
     * Default capacity of this hash map
     */
    public static final int DEFAULT_CAPACITY = 128;

    /**
     * Creates new instance of hash map using {@link #DEFAULT_CAPACITY}
     */
    public StringHashMap() {
	map = new HashMap<String, T>(DEFAULT_CAPACITY);
    }

    /**
     * Associates the specified value with the specified key in this map. If the
     * map previously contained a mapping for the key, the old value is
     * replaced.
     * 
     * @param key
     *            key with which the specified value is to be associated
     * @param value
     *            value to be associated with the specified key
     */
    public void put(String key, T value) {
	map.put(key, value);
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     * 
     * @param key
     *            the key whose associated value is to be returned
     * 
     * @return the value to which the specified key is mapped, or null if this
     *         map contains no mapping for the key
     */
    public T get(String key) {
	return map.get(key);
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     * 
     * 
     * @param key
     *            key whose mapping is to be removed from the map
     * @return the previous value associated with key, or null if there was no
     *         mapping for key. (A null return can also indicate that the map
     *         previously associated null with key.)
     */
    public T remove(String key) {
	return map.remove(key);
    }

    HashMap<String, T> map;
}
