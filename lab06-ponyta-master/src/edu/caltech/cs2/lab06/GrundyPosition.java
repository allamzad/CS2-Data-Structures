package edu.caltech.cs2.lab06;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class GrundyPosition implements Memo{
    /*
     * Stores a mapping from the height of a pile to how many of those piles exist.
     * Does not include piles of size less than three.
     */
    private SortedMap<Integer, Integer> heapCounts = new TreeMap<>();

    /**
     * Initializes a GrundyPosition with a single heap of height heapHeight.
     **/
    public GrundyPosition(int heapHeight) {
        if (heapHeight > 2) {
            this.heapCounts.put(heapHeight, 1);
        }
    }

    public GrundyPosition(SortedMap<Integer, Integer> heapCounts) {
        this.heapCounts = heapCounts;
    }

    /**
     * Returns a list of legal GrundyPositions that a single move of Grundy's Game
     * can get to.
     **/
    public List<GrundyPosition> getMoves() {
        if (this.isTerminalPosition()) {
            return new ArrayList<>();
        }
        ArrayList<GrundyPosition> list = new ArrayList<>();
        for (int key : this.heapCounts.keySet()) {
            for (int i = 1; i < (key+1)/2; i++) {
                list.add(splitHeap(this.heapCounts, key, i));
            }
        }
        return list;
    }

    private GrundyPosition splitHeap(SortedMap<Integer, Integer> heapCounts, int key, int splitNum) {
        SortedMap<Integer, Integer> newHeapCounts = new TreeMap<>(heapCounts);
        newHeapCounts.replace(key, newHeapCounts.get(key) - 1);
        if (newHeapCounts.get(key) == 0) {
            newHeapCounts.remove(key);
        }
        newHeapCounts.merge(key - splitNum, 1, Integer::sum);
        newHeapCounts.merge(splitNum, 1, Integer::sum);
        for (int i = 0; i < 3; i++) {
            newHeapCounts.remove(i);
        }
        if (newHeapCounts.size() == 0) {
            return new GrundyPosition(0);
        }
        return new GrundyPosition(newHeapCounts);
    }

    public boolean isTerminalPosition() {
        return this.heapCounts.keySet().isEmpty();
    }

    public boolean isPPosition() {
        if (memorization.get(this) != null) {
            return memorization.get(this);
        }
        if (this.isTerminalPosition()) {
            return true;
        }
        boolean containsN = true;
        for (GrundyPosition move : this.getMoves()) {
            if (!move.isNPosition()) {
                containsN = false;
            }
        }
        memorization.put(this, containsN);
        return containsN;
    }

    public boolean isNPosition() {
        boolean existsP = false;
        for (GrundyPosition move : this.getMoves()) {
            if (move.isPPosition()) {
                existsP = true;
            }
        }
        memorization.put(this, !existsP);
        return existsP;
    }

    /**
     * Ignore everything below this point.
     **/

    @Override
    public int hashCode() {
       return this.heapCounts.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GrundyPosition)) {
            return false;
        }
        return this.heapCounts.equals(((GrundyPosition) o).heapCounts);
    }

    @Override
    public String toString() {
        return this.heapCounts.toString();
    }
}