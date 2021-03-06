package edu.bu.met.wordguess_finalproject;


import android.content.Context;
import android.widget.Button;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameModel {
    // Singleton Pattern
    private static GameModel instance = null;

    public static GameModel getInstance(Context context) {
        if(instance == null) {
            instance = new GameModel(context);
        }
        return instance;
    }
    protected Puzzle currentPuzzle;
    protected InputOutput io;
    protected List <GameButton> solutionButtons; // Buttons: Blank at first, then contains user's solution
    protected List <GameButton> poolButtons; // Buttons representing the current state of the pool of available letter buttons

    // Used in the creation of the buttons.
    protected List<String> alphabet; // all possible English characters any puzzle might need
    protected List<String> pool; // pool of available letters shown to the user to select from
    protected List<String> addedToPool; // letters that were added to the Pool in addition to the solution word letters.

    private GameModel(Context context) {
        this.pool = new ArrayList<>();
        this.addedToPool = new ArrayList<>();
        this.solutionButtons = new ArrayList<>();
        this.poolButtons = new ArrayList<>();
        this.io = new InputOutput(context, this);

        // Move this to a language config file possibly
        alphabet = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
                "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"));
    }

    public void setCurrentPuzzle(int next) {
        // Loop back to the first puzzle for now if all we've reached the puzzle limit
        if (next > io.puzzles.size()) {
            io.setCurrentPuzzle(1);
            io.editor.commit();
            return;
        }
        io.setCurrentPuzzle(next);
        io.editor.commit();
    }


    public Button findFirstBlankButton () {
        // Look at each game button until we find one that's empty
        for (GameButton findButton: solutionButtons) {
            if (findButton.button.getText() == "") {
                return findButton.button;
            }

        }
        //No empty (blank) game button found, thus the solution is full.
        return null;
    }

    public Boolean checkSolution() {
        StringBuilder sb = new StringBuilder();
        for (GameButton gb : solutionButtons) {
            sb.append(gb.button.getText());
        }
        return sb.toString().equals(currentPuzzle.getSolution());
    }

    public void configNextPuzzle(int loadPuzzle) {
        // Clear data (if any) from previous puzzle (if any)
        pool.clear();
        addedToPool.clear();
        solutionButtons.clear();
        poolButtons.clear();

        // What happens when we finish all of our puzzles? We have 2 right now. We start over.
         if (io.getCurrentPuzzle() > io.puzzles.size()) {
            setCurrentPuzzle(1);
        }
        currentPuzzle = io.puzzles.get(loadPuzzle - 1); // public puzzle id is 1 based, but arraylist 0 based

        assemblePool();
    }

    private void assemblePool() {
        // This starts the same as the compound word (word) and is doubled with bogus letters
        for (int i = 0; i < currentPuzzle.getSolution().length(); i++){
            char c = currentPuzzle.getSolution().charAt(i);
            pool.add(Character.toString(c));
        }

        int poolSize = pool.size();
        while (pool.size() < poolSize * 2) { // Double the number of letters
            String letterToAdd = alphabet.get(new Random().nextInt(alphabet.size() - 1));
            pool.add(letterToAdd);
            addedToPool.add(letterToAdd); // makes it easier later when Remove Hint button used.
        }
        Collections.shuffle(pool);
    }
}
