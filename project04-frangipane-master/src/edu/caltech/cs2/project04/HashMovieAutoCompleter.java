package edu.caltech.cs2.project04;

import edu.caltech.cs2.datastructures.ArrayDeque;
import edu.caltech.cs2.interfaces.IDeque;

import java.util.HashMap;
import java.util.Map;

public class HashMovieAutoCompleter extends AbstractMovieAutoCompleter {
    private static Map<String, IDeque<String>> titles = new HashMap<>();

    public static void populateTitles() {
        for (String title : ID_MAP.keySet()) {
            ArrayDeque<String> k = new ArrayDeque<>();
            String word = "";
            for (int p = 0; p < title.length(); p++) {
                if (title.charAt(p) == ' ') {
                    k.addFront(word);
                    p++;
                    word = "";
                }
                if(p < title.length()) {
                    word += title.charAt(p);
                }
            }
            k.addFront(word);
            ArrayDeque<String> m = new ArrayDeque<>();
            int orange = k.size();
            String suffix = k.peekFront();
            k.removeFront();
            m.addFront(suffix);
            for (int i = 0; i < orange - 1; i++) {
                suffix = k.peekFront() + " " + suffix;
                k.removeFront();
                m.addFront(suffix);
            }
            titles.put(title, m);
        }
    }

    public static IDeque<String> complete(String term) {
        ArrayDeque<String> contains = new ArrayDeque<String>();

        ArrayDeque<String> k = new ArrayDeque<>();
        String word = "";
        for (int p = 0; p < term.length(); p++) {
            if (term.charAt(p) == ' ') {
                k.addBack(word);
                p++;
                word = "";
            }
            if(p < term.length()) {
                word += term.charAt(p);
            }
        }
        k.addBack(word);
        int kSize = k.size();
        String newTerm = "";
        for(int t = 0; t < kSize; t++){
            newTerm += k.peekFront();
            k.removeFront();
        }
        for(String title : titles.keySet()){
            for(String suffix : titles.get(title)){
                if(contains.size() > 0 && contains.peekFront().equals(title)){
                    break;
                }
                ArrayDeque<String> r = new ArrayDeque<>();
                String word2 = "";
                for (int s = 0; s < suffix.length(); s++) {
                    if (suffix.charAt(s) == ' ') {
                        r.addBack(word2.toLowerCase());
                        s++;
                        word2 = "";
                    }
                    if(s < suffix.length()) {
                        word2 += suffix.charAt(s);
                    }
                }
                r.addBack(word2.toLowerCase());

                String newSuffix = "";
                for(int g = 0; g < kSize; g++){
                    newSuffix += r.peekFront();
                    r.removeFront();
                }

                if(newSuffix.equals(newTerm)){
                    contains.addFront(title);
                }
            }
        }
        return contains;
    }
}
