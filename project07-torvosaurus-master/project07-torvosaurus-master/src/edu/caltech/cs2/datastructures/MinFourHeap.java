package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.interfaces.IPriorityQueue;

import java.util.Iterator;

public class MinFourHeap<E> implements IPriorityQueue<E> {

    private static final int DEFAULT_CAPACITY = 10;
    private static final int GROW_FACTOR = 2;

    private int size;
    private PQElement<E>[] data;
    private IDictionary<E, Integer> keyToIndexMap;

    /**
     * Creates a new empty heap with DEFAULT_CAPACITY.
     */
    public MinFourHeap() {
        this.size = 0;
        this.data = new PQElement[DEFAULT_CAPACITY];
        this.keyToIndexMap = new ChainingHashDictionary<>(MoveToFrontDictionary::new);
    }

    private void ensureCapacity(int size) {
        if (this.data.length < size) {
            PQElement<E>[] newData = new PQElement[this.data.length*GROW_FACTOR];
            for (int i = 0; i < this.size; i++) {
                newData[i] = this.data[i];
            }
            this.data = newData;
        }
    }

    @Override
    public void increaseKey(PQElement<E> key) {
        if (!this.keyToIndexMap.containsKey(key.data)) {
            throw new IllegalArgumentException("Does not contain key");
        }
        this.data[this.keyToIndexMap.get(key.data)] = key;
        this.percolateDown(this.data[this.keyToIndexMap.get(key.data)], this.keyToIndexMap.get(key.data));
    }

    @Override
    public void decreaseKey(PQElement<E> key) {
        if (!this.keyToIndexMap.containsKey(key.data)) {
            throw new IllegalArgumentException("Does not contain key");
        }
        this.data[this.keyToIndexMap.get(key.data)] = key;
        this.percolateUp(this.data[this.keyToIndexMap.get(key.data)], this.keyToIndexMap.get(key.data));
    }

    @Override
    public boolean enqueue(PQElement<E> epqElement) {
        if (this.keyToIndexMap.get(epqElement.data) != null) {
            throw new IllegalArgumentException("Key already in queue");
        }
        this.ensureCapacity(this.size + 1);
        this.data[this.size] = epqElement;
        this.keyToIndexMap.put(epqElement.data, this.size);
        this.size++;
        this.percolateUp(epqElement, this.size - 1);
        return true;
    }

    @Override
    public PQElement<E> dequeue() {
        if (this.size == 0) {
            return null;
        }
        PQElement<E> dequeuedElement = this.data[0];
        this.data[0] = this.data[this.size - 1];
        this.data[this.size - 1] = null;
        this.size--;
        this.keyToIndexMap.remove(dequeuedElement.data);
        if (this.size == 0) {
            return dequeuedElement;
        }
        this.keyToIndexMap.put(this.data[0].data, 0);
        percolateDown(this.data[0], 0);
        return dequeuedElement;
    }

    private int percolateUp(PQElement<E> key, int index) {
        if (index != 0) {
            int parentIndex = (index - 1) / 4;
            PQElement<E> parent = this.data[parentIndex];
            if (key.priority < parent.priority) {
                this.data[index] = parent;
                this.data[parentIndex] = key;
                this.keyToIndexMap.put(key.data, parentIndex);
                this.keyToIndexMap.put(parent.data, index);
                return percolateUp(key, parentIndex);
            }
            return index;
        }
        return 0;
    }

    private int percolateDown(PQElement<E> key, int index) {
        if (4 * (index + 1) - 3 > size - 1) {
            return index;
        }
        PQElement<E>[] children = new PQElement[4];
        for (int i = 0; i < 4; i++) {
            int childIndex = 4 * (index + 1) - i;
            if (childIndex < size) {
                children[i] = this.data[childIndex];
            }
        }
        PQElement<E> lowestPrioChild = null;
        int childIndex = 0;
        for (int i = 0; i < children.length; i++) {
            if (children[i] != null) {
                if (lowestPrioChild == null || children[i].priority < lowestPrioChild.priority) {
                    lowestPrioChild = children[i];
                    childIndex = 4 * (index + 1) - i;
                }
            }
        }
        if (lowestPrioChild != null && key.priority > lowestPrioChild.priority) {
            this.data[index] = lowestPrioChild;
            this.data[childIndex] = key;
            this.keyToIndexMap.put(key.data, childIndex);
            this.keyToIndexMap.put(lowestPrioChild.data, index);
            return percolateDown(key, childIndex);
        }
        return index;
    }

    @Override
    public PQElement<E> peek() {
        return this.data[0];
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Iterator<PQElement<E>> iterator() {
        return null;
    }
}