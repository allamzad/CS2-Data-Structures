package edu.caltech.cs2.textgenerator;

import edu.caltech.cs2.datastructures.LinkedDeque;
import edu.caltech.cs2.interfaces.IDeque;

import java.util.Iterator;
import java.util.Objects;

public class NGram implements Iterable<String>, Comparable<NGram> {
    public static final String NO_SPACE_BEFORE = ",?!.-,:'";
    public static final String NO_SPACE_AFTER = "-'><=";
    public static final String REGEX_TO_FILTER = "”|\"|“|\\(|\\)|\\*";
    public static final String DELIMITER = "\\s+|\\s*\\b\\s*";
    private IDeque<String> data;

    public static String normalize(String s) {
        return s.replaceAll(REGEX_TO_FILTER, "").strip();
    }

    public NGram(IDeque<String> x) {
        this.data = new LinkedDeque<>();
        for (String word : x) {
            this.data.add(word);
        }
    }

    public NGram(String data) {
        this(normalize(data).split(DELIMITER));
    }

    public NGram(String[] data) {
        this.data = new LinkedDeque<>();
        for (String s : data) {
            s = normalize(s);
            if (!s.isEmpty()) {
                this.data.addBack(s);
            }
        }
    }

    public NGram next(String word) {
        String[] data = new String[this.data.size()];
        Iterator<String> dataIterator = this.data.iterator();
        dataIterator.next();
        for (int i = 0; i < data.length - 1; i++) {
            data[i] = dataIterator.next();
        }
        data[data.length - 1] = word;
        return new NGram(data);
    }

    public String toString() {
        String result = "";
        String prev = "";
        for (String s : this.data) {
            result += ((NO_SPACE_AFTER.contains(prev) || NO_SPACE_BEFORE.contains(s) || result.isEmpty()) ? "" : " ") + s;
            prev = s;
        }
        return result.strip();
    }

    @Override
    public Iterator<String> iterator() {
        return this.data.iterator();
    }

    @Override
    public int compareTo(NGram other) {
            Iterator<String> it = this.iterator();
            Iterator<String> ot = other.iterator();
            String dataStr = it.next();
            String otherStr = ot.next();

            if(this.data.size() > other.data.size()){
                return 1;
            }
            if(this.data.size() < other.data.size()){
                return -1;
            }
            while(it.hasNext()) {
                    if (dataStr.hashCode() < otherStr.hashCode()) {
                        return -1;
                    } else if (dataStr.hashCode() > otherStr.hashCode()) {
                        return 1;
                    }
                    else{
                        dataStr = it.next();
                        otherStr = ot.next();
                }
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        Iterator<String> it = this.iterator();
        Iterator<String> ot = ((NGram)o).iterator();

        if(!this.data.peekFront().contains(" ") && this.data.peekFront().length() > 1) {
            if (this.compareTo((NGram) o) == 0) {
                return true;
            }
            return false;
        }

        if (!(o instanceof NGram)) {
            return false;
        }
        else{
            boolean equal = true;
                if (((NGram) o).data.size() == this.data.size()) {
                    while (it.hasNext() && ot.hasNext()) {
                        if (!Objects.equals(it.next(), ot.next())) {
                            equal = false;
                            break;
                        }
                    }
                }
            else{
                return false;
            }
            return equal;
        }

    }

    @Override
    public int hashCode() {
        int hashcode = 0;
        int numMultiply = 1;
        int num = 1;
        for(String k : this.data){
            char j = k.charAt(0);
            num = 1;
            for(int o = 0; o < numMultiply; o++){
                num *= j;
            }
            hashcode += num;
            numMultiply++;
        }
        return hashcode;
    }
}