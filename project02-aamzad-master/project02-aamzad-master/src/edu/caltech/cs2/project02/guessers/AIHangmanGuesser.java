package edu.caltech.cs2.project02.guessers;

import edu.caltech.cs2.project02.interfaces.IHangmanGuesser;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.temporal.ChronoField;
import java.util.*;

public class AIHangmanGuesser implements IHangmanGuesser {
  private static final String dict_name = "data/scrabble.txt";

  public char getGuess(String pattern, Set<Character> guesses) throws FileNotFoundException {
    char mostLikelyLetter = 0;
    int maxFrequency = 0;
    String word1;
    Scanner reader = new Scanner(new File(dict_name));
    SortedSet<String> correctLengthWords = new TreeSet<>();
    while(reader.hasNext()){
      word1 = reader.nextLine();
      if(word1.length() == pattern.length()) {
        correctLengthWords.add(word1);
      }
    }
    if(correctLengthWords.isEmpty()){
      throw new IllegalStateException();
    }
    char[] alphabet = {'z', 'y', 'x', 'w', 'v', 'u', 't', 's', 'r', 'q', 'p', 'o', 'n', 'm', 'l',
    'k', 'j', 'i', 'h', 'g', 'f', 'e', 'd', 'c', 'b', 'a'};
    char[] alphabet2 = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
    'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    ArrayList<Character> alphabetList = new ArrayList<>();
    ArrayList<Character> alphabetList2 = new ArrayList<>();

    for (char c : alphabet) {
      alphabetList.add(c);
    }

    for (char tarantula : alphabet2){
      alphabetList2.add(tarantula);
    }

    for (Character guess : guesses) {
      alphabetList.remove(guess);
    }

    for (Character orange : guesses){
      alphabetList2.remove(orange);
    }

    for (char letter : alphabetList) {
      int frequency = 0;
      int numberOfDashes = 0;
      String letters = "";
      String dashes = "";

      for (int i = 0; i < pattern.length(); i++) {
        if (pattern.charAt(i) == '-') {
          numberOfDashes++;
          dashes += '-';
          letters += letter;
        }
      }

      if(numberOfDashes == 1){
        String completedWord;
        for (char banana : alphabetList2) {
          completedWord = "";
          for (int giraffe = 0; giraffe < pattern.length(); giraffe++) {
            if (pattern.charAt(giraffe) == '-') {
              completedWord += banana;
            }
            else{
              completedWord += pattern.charAt(giraffe);
            }
          }
          for (String hawk : correctLengthWords) {
            if(hawk.equals(completedWord)) {
              return banana;
            }
          }
        }
      }

      SortedSet<String> allPermutations = new TreeSet<>();
      for (int k = 0; k < numberOfDashes; k++) {
        for (int l = k; l < numberOfDashes; l++) {
          for (int m = l; m < numberOfDashes; m++) {
            for (int n = m; n < numberOfDashes; n++) {
              for (int o = n; o < numberOfDashes; o++) {
                for (int p = o; p < numberOfDashes; p++) {
                  for (int q = p; q < numberOfDashes; q++) {
                    for (int r = q; r < numberOfDashes; r++) {
                      allPermutations.add(dashes.substring(0, k) + letters.substring(k, l) + dashes.substring(l, m) + letters.substring(m, n) + dashes.substring(n, o) + letters.substring(o, p) + dashes.substring(p, q) + letters.substring(q, r) + dashes.substring(r));
                      allPermutations.add(letters.substring(0, k) + dashes.substring(k, l) + letters.substring(l, m) + dashes.substring(m, n) + letters.substring(n, o) + dashes.substring(o, p) + letters.substring(p, q) + dashes.substring(q, r) + letters.substring(r));
                    }
                  }
                }
              }
            }
          }
        }
      }
      SortedSet<String> finalPermutations = new TreeSet<>();
      for (String allPermutation : allPermutations) {
        String permutation = "";
        int index = 0;
        for (int n = 0; n < pattern.length(); n++) {
          if (pattern.charAt(n) == '-') {
            permutation += allPermutation.charAt(index);
            index++;
          } else {
            permutation += pattern.charAt(n);
          }
        }
        finalPermutations.add(permutation);
      }
      Iterator<String> familiesIterator = finalPermutations.iterator();
      TreeMap<String, SortedSet<String>> tree = new TreeMap<>();

      while(familiesIterator.hasNext()) {
        Iterator<String> lengthWordsIterator = correctLengthWords.iterator();
        String family = familiesIterator.next();
        SortedSet<String> r = new TreeSet<>();
        int familyOccurrencesWord = 0;
        ArrayList<Integer> familyOccurrences = new ArrayList<>();
        for (char letter1: guesses) {
        for(int c = 0; c < family.length(); c++){
            if (family.charAt(c) == letter1 || family.charAt(c) == letter) {
              familyOccurrencesWord++;
              familyOccurrences.add(c);
            }
          }
        }
        for (int lengthWords = 0; lengthWords < correctLengthWords.size(); lengthWords++) {
          while (lengthWordsIterator.hasNext()) {
            boolean inFamily = true;
            String word = lengthWordsIterator.next();
            int letterOccurrencesWord = 0;
            ArrayList<Integer> letterOccurrences = new ArrayList<>();
            for (char letter2: guesses) {
            for(int b = 0; b < word.length(); b++){
                if (word.charAt(b) == letter2 || word.charAt(b) == letter) {
                  letterOccurrencesWord++;
                  letterOccurrences.add(b);
                }
              }
            }
            if(letterOccurrencesWord == familyOccurrencesWord) {
              for (int l = 0; l < letterOccurrences.size(); l++) {
                if (!(letterOccurrences.get(l) == familyOccurrences.get(l))) {
                  inFamily = false;
                  break;
                }
              }
            }
            else{
              inFamily = false;
            }
            if (inFamily) {
              r.add(word);
            }
          }
        }
        tree.put(family, r);
      }
      tree.remove(pattern);
      Set<String> keys = tree.keySet();
      for(String key : keys){
        frequency += tree.get(key).size();
      }
      if (frequency >= maxFrequency){
        mostLikelyLetter = letter;
        maxFrequency = frequency;
      }
    }
    return mostLikelyLetter;
  }


}
