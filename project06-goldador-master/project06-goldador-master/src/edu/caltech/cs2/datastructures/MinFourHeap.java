package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.interfaces.IPriorityQueue;

import java.util.Iterator;

public class MinFourHeap<E> implements IPriorityQueue<E> {

    private static final int DEFAULT_CAPACITY = 10;

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

    @Override
    public void increaseKey(PQElement<E> key) {
        if(this.keyToIndexMap.containsKey(key.data)) {
            int index = this.keyToIndexMap.get(key.data);
            this.data[index] = new PQElement<>(key.data, key.priority);
            boolean notGood = true;
            while(notGood) {
                for (int i = 0; i < (this.size + 3)/4; i ++) {
                    //GO THROUGH GROUPS OF 4, FIND THE MINIMUM, SWAP WITH THE HIGHER NODE
                    PQElement min = this.data[i * 4 + 1];
                    int minIndex = i * 4 + 1;
                    for(int j = i * 4 + 1; j < i * 4 + 5; j++){
                        if(j == this.size || this.data[j] == null){
                            break;
                        }
                        if(this.data[j].priority < min.priority){
                            min = this.data[j];
                            minIndex = j;
                        }
                    }
                    if(min != null && (min.priority < this.data[i].priority)){
                        PQElement temp = this.data[i];
                        this.data[i] = min;
                        this.data[minIndex] = temp;
                    }
                }
                boolean check = false;
                for (int j = 0; j < this.size; j++) {
                    if (this.data[j].priority < this.data[(j - 1) / 4].priority) {
                        check = true;
                        break;
                    }
                }
                if (!check){
                    break;
                }
            }
            for (int k = 0; k < this.size; k++) {
                this.keyToIndexMap.put(this.data[k].data, k);
            }
        }
        else{
            throw new IllegalArgumentException();
        }
    }


    @Override
    public void decreaseKey(PQElement<E> key) {
        if(this.keyToIndexMap.containsKey(key.data)) {
            this.increaseKey(key);
        }
        else{
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean enqueue(PQElement<E> epqElement) {
        if (this.keyToIndexMap.containsKey(epqElement.data)) {
            throw new IllegalArgumentException();
        }
        else if (this.size >= this.data.length) {
            PQElement<E>[] oldData = this.data;
            this.data = new PQElement[this.data.length * 2];
            for (int i = 0; i < oldData.length; i++) {
                this.data[i] = oldData[i];
            }
        }
        if (this.size == 0) {
            this.data[0] = epqElement;
            this.keyToIndexMap.put(epqElement.data, 0);
            this.size++;
        }
        else {
            this.data[this.size] = epqElement;
            this.keyToIndexMap.put(epqElement.data, this.size);
            this.size++;
            increaseKey(new PQElement<>(epqElement.data, epqElement.priority));
        }
        return true;
    }

    @Override
    public PQElement<E> dequeue() {
        if(this.keyToIndexMap.size() == 0){
            return null;
        }
        else{
            PQElement removed = this.data[0];
            this.data[0] = null;
            this.keyToIndexMap.remove((E) removed.data);
            for(int i = 0; i < this.size; i++){
                data[i] = data[i+1];
            }
            this.size--;
            boolean notGood = true;
            while(notGood) {
                for (int i = 0; i < (this.size + 3)/4; i ++) {
                    //GO THROUGH GROUPS OF 4, FIND THE MINIMUM, SWAP WITH THE HIGHER NODE
                    PQElement min = this.data[i * 4 + 1];
                    int minIndex = i * 4 + 1;
                    for(int j = i * 4 + 1; j < i * 4 + 5; j++){
                        if(j == this.size || this.data[j] == null){
                            break;
                        }
                        if(this.data[j].priority < min.priority){
                            min = this.data[j];
                            minIndex = j;
                        }
                    }
                    if(min != null && (min.priority < this.data[i].priority)){
                        PQElement temp = this.data[i];
                        this.data[i] = min;
                        this.data[minIndex] = temp;
                    }
                }
                boolean check = false;
                for (int j = 0; j < this.size; j++) {
                    if (this.data[j].priority < this.data[(j - 1) / 4].priority) {
                        check = true;
                        break;
                    }
                }
                if (!check){
                    break;
                }
            }
            for (int k = 0; k < this.size; k++) {
                this.keyToIndexMap.put(this.data[k].data, k);
            }
            return removed;
        }
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