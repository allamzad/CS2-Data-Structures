package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IQueue;
import edu.caltech.cs2.interfaces.IStack;

import java.util.Iterator;

public class ArrayDeque<E> implements IDeque<E>, IQueue<E>, IStack<E> {
    private E[] data;
    private int size;
    private int frontOrBack = 0;
    private static final int DEFAULT_CAPACITY = 10;
    private static final int GROW_FACTOR = 2;



    private ArrayDeque(int initialCapacity){
        this.data = (E[])new Object[initialCapacity];
        this.size = 0;
    }
    public ArrayDeque(){
        this(DEFAULT_CAPACITY);
    }

    private void ensureCapacity(int size) {
        if (this.capacity() < size) {
            E[] newData = (E[])new Object[(int)(this.capacity()*GROW_FACTOR + 1)];
            for (int i = 0; i < this.size; i++) {
                newData[i] = this.data[i];
            }
            this.data = newData;
        }
    }

    private int capacity() {
        return this.data.length;
    }

    @Override
    public String toString() {
        if (this.isEmpty()) {
            return "[]";
        }

        String result = "[";
        for (int i = 0; i < this.size; i++) {
            result += this.data[i] + ", ";
        }

        result = result.substring(0, result.length() - 2);
        return result + "]";
    }

    @Override
    public void addFront(E e) {
            this.ensureCapacity(this.size + 1);
            for (int i = this.size; i > 0; i--) {
                this.data[i] = this.data[i-1];
            }
            this.data[0] = e;
            this.size++;
    }

    @Override
    public void addBack(E e) {
            this.ensureCapacity(this.size + 1);
            this.data[this.size] = e;
            this.size++;

    }


    @Override
    public E removeFront() {
        E element = null;
        frontOrBack = 0;
        if (this.size > 0) {
            element = this.data[0];
            E[] newData = (E[]) new Object[this.size - 1];
            for (int i = 1; i < this.size; i++) {
                newData[i - 1] = this.data[i];
            }
            this.data = newData;
            this.size--;
        }
        return element;
    }

    @Override
    public E removeBack() {
        frontOrBack = 1;
        E element = null;
        if (this.size > 0) {
            element = this.data[this.size - 1];
            this.size--;
        }
        return element;
    }

    @Override
    public boolean enqueue(E e) {
        frontOrBack = 0;
        this.ensureCapacity(this.size + 1);
        if (this.size < this.capacity()) {
            this.data[this.size] = e;
            this.size++;
            return true;
        }
        return false;
    }

    @Override
    public E dequeue() {
        frontOrBack = 0;
        E element = null;
        if (this.size > 0) {
            element = this.data[0];
            E[] newData = (E[]) new Object[this.size-1];
            for (int i = 1; i < this.size; i++) {
                newData[i - 1] = this.data[i];
            }
            this.data = newData;
            this.size--;
        }
        return element;
    }

    @Override
    public boolean push(E e) {
        frontOrBack = 1;
        this.ensureCapacity(this.size + 1);
        if (this.size < this.capacity()) {
            this.data[this.size] = e;
            this.size++;
            return true;
        }
        return false;
    }

    @Override
    public E pop() {
        frontOrBack = 1;
        E element = null;
        if (this.size > 0) {
            element = this.data[this.size - 1];
            this.size--;
        }
        return element;
    }

    @Override
    public E peekFront() {
        E element = null;
        if (this.size > 0) {
            element = this.data[0];
        }
        return element;
    }

    @Override
    public E peekBack() {
        E element = null;
        if (this.size > 0) {
            element = this.data[this.size-1];
        }
        return element;
    }

    @Override
    public E peek() {
        if(this.frontOrBack == 1){
            E element = null;
            if (this.size > 0) {
                element = this.data[this.size-1];
            }
            return element;
        }
        else{
            E element = null;
            if (this.size > 0) {
                element = this.data[0];
            }
            return element;
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<E> {
        /*
        Option 1: "currentIndex is the previous index that was next()ed."
        Option 2: "currentIndex is the next index to give to next()"
        We chose option 2.
         */
        private int currentIndex;

        public ArrayDequeIterator() {
            this.currentIndex = 0;
        }

        public boolean hasNext() {
            return this.currentIndex < (ArrayDeque.this).size();
        }

        public E next() {
            E element = ArrayDeque.this.data[this.currentIndex];
            this.currentIndex++;
            return element;
        }
    }

    public int size() {
        return this.size;
    }

}

