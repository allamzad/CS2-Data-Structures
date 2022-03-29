package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IQueue;
import edu.caltech.cs2.interfaces.IStack;

import java.util.Iterator;

public class ArrayDeque<E> implements IDeque<E>, IQueue<E>, IStack<E> {
    private static final int DEFAULT_CAPACITY = 10;
    private static final int GROW_FACTOR = 2;
    private E[] data;
    private int size;
    private E head;

    public ArrayDeque(){
        this(DEFAULT_CAPACITY);
    }

    public ArrayDeque(int initialCapacity){
        this.data = (E[])new Object[initialCapacity];
        this.size = 0;
    }

    private int capacity() {
        return this.data.length;
    }

    private void ensureCapacity(int size) {
        if (this.capacity() < size) {
            E[] newData = (E[])new Object[this.capacity()*GROW_FACTOR];
            for (int i = 0; i < this.size; i++) {
                newData[i] = this.data[i];
            }
            this.data = newData;
        }
    }

    @Override
    public void addFront(E e) {
        this.ensureCapacity(this.size + 1);
        E[] newData = (E[])new Object[this.capacity()];
        newData[0] = e;
        for (int i = 0; i < this.size; i++) {
            newData[i + 1] = this.data[i];
        }
        this.data = newData;
        this.head = this.data[0];
        this.size++;
    }

    @Override
    public void addBack(E e) {
        this.ensureCapacity(this.size + 1);
        this.head = e;
        this.data[this.size] = e;
        this.size++;
    }

    @Override
    public E removeFront() {
        if (this.size == 0) {
            return null;
        } else {
            E[] newData = (E[])new Object[this.capacity()];
            E removedValue = this.data[0];
            for (int i = 1; i < this.size; i++) {
                newData[i - 1] = this.data[i];
            }
            this.data = newData;
            this.size--;
            return removedValue;
        }
    }

    @Override
    public E removeBack() {
        if (this.size == 0) {
            return null;
        } else {
            E removedValue = this.data[this.size - 1];
            this.data[this.size - 1] = null;
            this.size--;
            return removedValue;
        }
    }

    @Override
    public boolean enqueue(E e) {
        this.addBack(e);
        this.head = this.data[0];
        return true;
    }

    @Override
    public E dequeue() {
        if (this.size > 0) {
            E returnValue = this.removeFront();
            this.head = this.data[0];
            return returnValue;
        } else {
            return null;
        }
    }

    @Override
    public boolean push(E e) {
        this.addBack(e);
        this.head = e;
        return true;
    }

    @Override
    public E pop() {
        if (this.size > 0) {
            E returnValue = this.removeBack();
            this.head = this.data[this.size - 1];
            return returnValue;
        } else {
            return null;
        }
    }

    @Override
    public E peekFront() {
        if (this.size == 0) {
            return null;
        } else {
            return this.data[0];
        }
    }

    @Override
    public E peekBack() {
        if (this.size == 0) {
            return null;
        } else {
            return this.data[this.size - 1];
        }
    }

    @Override
    public E peek() {
        return this.head;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<>() {
            private int currentIndex = 0;

            public boolean hasNext() {
                return currentIndex < size;
            }

            public E next() {
                return data[currentIndex++];
            }
        };
    }

    @Override
    public int size() {
        return this.size;
    }

    public String toString(){
        if (this.isEmpty()) {
            return "[]";
        }

        String result = "[";
        for (int i = 0; i < this.size; i++) {
            result += this.data[i] + ", ";
        }

        result = result.substring(0, result.length() - 2) + "]";
        return result;

    }
}

