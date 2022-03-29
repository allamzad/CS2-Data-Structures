package edu.caltech.cs2.sorts;

import edu.caltech.cs2.datastructures.MinFourHeap;
import edu.caltech.cs2.interfaces.IPriorityQueue;

public class TopKSort {
    /**
     * Sorts the largest K elements in the array in descending order. Modifies the array in place.
     * @param array - the array to be sorted; will be manipulated.
     * @param K - the number of values to sort
     * @param <E> - the type of values in the array
     * @throws IllegalArgumentException if K < 0
     */
    public static <E> void sort(IPriorityQueue.PQElement<E>[] array, int K) {
        if (K < 0) {
            throw new IllegalArgumentException("K cannot be negative!");
        }
            MinFourHeap topKHeap = new MinFourHeap();

        if(K > 0 && array.length > 0) {
            for (int i = 0; i < array.length; i++) {
                topKHeap.enqueue(array[i]);
            }

            for (int i = 0; i < array.length - K; i++) {
                topKHeap.dequeue();
            }

            for (int i = array.length - K; i < array.length; i++) {
                array[(array.length - 1) - i] = topKHeap.dequeue();
            }
        }
            for(int p = K; p < array.length; p++){
                array[p] = null;
            }


    }



}
