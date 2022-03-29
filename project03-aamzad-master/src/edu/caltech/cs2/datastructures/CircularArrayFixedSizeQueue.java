package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IFixedSizeQueue;

import java.util.Iterator;

public class CircularArrayFixedSizeQueue<E> implements IFixedSizeQueue<E> {
    private E[] data;
    private int frontIndex;
    private int backIndex;


    public CircularArrayFixedSizeQueue(int capacity){
        this.data = (E[])new Object[capacity];
    }

    @Override
    public boolean isFull() {
        return (this.size() == this.data.length) || (this.backIndex - this.frontIndex - 1
                == this.data.length);
    }

    @Override
    public int capacity() {
        return this.data.length;
    }

    @Override
    public boolean enqueue(E e) {
        if (!isFull()) {
            if(this.backIndex == this.capacity()){
                this.backIndex = 0;
            }
            this.data[backIndex] = e;
            this.backIndex++;
            return true;
        }
        return false;
    }
    @Override
    public E dequeue() {
        E element = data[this.frontIndex];
        if(this.size() > 0) {
            if(this.backIndex == this.capacity()){
                this.backIndex = 0;
            }
            if(frontIndex == 0 && backIndex == this.capacity()){
                E[] newData = (E[]) new Object[this.capacity()];
                for(int i = frontIndex; i < capacity(); i++){
                    newData[i] = this.data[i];
                }
                this.data = newData;
            }
            this.frontIndex++;
            if (this.frontIndex == this.capacity()) {
                this.frontIndex = 0;
            }
        }
        return element;
    }

    @Override
    public E peek() {
        return this.data[this.frontIndex];
    }

    @Override
    public int size() {
        if(frontIndex < backIndex){
            return backIndex - frontIndex;
        }
        else if(backIndex < frontIndex) {
            return this.capacity()- (frontIndex-backIndex);
        }
        else if(frontIndex != 0 && this.data[frontIndex] != null){
            return this.capacity();
        }
        return 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new CircularArrayFixedSizeIterator();
    }

        private class CircularArrayFixedSizeIterator implements Iterator<E> {
            /*
            Option 1: "currentIndex is the previous index that was next()ed."
            Option 2: "currentIndex is the next index to give to next()"
            We chose option 2.
             */
            private int currentIndex;
            private boolean traversed;

            public CircularArrayFixedSizeIterator() {
                this.traversed = false;
                this.currentIndex = frontIndex;
            }

            public boolean hasNext() {
                if(frontIndex < backIndex) {
                    return this.currentIndex < (CircularArrayFixedSizeQueue.this).size();
                }
                else {
                    if(this.currentIndex == CircularArrayFixedSizeQueue.this.capacity()){
                        this.currentIndex = 0;
                        this.traversed = true;
                    }
                    else if((this.currentIndex < CircularArrayFixedSizeQueue.this.capacity()) && !traversed){
                        return true;
                    }
                    if(this.currentIndex < backIndex && traversed){
                        return true;
                    }
                    this.traversed = false;
                    return false;
                }
            }

            public E next() {
                E element = CircularArrayFixedSizeQueue.this.data[this.currentIndex];
                this.currentIndex++;
                return element;
            }
        }

    @Override
    public String toString() {
        if (this.size() == 0) {
            return "[]";
        }
        String result = "[";
        if(frontIndex < backIndex) {
            for (int i = 0; i < this.size(); i++) {
                result += this.data[i] + ", ";
            }
        }
        else{
            for (int i = frontIndex; i < this.size(); i++) {
                result += this.data[i] + ", ";
            }
            for (int i = 0; i < backIndex; i++) {
                result += this.data[i] + ", ";
            }
        }

        result = result.substring(0, result.length() - 2);
        return result + "]";
    }
}
