public interface Cache<K, V> {
    /**
     * Get the value for a key. Returns null if the key is not
     * in the cache.
     *
     * @param key the key
     */
    V get(K key);
    /**
     1
     * Put a new key value pair in the cache
     *
     * @param key the key
     * @param value the value
     */
    void put(K key, V value);
}