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
        return false;
    }


    @Override
    public IDeque<V> getCompletions(K prefix) {
        return null;
    }

    @Override
    public void clear() {}

    @Override
    public V get(K key) {
        return null;
    }

    @Override
    public V remove(K key) {
        return null;
    }

    @Override
    public V put(K key, V value) {
        return null;
    }

    @Override
    public boolean containsKey(K key) {
        return false;
    }

    @Override
    public boolean containsValue(V value) {
        return false;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public ICollection<K> keys() {
        return null;
    }

    @Override
    public ICollection<V> values() {
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return null;
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
