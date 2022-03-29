package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.ITrieMap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

public class TrieMap<A, K extends Iterable<A>, V> implements ITrieMap<A, K, V> {
    private TrieNode<A, V> root;
    private Function<IDeque<A>, K> collector;
    private int size;

    public TrieMap(Function<IDeque<A>, K> collector) {
        this.root = null;
        this.collector = collector;
        this.size = 0;
    }


    @Override
    public boolean isPrefix(K key) {
        Iterator<A> iter = key.iterator();
        TrieNode<A, V> currNode = this.root;
        if (currNode == null) {
            return false;
        }
        while (iter.hasNext()) {
            currNode = currNode.pointers.get(iter.next());
            if (currNode == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public IDeque<V> getCompletions(K prefix) {
        IDeque<V> values = new ArrayDeque<>();
        if (!isPrefix(prefix)) {
            return values;
        }
        Iterator<A> iter = prefix.iterator();
        TrieNode<A, V> currNode = this.root;
        while (iter.hasNext()) {
            currNode = currNode.pointers.get(iter.next());
        }
        IDeque<K> result = new ArrayDeque<>();
        IDeque<A> keys = new ArrayDeque<>();
        keysHelper(result, keys, currNode);
        if (currNode.value != null) {
            values.addBack(currNode.value);
        }
        for (K key : result) {
            TrieNode<A, V> tempNode = currNode;
            Iterator<A> keyIter = key.iterator();
            while (keyIter.hasNext()) {
                tempNode = tempNode.pointers.get(keyIter.next());
            }
            values.addBack(tempNode.value);
        }
        return values;
    }

    @Override
    public void clear() {
        this.root = null;
        this.size = 0;
    }

    @Override
    public V get(K key) {
        Iterator<A> iter = key.iterator();
        TrieNode<A, V> currNode = this.root;
        if (currNode == null) {
            return null;
        }
        while (iter.hasNext()) {
            currNode = currNode.pointers.get(iter.next());
            if (currNode == null) {
                return null;
            }
        }
        return currNode.value;
    }

    @Override
    public V remove(K key) {
        Iterator<A> iter = key.iterator();
        TrieNode<A, V> currNode = this.root;
        V removedValue;
        if (get(key) == null) {
            return null;
        }
        while (iter.hasNext()) {
            currNode = currNode.pointers.get(iter.next());
        }
        removedValue = currNode.value;
        currNode.value = null;
        prune(this.root);
        if (this.root.pointers.isEmpty() && this.root.value == null) {
            clear();
        }
        this.size = values().size();
        return removedValue;
    }

    private boolean prune(TrieNode<A, V> node) {
        IDeque<Boolean> hasValue = new ArrayDeque<>();
        if (node.pointers.isEmpty()) {
            return node.value != null;
        }
        Iterator<Map.Entry<A, TrieNode<A, V>>> iter = node.pointers.entrySet().iterator();
        IDeque<A> toRemove = new ArrayDeque<>();
        while (iter.hasNext()) {
            Map.Entry<A, TrieNode<A, V>> entry = iter.next();
            TrieNode<A, V> nextNode = entry.getValue();
            hasValue.addBack(prune(nextNode));
            if (nextNode.pointers.isEmpty() && nextNode.value == null) {
                toRemove.addBack(entry.getKey());
            }
        }
        if (!hasValue.contains(true)) {
            iter.remove();
            return node.value != null;
        }
        for (A removeNode : toRemove) {
            node.pointers.remove(removeNode);
        }
        return true;
    }

    @Override
    public V put(K key, V value) {
        V originalValue = get(key);
        Iterator<A> iter = key.iterator();
        if (this.root == null) {
            this.root = new TrieNode<>();
        }
        TrieNode<A, V> currNode = this.root;
        while (iter.hasNext()) {
            A currKey = iter.next();
            if (currNode.pointers.get(currKey) == null) {
                currNode.pointers.put(currKey, new TrieNode<>());
            }
            currNode = currNode.pointers.get(currKey);
        }
        if (currNode.value == null) {
            this.size++;
        }
        currNode.value = value;
        return originalValue;
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(V value) {
        return values().contains(value);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public IDeque<K> keys() {
        IDeque<K> result = new ArrayDeque<>();
        IDeque<A> keys = new ArrayDeque<>();
        if (this.root != null) {
            keysHelper(result, keys, this.root);
        }
        return result;
    }

    private void keysHelper(IDeque<K> result, IDeque<A> keys, TrieNode<A, V> currNode) {
        if (currNode.pointers.size() != 0) {
            for (Map.Entry<A, TrieNode<A, V>> entry : currNode.pointers.entrySet()) {
                keys.addBack(entry.getKey());
                if (entry.getValue().value != null) {
                    ArrayDeque<A> keyCopy = new ArrayDeque<>();
                    Iterator<A> iter = keys.iterator();
                    while (iter.hasNext()) {
                        keyCopy.addBack(iter.next());
                    }
                    K collected = this.collector.apply(keyCopy);
                    result.addBack(collected);
                }
                keysHelper(result, keys, entry.getValue());
                keys.removeBack();
            }
        }
    }

    @Override
    public ICollection<V> values() {
        IDeque<V> values = new ArrayDeque<>();
        for (K key : keys()) {
            values.addBack(get(key));
        }
        return values;
    }

    @Override
    public Iterator<K> iterator() {
        return keys().iterator();
    }
    
    private static class TrieNode<A, V> {
        public final Map<A, TrieNode<A, V>> pointers;
        public V value;

        public TrieNode() {
            this(null);
        }

        public TrieNode(V value) {
            this.pointers = new HashMap<>();
            this.value = value;
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            if (this.value != null) {
                b.append("[" + this.value + "]-> {\n");
                this.toString(b, 1);
                b.append("}");
            }
            else {
                this.toString(b, 0);
            }
            return b.toString();
        }

        private String spaces(int i) {
            StringBuilder sp = new StringBuilder();
            for (int x = 0; x < i; x++) {
                sp.append(" ");
            }
            return sp.toString();
        }

        protected boolean toString(StringBuilder s, int indent) {
            boolean isSmall = this.pointers.entrySet().size() == 0;

            for (Map.Entry<A, TrieNode<A, V>> entry : this.pointers.entrySet()) {
                A idx = entry.getKey();
                TrieNode<A, V> node = entry.getValue();

                if (node == null) {
                    continue;
                }

                V value = node.value;
                s.append(spaces(indent) + idx + (value != null ? "[" + value + "]" : ""));
                s.append("-> {\n");
                boolean bc = node.toString(s, indent + 2);
                if (!bc) {
                    s.append(spaces(indent) + "},\n");
                }
                else if (s.charAt(s.length() - 5) == '-') {
                    s.delete(s.length() - 5, s.length());
                    s.append(",\n");
                }
            }
            if (!isSmall) {
                s.deleteCharAt(s.length() - 2);
            }
            return isSmall;
        }
    }
}
