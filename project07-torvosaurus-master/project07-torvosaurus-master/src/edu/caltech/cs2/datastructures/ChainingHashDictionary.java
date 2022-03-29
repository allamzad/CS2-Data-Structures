package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IDictionary;

import java.util.Iterator;
import java.util.function.Supplier;

public class ChainingHashDictionary<K, V> implements IDictionary<K, V> {
    private Supplier<IDictionary<K, V>> chain;
    private IDictionary<K, V>[] bucket;
    private int size;
    private final double LOAD_FACTOR = 1.0;

    public ChainingHashDictionary(Supplier<IDictionary<K, V>> chain) {
        this.chain = chain;
        this.bucket = new IDictionary[17];
        this.size = 0;
    }

    /**
     * @param key
     * @return value corresponding to key
     */
    @Override
    public V get(K key) {
        int hashCode = getHashcode(key);
        if (this.bucket[hashCode] == null) {
            return null;
        }
        if (this.bucket[hashCode].containsKey(key)) {
            return this.bucket[hashCode].get(key);
        }
        return null;
    }

    @Override
    public V remove(K key) {
        int hashCode = getHashcode(key);
        if (this.bucket[hashCode] == null) {
            return null;
        }
        if (this.bucket[hashCode].containsKey(key)) {
            this.size--;
            V removedValue = this.bucket[hashCode].get(key);
            this.bucket[hashCode].remove(key);
            return removedValue;
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        this.rehash();
        int hashCode = getHashcode(key);
        if (this.bucket[hashCode] == null) {
            this.size++;
            IDictionary<K, V> newEntry = this.chain.get();
            newEntry.put(key, value);
            this.bucket[hashCode] = newEntry;
            return null;
        }
        if (this.bucket[hashCode].containsKey(key)) {
            V replacedValue = this.bucket[hashCode].remove(key);
            this.bucket[hashCode].put(key, value);
            return replacedValue;
        }
        this.size++;
        this.bucket[hashCode].put(key, value);
        return null;
    }

    private void rehash() {
        if ((double)this.size/ this.bucket.length >= LOAD_FACTOR) {
            IDictionary<K, V>[] newBucket = new IDictionary[newSize(this.bucket.length)];
            IDictionary<K, V>[] oldBucket = this.bucket;
            this.bucket = newBucket;
            this.size = 0;
            for (IDictionary<K, V> entry : oldBucket) {
                if (entry != null) {
                    for (K key : entry.keys()) {
                        this.put(key, entry.get(key));
                    }
                }
            }
        }
    }

    private int getHashcode(K key) {
        int hashCode = key.hashCode() % bucket.length;
        if (hashCode < 0) {
            hashCode += this.bucket.length;
        }
        return hashCode;
    }

    private static int newSize(int size) {
        size *= 2;
        for (int i = 2; i < size; i++) {
            if (size % i == 0) {
                size++;
                i = 2;
            }
        }
        return size;
    }

    @Override
    public boolean containsKey(K key) {
        return this.get(key) != null;
    }

    /**
     * @param value
     * @return true if the HashDictionary contains a key-value pair with
     * this value, and false otherwise
     */
    @Override
    public boolean containsValue(V value) {
        return this.values().contains(value);
    }

    /**
     * @return number of key-value pairs in the HashDictionary
     */
    @Override
    public int size() {
        return this.size;
    }

    @Override
    public ICollection<K> keys() {
        IDeque<K> keys = new ArrayDeque<>();
        for (IDictionary<K, V> entry : this.bucket) {
            if (entry != null) {
                for (K key : entry.keys()) {
                    keys.addBack(key);
                }
            }
        }
        return keys;
    }

    @Override
    public ICollection<V> values() {
        IDeque<V> values = new ArrayDeque<>();
        for (IDictionary<K, V> entry : this.bucket) {
            if (entry != null) {
                for (V value : entry.values()) {
                    values.addBack(value);
                }
            }
        }
        return values;
    }

    /**
     * @return An iterator for all entries in the HashDictionary
     */
    @Override
    public Iterator<K> iterator() {
        return keys().iterator();
    }
}
