package edu.caltech.cs2.datastructures;

import com.sun.jdi.InterfaceType;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IQueue;
import edu.caltech.cs2.interfaces.IStack;

import java.util.Iterator;

public class LinkedDeque<E> implements IDeque<E>, IQueue<E>, IStack<E> {
    private Node<E> head, tail;
    private int size;

    public LinkedDeque(){
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    private static class Node<E> {
        public final E data;
        public Node<E> next;
        public Node<E> prev;

        public Node(E data) {
            this(data, null, null);
        }

        public Node(E data, Node<E> prev, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }
    }

    @Override
    public void addFront(E e) {
        Node<E> startNode = new Node<>(e);
        if (this.head == null){
            this.head = startNode;
            this.tail = startNode;
        }
        else{
            startNode.next = this.head;
            this.head.prev = startNode;
            this.head = startNode;
        }
        size++;
    }

    @Override
    public void addBack(E e) {
        Node<E> startNode = new Node<>(e);
        if (this.head == null) {
            this.head = startNode;
            this.tail = startNode;
        }
        else{
            this.tail.next = startNode;
            startNode.prev = this.tail;
            this.tail = startNode;
            this.tail.next = null;
            this.head.prev = null;
        }
        size++;
    }

    @Override
    public E removeFront() {
        E element = null;
        if (this.head == null) {
            return null;
        }
        else if (size == 1){
            element = this.tail.data;
            this.head = null;
            this.tail = null;
            size--;
        }
        else{
            element = this.head.data;
            this.head = this.head.next;
            this.head.prev = null;
            size--;
        }
        return element;
    }

    @Override
    public E removeBack() {
        E element = null;
        if (this.head == null) {
            return null;
        }
        else if (size == 1){
            element = this.tail.data;
            this.head = null;
            this.tail = null;
            size--;
        }
        else{
            element = this.tail.data;
            this.tail = this.tail.prev;
            this.tail.next = null;
            size--;
        }
        return element;
    }

    @Override
    public boolean enqueue(E e) {
        //1
        addBack(e);
        return true;

    }

    @Override
    public E dequeue() {
        //1
        E element = removeFront();
        return element;
    }

    @Override
    public boolean push(E e) {
        //0
        addFront(e);
        return true;
    }

    @Override
    public E pop() {
        //0
        E element = removeFront();
        return element;
    }

    @Override
    public E peekFront() {
        if(this.head != null){
            return this.head.data;
        }
        return null;
    }

    @Override
    public E peekBack() {
        if(this.tail != null){
            return this.tail.data;
        }
        return null;
    }

    @Override
    public E peek() {
        E element = null;
        return this.peekFront();
        /*
        if(this.frontOrBack == 0) {
            return this.peekFront();
        }
        else {
            return this.peekBack();
        }
         */
    }

    @Override
    public Iterator<E> iterator() {
        return new LinkedDeque.LinkedDequeIterator();
    }

    private class LinkedDequeIterator implements Iterator<E> {

        private int currentIndex;
        private Node<E> curr;

        public LinkedDequeIterator() {
            this.currentIndex = 0;
            curr = LinkedDeque.this.head;
        }

        public boolean hasNext() {
            return this.currentIndex < (LinkedDeque.this).size();
        }

        public E next() {
            if(this.currentIndex == 0){
                this.currentIndex++;
                return LinkedDeque.this.head.data;
            }
            curr = curr.next;
            E element = curr.data;
            this.currentIndex++;
            return element;
        }
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public String toString() {
        if (this.isEmpty() || this.head == null) {
            return "[]";
        }

        String result = "[";
        Node<E> curr = this.head;
        result += curr.data + ", ";
        while (curr.next != null) {
            curr = curr.next;
            result += curr.data + ", ";
        }
        result = result.substring(0, result.length() - 2);
        return result + "]";
    }
}
