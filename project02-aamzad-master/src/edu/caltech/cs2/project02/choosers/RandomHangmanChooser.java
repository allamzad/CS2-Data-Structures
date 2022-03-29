package edu.caltech.cs2.project02.choosers;

import edu.caltech.cs2.project02.interfaces.IHangmanChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;



public class RandomHangmanChooser implements IHangmanChooser {

  private static final Random RANDOM = new Random();
  private final String secretWord;
  private String curGuess = "";
  private static int numGuesses = 0;
  private SortedSet<Character> guesses = new TreeSet<>();
    
  public RandomHangmanChooser(int wordLength, int maxGuesses) throws FileNotFoundException {
    numGuesses = maxGuesses;
    if(wordLength < 1 || maxGuesses < 1){
      throw new IllegalArgumentException();
    }
    String word = "";
    Scanner reader = new Scanner(new File("data/scrabble.txt"));
    SortedSet<String> correctLengthWords = new TreeSet<>();

    while(reader.hasNext()){
      word = reader.nextLine();
      if(word.length() == wordLength) {
        correctLengthWords.add(word);
      }
    }
    if(correctLengthWords.isEmpty()){
      throw new IllegalStateException();
    }
    int randomWordIndex = RANDOM.nextInt(correctLengthWords.size());
    Iterator<String> iterate = correctLengthWords.iterator();
    for(int i = 0; i < randomWordIndex; i++){
      iterate.next();
    }
    secretWord = iterate.next();
    reader.close();
  }


  public int makeGuess(char letter) {
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
    if(numGuesses < 1){
      throw new IllegalStateException();
    }
    if (!secretWord.contains(Character.toString(letter))){
      numGuesses--;
    }
    for(int i = 0; i < secretWord.length(); i++){
      if(secretWord.charAt(i) == letter){
        occurrences++;
      }
    }
    guesses.add(letter);
    String theoreticalWord = "";
    for(int l = 0; l < secretWord.length(); l++){
      if(curGuess.charAt(l) == '-'){
        theoreticalWord += letter;
      }
      else{
        theoreticalWord += curGuess.charAt(l);
      }
    }
    if(secretWord.equals(theoreticalWord)){
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
    curGuess = "";
    boolean charResolved;
    for(int i = 0; i < secretWord.length(); i++){
      Iterator<Character> iterate = guesses.iterator();
      charResolved = false;
      while(iterate.hasNext()){
        char n = iterate.next();
        if(n == secretWord.charAt(i)){
          curGuess += n;
          charResolved = true;
        }
      }
      if(!charResolved){
        curGuess += '-';
      }
    }
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
}