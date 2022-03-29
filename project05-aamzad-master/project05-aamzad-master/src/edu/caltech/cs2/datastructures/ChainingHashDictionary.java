package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDictionary;

import java.util.Iterator;
import java.util.function.Supplier;

public class ChainingHashDictionary<K, V> implements IDictionary<K, V> {
    private Supplier<IDictionary<K, V>> chain;
    private int size;
    private int capacity = 11;
    private IDictionary<K, V>[] array;

    public ChainingHashDictionary(Supplier<IDictionary<K, V>> chain) {
        this.array = new IDictionary[capacity];
        for(int i = 0; i < this.capacity; i++){
            this.array[i] = chain.get();
        }
        this.chain = chain;
    }

    /**
     * @param key
     * @return value corresponding to key
     */
    @Override
    public V get(K key) {
        int hashcode = getHash(key);
            if (this.array[hashcode] == null) {
                return null;
            }
            return this.array[hashcode].get(key);
    }

    @Override
    public V remove(K key) {
        V value = null;
        int hashcode = getHash(key);
        if(this.containsKey(key)){
            value = this.array[hashcode].remove(key);
            this.size--;
        }
        return value;
    }

    @Override
    public V put(K key, V value) {
        rehash();
        int hashcode = getHash(key);
            V oldValue = this.get(key);
            if (oldValue == null) {
                this.size++;
            }
            if(value != oldValue) {
                this.array[hashcode].put(key, value);
            }
            return oldValue;
    }

    private int getPrime(int k) {
        int start = 2 * k + 1;
        while (true) {
            boolean isPrime = true;
            for (int i = 2; i < start; i++) {
                if (start % i == 0) {
                    isPrime = false;
                    break;
                }
            }
            if (isPrime) {
                return start;
            }
            start++;
        }
    }

    private void rehash (){
        if (this.size / (this.capacity * 1.0) > 1) {
            IDictionary<K, V>[] oldArray = this.array;
            this.capacity = getPrime(this.capacity);
            this.array = new IDictionary[this.capacity];
            for(int i = 0; i < this.capacity; i++){
                this.array[i] = chain.get();
            }
            for (IDictionary<K, V> ks : oldArray) {
                if(ks != null) {
                    for (K k : ks) {
                        int hashcode = getHash(k);
                        V oldValue = ks.get(k);
                        this.array[hashcode].put(k, oldValue);
                    }
                }
            }
        }
    }

    private int getHash(K key){
        int hashcode = key.hashCode();
        if (hashcode >= 0) {
            hashcode = hashcode % this.capacity;
        }
        else {
            hashcode = (hashcode) % this.capacity;
            if(hashcode < 0){
                hashcode += this.capacity;
            }
        }
        return hashcode;
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
        return this.values() != null && this.values().contains(value);
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
            int counter = 0;
            LinkedDeque<K> keys = new LinkedDeque<>();
            while (counter < this.capacity) {
                while (counter < this.capacity && this.array[counter] == null) {
                    counter++;
                }
                if (counter < this.capacity) {
                    for (K k : this.array[counter]) {
                        keys.addBack(k);
                    }
                }
                counter++;
            }
            return keys;
    }

    @Override
    public ICollection<V> values() {
        int counter = 0;
        LinkedDeque<V> values = new LinkedDeque<>();
        while(counter < this.capacity) {
            while (counter < this.capacity && this.array[counter] == null) {
                counter++;
            }
            if(counter < this.capacity) {
                for (K k : this.array[counter]) {
                    values.addBack(this.get(k));
                }
            }
            counter++;
        }
        return values;
    }

    /**
     * @return An iterator for all entries in the HashDictionary
     */
    @Override
    public Iterator<K> iterator() {
        return new ChainingHashDictionaryIterator();
    }

    private class ChainingHashDictionaryIterator implements Iterator<K> {
        private int chainIndex;
        private int currentIndex;

        public ChainingHashDictionaryIterator() {
            this.chainIndex = 0;
            this.currentIndex = 0;
        }

        public boolean hasNext() {
            if(this.currentIndex >= (ChainingHashDictionary.this).capacity){
                return false;
            }
            while(ChainingHashDictionary.this.array[this.currentIndex].size() == 0) {
                this.currentIndex++;
                if(this.currentIndex >= (ChainingHashDictionary.this).capacity){
                    return false;
                }
            }
            if(this.currentIndex >= (ChainingHashDictionary.this).capacity){
                return false;
            }
            if(this.chainIndex < ChainingHashDictionary.this.array[this.currentIndex].size()){
                return true;
            }
            return this.currentIndex < (ChainingHashDictionary.this).capacity;
        }

        public K next() {
                int counter = 0;
                for (K key : ChainingHashDictionary.this.array[this.currentIndex]) {
                    if (counter == this.chainIndex) {
                        this.chainIndex++;
                        if (this.chainIndex == ChainingHashDictionary.this.array[this.currentIndex].size()) {
                            this.currentIndex++;
                            this.chainIndex = 0;
                        }
                        return key;
                    }
                    counter++;
            }
            return null;
        }
    }


}
