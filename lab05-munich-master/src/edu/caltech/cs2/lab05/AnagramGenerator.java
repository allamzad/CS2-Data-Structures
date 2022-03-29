package edu.caltech.cs2.lab05;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnagramGenerator {
    public static void printPhrases(String phrase, List<String> dictionary) {
        String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
                "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        LetterBag original = new LetterBag(phrase);
        List<String> hictionary = new ArrayList<>();
        hictionary.addAll(dictionary);
        List<String> acc = new ArrayList<>();
        if (phrase.equals(" ") || phrase.equals("")) {
            System.out.println(acc);
        }
        while (phrase.contains(" ")) {
            phrase = phrase.replace(" ", "");
        }
        for (String s : alphabet) {
            for (String word : dictionary) {

                if ((word.contains(s) && !phrase.contains(s)) || word.length() > phrase.length()) {
                    hictionary.remove(word);
                }
            }
        }
            List<String> allPermutations = new ArrayList<>();
            for (String word : hictionary) { //allwords
                if (new LetterBag(word).equals(original) && !allPermutations.contains(word)) {
                    allPermutations.add(word);
                }
                if (word.length() < phrase.length()) {
                    for (String k : hictionary) { //allwords
                        if (new LetterBag(word + k).equals(original) && !allPermutations.contains(word + " " + k)) {
                            allPermutations.add(word + " " + k);
                        }
                        for (String b : hictionary) {
                            if (b.length() <= phrase.length() - word.length() - k.length()) {
                                    if (new LetterBag(word + k + b).equals(original) && !allPermutations.contains(word + " " + k + " " + b)) {
                                        allPermutations.add(word + " " + k + " "  + b);
                                    }
                            }
                        }
                    }
                }
            }
            for (String word : allPermutations) {
                System.out.println(Arrays.toString(word.split(" ")));
            }

        }

    public static void printWords(String word, List<String> dictionary) {
        LetterBag choices = new LetterBag(word);
        LetterBag unchanged = new LetterBag(word);
        for(String words : dictionary){
            LetterBag dictWord = new LetterBag(words);
                if(words.length() == word.length()) {
                    if (choices.equals(dictWord)) {
                        System.out.println(words);
                        choices = unchanged;
                    }
                }
        }
    }



}
