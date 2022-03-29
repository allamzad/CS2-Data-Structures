package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IDictionary;

import java.util.Iterator;

public class MoveToFrontDictionary<K, V> implements IDictionary<K,V> {
    private Node<K, V> root;
    private int size;

    private static class Node<K, V> {
        public K key;
        public V data;
        public Node<K, V> next;

        public Node(K key, V data) {
            this(key, data, null);
        }

        public Node(K key, V data, Node<K, V> next) {
            this.key = key;
            this.data = data;
            this.next = next;
        }
    }

    public MoveToFrontDictionary() {
        this.root = null;
        this.size = 0;
    }

    @Override
    public V remove(K key) {
        if (!this.keys().contains(key)) {
            return null;
        }
        V removedValue = getNode(key).data;
        moveToFront(key);
        this.root = this.root.next;
        size--;
        return removedValue;
    }

    @Override
    public V put(K key, V value) {
        if (size == 0) {
            this.root = new Node<>(key, value);
            this.size++;
            return null;
        }
        if (this.containsKey(key)) {
            V replacedValue = getNode(key).data;
            getNode(key).data = value;
            return replacedValue;
        }
        this.root = new Node<>(key, value, this.root);
        this.size++;
        return null;
    }

    @Override
    public boolean containsKey(K key) {
        return this.get(key) != null;
    }

    @Override
    public boolean containsValue(V value) {
        return this.values().contains(value);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public ICollection<K> keys() {
        IDeque<K> keys = new ArrayDeque<>();
        for (K key : this) {
            keys.addBack(key);
        }
        return keys;
    }

    @Override
    public ICollection<V> values() {
        IDeque<V> values = new ArrayDeque<>();
        Node<K, V> currentNode = this.root;
        if (currentNode == null) {
            return values;
        }
        values.addBack(currentNode.data);
        while (currentNode.next != null) {
            currentNode = currentNode.next;
            values.addBack(currentNode.data);
        }
        return values;
    }

    public V get(K key) {
        boolean move = moveToFront(key);
        if (!move) {
            return null;
        }
        return this.root.data;
    }

    private Node<K, V> getNode(K key) {
        Node<K, V> currentNode = this.root;
        while (!currentNode.key.equals(key)) {
            currentNode = currentNode.next;
        }
        return currentNode;
    }

    private boolean moveToFront(K key) {
        Iterator<K> iter = this.iterator();
        K currentKey;
        K prevKey = null;
        while (iter.hasNext()) {
            currentKey = iter.next();
            if (!currentKey.equals(key)) {
                prevKey = currentKey;
            } else {
                if (prevKey == null) {
                    return true;
                }
                Node<K, V> movedNode = this.getNode(key);
                Node<K, V> prevNode = this.getNode(prevKey);
                if (movedNode.next != null) {
                    prevNode.next = movedNode.next;
                } else {
                    prevNode.next = null;
                }
                this.root = new Node<>(movedNode.key, movedNode.data, this.root);
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<K> iterator() {
        return new Iterator<>() {
            Node<K, V> currentNode = root;

            public boolean hasNext() {
                return currentNode != null;
            }

            public K next() {
                K currentKey = currentNode.key;
                currentNode = currentNode.next;
                return currentKey;
            }
        };
    }
}
