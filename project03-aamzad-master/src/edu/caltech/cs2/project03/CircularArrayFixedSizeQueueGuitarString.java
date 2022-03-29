package edu.caltech.cs2.project03;

import edu.caltech.cs2.datastructures.ArrayDeque;
import edu.caltech.cs2.datastructures.CircularArrayFixedSizeQueue;
import edu.caltech.cs2.interfaces.IFixedSizeQueue;
import edu.caltech.cs2.interfaces.IQueue;

import java.util.Random;


public class CircularArrayFixedSizeQueueGuitarString {
    private IFixedSizeQueue queue;
    private static Random num = new Random();
    private static final int baseFreq = 44100;
    private static final double factor = 0.996;

    public CircularArrayFixedSizeQueueGuitarString(double frequency) {
        this.queue = new CircularArrayFixedSizeQueue(((int) (baseFreq * 1.0 / frequency + 1)));
        for(int i = 0; i < queue.capacity(); i++){
            this.queue.enqueue(0.0);
        }
    }

    public int length() {
        return queue.capacity();
    }

    public void pluck() {
        for(int i = 0; i < length(); i++) {
            this.queue.dequeue();
        }
        for(int i = 0; i < length(); i++) {
            this.queue.enqueue((num.nextDouble() - 0.5));
        }
    }

    public void tic() {
        double first = (double) this.queue.peek();
        this.queue.dequeue();
        this.queue.enqueue( factor * (first + (double) this.queue.peek()) / 2.0);

    }

    public double sample() {
        return (double) this.queue.peek();
    }
}
