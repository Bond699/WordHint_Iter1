package edu.bu.met.wordhint_iter1;


import android.content.Context;
import android.widget.Button;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameModel {
    private static GameModel instance = null;

    public static GameModel getInstance(Context context) {
        if(instance == null) {
            instance = new GameModel(context);
        }
        return instance;
    }

    private Boolean removeHint;
    private String image;
    protected InputOutput io;
    protected List <GameButton> solutionButtons; // Buttons: Blank at first, then contains user's solution
    protected List <GameButton> poolButtons; // Buttons representing the current state of the pool of available letter buttons
    protected List <String> puzzles;

    // Used in the creation of the buttons.
    protected List<String> word; // actual compound word to be solved
    protected List<String> alphabet; // all possible English characters any puzzle might need
    protected List<String> pool; // pool of available letters shown to the user to select from
    protected List<String> addedToPool; // letters that were added to the Pool in addition to the solution word letters.

    private Context context;

    private GameModel(Context context) {
        this.context = context;
        this.removeHint = false;
        this.puzzles = new ArrayList<>();
        this.pool = new ArrayList<>();
        this.addedToPool = new ArrayList<>();
        this.word = new ArrayList<>();
        this.solutionButtons = new ArrayList<>();
        this.poolButtons = new ArrayList<>();
        this.poolButtons = new ArrayList<>();
        this.io = new InputOutput(context, this);

        populatePuzzles();

        // Move this to a language config file possibly
        alphabet = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
                "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"));
    }

    public String getImage() {
        return image;
    }

    public void setCurrentPuzzle(int next) {
        // Loop back to the first puzzle for now if all we've reached the puzzle limit
        if (next > puzzles.size()) {
            io.editor.putInt(io.LEVEL_KEY, 1);
            io.editor.commit();
            return;
        }
        io.editor.putInt(io.LEVEL_KEY, next);
        io.editor.commit();
    }

    public int getCurrentPuzzle() {
        return io.sharedPref.getInt(io.LEVEL_KEY, -1);
    }

    public int getHighestPuzzle() {
        return io.sharedPref.getInt(io.HIGHEST_KEY, -1);
    }

    public void setHighestPuzzle(int highest) {
        io.editor.putInt(io.HIGHEST_KEY, highest);
        io.editor.commit();
    }

    public int getStars() {
        return io.sharedPref.getInt(io.STAR_KEY, -1);
    }

    public void setStars(int stars) {
        //this.stars = stars;
        io.editor.putInt(io.STAR_KEY, stars);
        io.editor.commit();
    }

    public void addStars(int stars) {
        io.editor.putInt(io.STAR_KEY, (getStars() + stars));
        io.editor.commit();
    }

    public Boolean getRemoveHint() {
        return removeHint;
    }

    public void setRemoveHint(Boolean removeHint) {
        this.removeHint = removeHint;
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
        return sb.toString().equals(listToString());
    }

    //Convert Array to String.
    // Else I have to use Oreo to get to the Java version that supports String.join(",", arr1);
    public String listToString() {
        StringBuilder sb = new StringBuilder();
        for (String letter : word) {
            sb.append(letter);
        }
        return sb.toString();
    }

    public List<String> stringToList(String string) {
        return new ArrayList<>(Arrays.asList(string.split("(?!^)")));
    }

    private void populatePuzzles() {
        puzzles.add("JUMPROPE");
        puzzles.add("HORSESHOE");
        puzzles.add("ANTFARM");
        puzzles.add("CARWASH");
        puzzles.add("MOONWALK");
        puzzles.add("TEASPOON");
        puzzles.add("MILKSHAKE");
        puzzles.add("TAILGATE");
        puzzles.add("HAIRBAND");
        puzzles.add("TIMELINE");
        puzzles.add("BACKPACK");
    }

    public void getNextPuzzle(int loadPuzzle) {
        // Clear data (if any) from previous puzzle (if any)
        pool.clear();
        word.clear();
        addedToPool.clear();
        solutionButtons.clear();
        poolButtons.clear();

        // What happens when we finish all of our puzzles? We have 2 right now. We start over.
         if (getCurrentPuzzle() > puzzles.size()) {
            setCurrentPuzzle(1);
        }
        //String puzzle = puzzles.get(getCurrentPuzzle() - 1); // public puzzle id is 1 based, but arraylist 0 based
        String puzzle = puzzles.get(loadPuzzle - 1);

        word = stringToList(puzzle);
        image = puzzle.toLowerCase();

        assemblePool();
    }

    private void assemblePool() {
        // This starts the same as the compound word (word) and is doubled with bogus letters
        for (String letter : word) {
            pool.add(letter);
        }

        int poolSize = pool.size();
        while (pool.size() < poolSize * 2) {
            String letterToAdd = alphabet.get(new Random().nextInt(alphabet.size() - 1));
            pool.add(letterToAdd);
            addedToPool.add(letterToAdd); // makes it easier later when Remove Hint button used.
        }
        Collections.shuffle(pool);
    }
}
