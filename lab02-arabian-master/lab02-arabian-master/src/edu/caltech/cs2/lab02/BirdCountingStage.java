package edu.caltech.cs2.lab02;

import java.util.*;

public class BirdCountingStage implements AdventureStage {
    private static final int[] SOME_NEAT_NUMBERS = {5, 3, 2, 6, 1};

    private final Scanner sc;
    private final Map<String, AdventureStage> responses;
    private List<String> input = new ArrayList<>();

    /**
     * Constructor for BirdCountingStage.
     * Sets responses and scanner.
     *
     * @param sc scanner used by whole game.
     */
    public BirdCountingStage(Scanner sc) {
        this.sc = sc;
        this.responses = new HashMap<>();
        this.responses.put("flowers", new FillerStage(
            "Woah! Look at the flowers! There's all kinds of colors! That's pretty neat! I think I see some animals up ahead! Let's go check it out.",
            "go",
            new SpeciesListStage(sc)
        ));
        this.responses.put("rocks", new FillerStage(
            "Woah! Those are some neat rocks! I think there are some neat animals up ahead! Let's go check it out. ",
            "go",
            new SpeciesListStage(sc)
        ));
    }

    /**
     * returns prompt
     *
     * @return prompt for next stage selection.
     */
    @Override
    public String riddlePrompt() {
        return "Now that that's over, do you want to look at neat rocks or neat flowers?";
    }

    /**
     * Plays stage.
     * Gives prompt for number of ducks and prints out 3 sets of ducks that the user counts and inputs.
     * If wrong answer is given for number of ducks, repeat with 3 new sets of ducks.
     */
    @Override
    public void playStage() {
        while (true) {
            System.out.println("The Caltech Mallard Duck likes to travel in packs. That's pretty neat.\n" +
                    "How many ducks do you see?");
            int count = 0;
            int expectedSum = 0;
            while (count < 3) {
                int currNum = SOME_NEAT_NUMBERS[AdventureGame.RAND.nextInt(SOME_NEAT_NUMBERS.length)];
                expectedSum += currNum;
                for (int i = 0; i < currNum; i++) {
                    System.out.print("-.-");
                    if (i < currNum - 1) {
                        System.out.print(" ");
                    }
                }
                System.out.println();
                if(this.sc.hasNextLine()) {
                    this.input.add(this.sc.next());
                }
                else{
                    throw new NullPointerException();
                }
                if (count < 2) {
                    System.out.println("How about now?");
                }
                count++;
            }
            if (this.sumInput() == expectedSum) {
                break;
            }
            System.out.println("You did not count the ducks correctly. Let's try again!");
            this.input.clear();
        }
        System.out.println("You sure did look at all those chickens! That's pretty neat!");
    }

    /**
     * Gets hashmap of responses
     *
     * @return hashmap of responses generated by constructor.
     */
    @Override
    public Map<String, AdventureStage> getResponses() {
        return this.responses;
    }

    /**
     * Uses this.input (the user's number inputs) to calculate the sum of this.input.
     *
     * @return sum of elements in this.input.
     */
    private int sumInput() {
        int sum = 0;
        for (int i = 0; i < this.input.size(); i++) {
            sum += Integer.parseInt(this.input.get(i));
        }
        return sum;
    }

}
