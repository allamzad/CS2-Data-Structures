package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IQueue;
import edu.caltech.cs2.interfaces.IStack;

import java.util.Iterator;

public class LinkedDeque<E> implements IDeque<E>, IQueue<E>, IStack<E> {
    private Node<E> head = null;
    private Node<E> tail = null;
    private int size;
    private static class Node<E> {
        public final E data;
        public Node<E> next;
        public Node<E> prev;

        public Node(E data) {
            this(data, null, null);
        }

        public Node(E data, Node<E> next, Node<E> prev) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

    @Override
    public void addFront(E e) {
        if (this.size == 0) {
            this.head = new Node<>(e);
            this.tail = this.head;
            this.size = 1;
        } else {
            Node<E> next = this.head;
            this.head = new Node<>(e, next, null);
            next.prev = this.head;
            this.size++;
        }
    }

    @Override
    public void addBack(E e) {
        if (this.size == 0) {
            this.head = new Node<>(e);
            this.tail = this.head;
            this.size = 1;
        } else {
            Node<E> tail = this.tail;
            this.tail = new Node<>(e, null, tail);
            tail.next = this.tail;
            this.size++;
        }
    }

    @Override
    public E removeFront() {
        if (this.size == 0) {
            return null;
        } else {
            E removedValue = this.head.data;
            this.size--;
            if (this.size == 0) {
                this.head = null;
                this.tail = null;
            } else {
                this.head = this.head.next;
                this.head.prev = null;
            }
            return removedValue;
        }
    }

    @Override
    public E removeBack() {
        if (this.size == 0) {
            return null;
        } else {
            E removedValue = this.tail.data;
            this.size--;
            if (this.size == 0) {
                this.head = null;
                this.tail = null;
            } else {
                this.tail.prev.next = null;
                this.tail = this.tail.prev;
            }
            return removedValue;
        }
    }

    @Override
    public boolean enqueue(E e) {
        this.addBack(e);
        return true;
    }

    @Override
    public E dequeue() {
        if (this.size > 0) {
            return this.removeFront();
        } else {
            return null;
        }
    }

    @Override
    public boolean push(E e) {
        this.addFront(e);
        return true;
    }

    @Override
    public E pop() {
        if (this.size > 0) {
            return this.removeFront();
        } else {
            return null;
        }
    }

    @Override
    public E peekFront() {
        if (this.size == 0) {
            return null;
        } else {
            return this.head.data;
        }
    }

    @Override
    public E peekBack() {
        if (this.size == 0) {
            return null;
        } else {
            return this.tail.data;
        }
    }

    @Override
    public E peek() {
        if (this.size == 0){
            return null;
        }
        return this.head.data;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<>() {
            Node<E> currentNode = head;

            public boolean hasNext() {
                return currentNode != null;
            }

            public E next() {
                E currentData = currentNode.data;
                currentNode = currentNode.next;
                return currentData;
            }
        };
    }

    @Override
    public int size() {
        return this.size;
    }

    public String toString(){
        if (this.head == null) {
            return "[]";
        }

        Iterator<E> iter = this.iterator();
        String result = "[";
        while (iter.hasNext()) {
            result += iter.next() + ", ";
        }

        result = result.substring(0, result.length() - 2) + "]";
        return result;

    }
}
