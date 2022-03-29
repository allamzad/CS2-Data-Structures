package edu.caltech.cs2.project01;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SubstitutionCipher {
    private String ciphertext;
    private Map<Character, Character> key;

    // Use this Random object to generate random numbers in your code,
    // but do not modify this line.
    private static final Random RANDOM = new Random();

    /**
     * Construct a SubstitutionCipher with the given cipher text and key
     *
     * @param ciphertext the cipher text for this substitution cipher
     * @param key        the map from cipher text characters to plaintext characters
     */
    public SubstitutionCipher(String ciphertext, Map<Character, Character> key) {
        this.ciphertext = ciphertext;
        this.key = key;
    }

    /**
     * Construct a SubstitutionCipher with the given cipher text and a randomly
     * initialized key.
     *
     * @param ciphertext the cipher text for this substitution cipher
     */
    public SubstitutionCipher(String ciphertext) {
        this.ciphertext = ciphertext;
        Map<Character, Character> key = new HashMap<>();
        for (int i = 0; i < CaesarCipher.ALPHABET.length; i++) {
            key.put(CaesarCipher.ALPHABET[i], CaesarCipher.ALPHABET[i]);
        }
        this.key = key;
        for (int j = 0; j < 10000; j++) {
            this.randomSwap();
        }
    }

    /**
     * Returns the unedited cipher text that was provided by the user.
     *
     * @return the cipher text for this substitution cipher
     */
    public String getCipherText() {
        return ciphertext;
    }

    /**
     * Applies this cipher's key onto this cipher's text.
     * That is, each letter should be replaced with whichever
     * letter it maps to in this cipher's key.
     *
     * @return the resulting plain text after the transformation using the key
     */
    public String getPlainText() {
        String cryptedMessage = "";
        for (int i = 0; i < ciphertext.length(); i++) {
            cryptedMessage += key.get(ciphertext.charAt(i));
        }
        return cryptedMessage;
    }

    /**
     * Returns a new SubstitutionCipher with the same cipher text as this one
     * and a modified key with exactly one random pair of characters exchanged.
     *
     * @return the new SubstitutionCipher
     */
    public SubstitutionCipher randomSwap() {
        int randomNumber = RANDOM.nextInt(26);
        int randomNumber2 = RANDOM.nextInt(26);
        while (randomNumber == randomNumber2) {
            randomNumber2 = RANDOM.nextInt(26);
        }
        Map<Character, Character> modifiedKey = new HashMap<>();
        char randomNumberValue = key.get(CaesarCipher.ALPHABET[randomNumber]);
        char randomNumberValue2 = key.get(CaesarCipher.ALPHABET[randomNumber2]);

        for (int i = 0; i < CaesarCipher.ALPHABET.length; i++) {
            if (i != randomNumber && i != randomNumber2) {
                modifiedKey.put(CaesarCipher.ALPHABET[i], key.get(CaesarCipher.ALPHABET[i]));
            } else if (i == randomNumber) {
                modifiedKey.put(CaesarCipher.ALPHABET[randomNumber], randomNumberValue2);
            } else {
                modifiedKey.put(CaesarCipher.ALPHABET[randomNumber2], randomNumberValue);
            }
        }
        this.key = modifiedKey;
        return new SubstitutionCipher(ciphertext, modifiedKey);
    }

    /**
     * Returns the "score" for the "plain text" for this cipher.
     * The score for each individual quadgram is calculated by
     * the provided likelihoods object. The total score for the text is just
     * the sum of these scores.
     *
     * @param likelihoods the object used to find a score for a quadgram
     * @return the score of the plain text as calculated by likelihoods
     */
    public double getScore(QuadGramLikelihoods likelihoods) {
        int cipherTextLength = this.getCipherText().length();
        String plainText = this.getPlainText();
        double sum = 0;
        for (int i = 0; i < cipherTextLength - 3; i++) {
           sum += likelihoods.get(plainText.substring(i, i + 4));
        }
        return sum;
    }

    /**
     * Attempt to solve this substitution cipher through the hill
     * climbing algorithm. The SubstitutionCipher this is called from
     * should not be modified.
     *
     * @param likelihoods the object used to find a score for a quadgram
     * @return a SubstitutionCipher with the same ciphertext and the optimal
     * found through hill climbing
     */
    public SubstitutionCipher getSolution(QuadGramLikelihoods likelihoods) {
        double oldBestScore = this.getScore(likelihoods);
        double curBestScore = oldBestScore;
        SubstitutionCipher cipher = new SubstitutionCipher(this.ciphertext);
        Map<Character, Character> likelyCipherKey = this.key;
        int i = 0;
        int j = 1;
        while (j < 10) {
            cipher.key = likelyCipherKey;
            for (int k = 0; k < j; k++) {
                cipher.randomSwap();
            }
            i++;
            if (cipher.getScore(likelihoods) > curBestScore){
                i = 0;
                curBestScore = cipher.getScore(likelihoods);
                likelyCipherKey = cipher.key;
            }
            if (i == 1000 && (oldBestScore == curBestScore) && j > 5){
                break;
            }
            else if (i == 1000){
                i = 0;
                j++;
            }
        }
        SubstitutionCipher best = new SubstitutionCipher(cipher.getCipherText(), likelyCipherKey);
        return best;
    }

}