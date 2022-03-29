package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.ITrieMap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.Iterator;

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
        if(this.root == null){
            return false;
        }
        TrieNode<A,V> start = this.root;
        for(A m : key){
            start = changeStart(start, m);
        }
        return start != null;
    }

    @Override
    public ICollection<V> getCompletions(K prefix) {
        return null;
    }

    @Override
    public void clear() {
        this.root = null;
        this.size = 0;
    }

    @Override
    public V get(K key) {
        V value = null;
        if(this.root == null){
            return null;
        }
        TrieNode<A,V> start = this.root;
        for(A m : key){
            value = start.value;
            start = changeStart(start, m);
        }
        return value;
    }

    @Override
    public V remove(K key) {
        if(this.size == 0){
            return null;
        }
        this.root = null;
        return null;
    }

    @Override
    public V put(K key, V value) {
        V oldValue = null;
        if(this.root == null){
            this.root = new TrieNode<>();
        }
        TrieNode<A,V> start = this.root;
        int keyLength = 0;
        for(A m : key){
            keyLength++;
        }
        int curLength = 0;
        for (A m : key) {
            curLength++;
                if (curLength == keyLength) {
                   if (start.value != null) {
                        start.value = value;
                        this.size++;
                    } else {
                        oldValue = start.value;
                        start.value = value;
                   }
                }
                else {
                    putHelper(start, m);
                    start = changeStart(start, m);
                }
            }
        return oldValue;
    }

    public void putHelper(TrieNode<A,V> start, A alphabet){
        if(!start.pointers.containsKey(alphabet)){
            start.pointers.put(alphabet, new TrieNode<>());
        }
    }


    public TrieNode<A, V> changeStart(TrieNode<A,V> start, A alphabet){
        return start.pointers.get(alphabet);
    }

    @Override
    public boolean containsKey(K key) {
        return this.keys().contains(key);
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
        ICollection<K> result = new ArrayDeque<>();
        TrieNode<A,V> start = this.root;
        IDeque<A> m = new ArrayDeque<>();
        return keys(result, m, start);
    }


    private ICollection<K> keys(ICollection<K> result, IDeque<A> acc, TrieNode curr) {
        if(curr == null){
            return result;
        }
        //if (curr.value != null) {
            //K key = this.collector.apply(acc);
            //result.add(key);
       // }
        if(curr.value == null){
            acc.removeBack();
        }
        // traverse all the paths

        for (Object c : curr.pointers.keySet()) {
            acc.add((A) c);
            K key = this.collector.apply(acc);
            result.add(key);
            keys(result, acc, (TrieNode) curr.pointers.get((A) c)); // recurse
        }
        return result;
    }


    @Override
    public ICollection<V> values() {
        ICollection<V> result = new ArrayDeque<>();
        return values(result, this.root);
    }

    private ICollection<V> values(ICollection<V> result, TrieNode curr) {
        if(curr == null){
            return result;
        }
        for (K c : this.keys()) {
            result.add((this.get(c)));
        }
        return result;
    }

    @Override
    public Iterator<K> iterator() {
        return null;
    }

    private class TrieMapIterator implements Iterator<K> {
        /*
        Option 1: "currentIndex is the previous index that was next()ed."
        Option 2: "currentIndex is the next index to give to next()"
        We chose option 2.
         */
        private int currentIndex;

        public TrieMapIterator() {
            ICollection<K> k = TrieMap.this.keys();
        }

        public boolean hasNext() {
            return this.currentIndex < (TrieMap.this).size();
        }

        public K next() {
            //K element = TrieMap.this.get(keys());
            //this.currentIndex++;
            //return element;
            return null;
        }
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
