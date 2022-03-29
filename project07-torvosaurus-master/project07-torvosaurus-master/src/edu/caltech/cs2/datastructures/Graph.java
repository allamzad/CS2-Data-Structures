package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.interfaces.IGraph;
import edu.caltech.cs2.interfaces.ISet;

public class Graph<V, E> implements IGraph<V, E> {

    IDictionary<V, IDictionary<V, E>> graph = new ChainingHashDictionary<>(HashDictionary::new);

    @Override
    public boolean addVertex(V vertex) {
        if (!this.vertices().contains(vertex)) {
            this.graph.put(vertex, new HashDictionary<>());
            return true;
        }
        return false;
    }

    @Override
    public boolean addEdge(V src, V dest, E e) {
        if (this.vertices().contains(src) && this.vertices().contains(dest)) {
            E prevValue = this.graph.get(src).put(dest, e);
            return prevValue == null;
        }
        throw new IllegalArgumentException("Vertex not found");
    }

    @Override
    public boolean addUndirectedEdge(V n1, V n2, E e) {
        boolean n1Src = addEdge(n1, n2, e);
        boolean n2Src = addEdge(n2, n1, e);
        if (!n1Src || !n2Src) {
            return false;
        }
        return true;
    }

    @Override
    public boolean removeEdge(V src, V dest) {
        if (!this.vertices().contains(src) || !this.vertices().contains(dest)) {
            throw new IllegalArgumentException("Vertex not found");
        }
        E removedValue = this.graph.get(src).remove(dest);
        return removedValue != null;
    }

    @Override
    public ISet<V> vertices() {
        return this.graph.keySet();
    }

    @Override
    public E adjacent(V i, V j) {
        if (!this.vertices().contains(i) || !this.vertices().contains(j)) {
            throw new IllegalArgumentException("Vertex not found");
        }
        return this.graph.get(i).get(j);
    }

    @Override
    public ISet<V> neighbors(V vertex) {
        if (!this.vertices().contains(vertex)) {
            throw new IllegalArgumentException("Vertex not found");
        }
        return this.graph.get(vertex).keySet();
    }
}