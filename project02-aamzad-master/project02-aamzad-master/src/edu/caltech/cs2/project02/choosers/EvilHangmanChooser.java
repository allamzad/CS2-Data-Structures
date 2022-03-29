package edu.caltech.cs2.project02.choosers;

import edu.caltech.cs2.project02.interfaces.IHangmanChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class EvilHangmanChooser implements IHangmanChooser {
  private String secretWord = "";
  private String curGuess = "";
  private static int numGuesses = 0;
  private SortedSet<Character> guesses = new TreeSet<>();
  private SortedSet<String> correctLengthWords = new TreeSet<>();

  public EvilHangmanChooser(int wordLength, int maxGuesses) throws FileNotFoundException {
    numGuesses = maxGuesses;
    if(wordLength < 1 || maxGuesses < 1){
      throw new IllegalArgumentException();
    }
    String word;
    Scanner reader = new Scanner(new File("data/scrabble.txt"));
    while(reader.hasNext()){
      word = reader.nextLine();
      if(word.length() == wordLength) {
        correctLengthWords.add(word);
      }
    }
    if(correctLengthWords.isEmpty()){
      throw new IllegalStateException();
    }
    String initialWord = "";
    for (int i = 0; i < wordLength; i++){
      initialWord += "-";
    }
    curGuess = initialWord;
    reader.close();
  }

  @Override
  public int makeGuess(char letter) {
    if(numGuesses < 1){
      throw new IllegalStateException();
    }
    int occurrences = 0;
    if (!Character.isLowerCase(letter)){
      throw new IllegalArgumentException();
    }
    if(!guesses.isEmpty() && guesses.size() < 26) {
      Iterator<Character> iterate = guesses.iterator();
      for (int i = 0; i < guesses.size(); i++) {
        if(letter == iterate.next()){
          throw new IllegalArgumentException();
        }
      }
    }
    guesses.add(letter);
    SortedSet<String> potentialFamilies = generateFamilies(letter);
    Iterator<String> familiesIterator = potentialFamilies.iterator();
    TreeMap<String, SortedSet<String>> tree = new TreeMap<>();

    while(familiesIterator.hasNext()) {
      Iterator<String> lengthWordsIterator = correctLengthWords.iterator();
      String family = familiesIterator.next();
      SortedSet<String> r = new TreeSet<>();
      int familyOccurrencesWord = 0;
      ArrayList<Integer> familyOccurrences = new ArrayList<>();
      for(int c = 0; c < family.length(); c++){
        if(family.charAt(c) == letter){
          familyOccurrencesWord++;
          familyOccurrences.add(c);
        }
      }
      for (int lengthWords = 0; lengthWords < correctLengthWords.size(); lengthWords++) {
        while (lengthWordsIterator.hasNext()) {
          boolean inFamily = true;
          String word = lengthWordsIterator.next();
          int letterOccurrencesWord = 0;
          ArrayList<Integer> letterOccurrences = new ArrayList<>();
          for(int b = 0; b < word.length(); b++){
            if(word.charAt(b) == letter){
              letterOccurrencesWord++;
              letterOccurrences.add(b);
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
    int maxFamilySize = 0;
    String familyWithMaxSize = "";
    for (String family : potentialFamilies) {
      if (maxFamilySize < tree.get(family).size()) {
        maxFamilySize = tree.get(family).size();
        familyWithMaxSize = family;
      }
    }

    for(int h = 0; h < familyWithMaxSize.length(); h++){
      if(familyWithMaxSize.charAt(h) == letter){
        occurrences++;
      }
    }
    correctLengthWords = tree.get(familyWithMaxSize);
    curGuess = familyWithMaxSize;
    boolean containsLetter = false;
    for(int d = 0; d < curGuess.length(); d++){
      if(curGuess.charAt(d) == letter){
        containsLetter = true;
        break;
      }
    }
    if(!containsLetter){
      numGuesses--;
    }

    if(tree.get(familyWithMaxSize).size() == 1){
      Iterator<String> secret = tree.get(familyWithMaxSize).iterator();
      secretWord = secret.next();
    }

    String theoreticalWord = "";
    if(secretWord != null) {
      for (int i = 0; i < curGuess.length(); i++) {
        if (curGuess.charAt(i) == '-'){
          theoreticalWord += letter;
        }
        else{
          theoreticalWord += curGuess.charAt(i);
        }
      }
    }
    if(secretWord != null && secretWord.equals(theoreticalWord)){
      curGuess = theoreticalWord;
      guesses.clear();
    }
    return occurrences;
  }

  @Override
  public boolean isGameOver() {
    if(curGuess.equals(secretWord) || numGuesses == 0){
      curGuess = "";
      return true;
    }
    return false;
  }

  @Override
  public String getPattern() {
    return curGuess;
  }

  @Override
  public SortedSet<Character> getGuesses() {
    return guesses;
  }

  @Override
  public int getGuessesRemaining() {
    return numGuesses;
  }

  @Override
  public String getWord() {
    numGuesses = 0;
    return secretWord;
  }

  public SortedSet<String> generateFamilies(char letter){
    int numberOfDashes = 0;
    String letters = "";
    String dashes = "";

    for(int i = 0; i < curGuess.length(); i++) {
      if (curGuess.charAt(i) == '-') {
        numberOfDashes++;
        dashes += '-';
        letters += letter;
      }
    }

      SortedSet<String> allPermutations = new TreeSet<>();
      for(int k = 0; k < numberOfDashes; k++){
        for(int l = k; l < numberOfDashes; l++){
          for(int m = l; m < numberOfDashes; m++) {
            for (int n = m; n < numberOfDashes; n++) {
              for(int o = n; o < numberOfDashes; o++){
                for(int p = o; p < numberOfDashes; p++) {
                  for(int q = p; q < numberOfDashes; q++){
                    for(int r = q; r < numberOfDashes; r++) {
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
      for (int n = 0; n < curGuess.length(); n++) {
        if (curGuess.charAt(n) == '-') {
          permutation += allPermutation.charAt(index);
          index++;
        } else {
          permutation += curGuess.charAt(n);
        }
      }
      finalPermutations.add(permutation);
    }
    return finalPermutations;
    }

}