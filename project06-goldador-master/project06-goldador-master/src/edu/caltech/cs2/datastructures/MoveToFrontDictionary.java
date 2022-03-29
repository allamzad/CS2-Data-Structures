package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDictionary;

import java.util.Iterator;

public class MoveToFrontDictionary<K, V> implements IDictionary<K,V> {
    private int size;
    private Node<K, V> head;

    public MoveToFrontDictionary() {
        this.size = 0;
        this.head = null;
    }

    private static class Node<K, V> {
        public final K key;
        public V value;
        public Node<K, V> next;

        public Node(K key, V value) {
            this(key, value, null);
        }

        public Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    @Override
    public V remove(K key) {
        if (this.head == null) {
            return null;
        }
        else if (this.head.key.equals(key)) {
            V value = this.head.value;
            if(this.size > 1) {
                this.head = this.head.next;
            }
            else if (this.size == 1){
                this.head = null;
            }
            this.size--;
            return value;
        }
        else {
            Node<K, V> start = this.head;
            V value = null;
            while (start.next != null) {
                if (start.next.key.equals(key)) {
                    value = start.next.value;
                    start.next = start.next.next;
                    this.size--;
                    return value;
                }
                start = start.next;
            }
            return value;
        }
    }

    @Override
    public V put(K key, V value) {
        V oldValue = null;
        if (this.head == null) {
            this.head = new Node<>(key, value);
            this.size++;
        }
        else {
            if (this.head.key.equals(key)) {
                oldValue = this.head.value;
                this.head.value = value;
                return oldValue;
            }
            else if (!this.containsKey(key)) {
                oldValue = null;
                Node<K, V> other = this.head;
                Node<K, V> newNode = new Node<>(key, value);
                newNode.next = other;
                this.head = newNode;
                this.size++;
            }
            else{
                this.remove(key);
                this.put(key, value);
            }
        }
        return oldValue;
    }

    @Override
    public boolean containsKey(K key) {
        return this.get(key) != null;
    }

    @Override
    public boolean containsValue(V value) {
        return this.values() != null && this.values().contains(value);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public ICollection<K> keys() {
        if(this.head == null){
            return null;
        }
        else{
            LinkedDeque<K> keys = new LinkedDeque<>();
            Node<K, V> start = this.head;
            while (start != null) {
                keys.addBack(start.key);
                start = start.next;
            }
            return keys;
        }
    }

    @Override
    public ICollection<V> values() {
        if(this.head == null){
            return null;
        }
        else{
            LinkedDeque<V> values = new LinkedDeque<>();
            Node<K, V> start = this.head;
            while (start != null) {
                values.add(start.value);
                start = start.next;
            }
            return values;
        }
    }

    public V get(K key) {
        V value = this.remove(key);
        if(value != null) {
            Node<K, V> other = this.head;
            Node<K, V> newNode = new Node<>(key, value);
            newNode.next = other;
            this.head = newNode;
            this.size++;
        }
        return value;
    }

    @Override
    public Iterator<K> iterator() {
        return new MoveToFrontIterator();
    }

    private class MoveToFrontIterator implements Iterator<K> {

        private int currentIndex;
        private Node<K, V> curr;

        public MoveToFrontIterator() {
            this.currentIndex = 0;
            curr = MoveToFrontDictionary.this.head;
        }

        public boolean hasNext() {
            return this.currentIndex < (MoveToFrontDictionary.this).size();
        }

        public K next() {
            if(this.currentIndex == 0){
                this.currentIndex++;
                return MoveToFrontDictionary.this.head.key;
            }
            curr = curr.next;
            K element = curr.key;
            this.currentIndex++;
            return element;
        }
    }

}
