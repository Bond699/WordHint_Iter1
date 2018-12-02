package edu.bu.met.wordhint_iter1;

import android.content.SharedPreferences;
import android.content.Context;
import android.view.View;

public class InputOutput {
    public final String MY_PREF = "userdata";
    public final String STAR_KEY = "stars";
    public final String LEVEL_KEY = "currentPuzzle";
    public final String HIGHEST_KEY = "highestPuzzle";
    public final String SOLUTION_KEY = "solutionButtons";
    public final String POOL_KEY = "poolButtons";
    public final String ADDED_TO_POOL_KEY = "addedToPool";
    public final String REMOVE_HINT_KEY = "removeHint";

    private GameModel model;
    protected SharedPreferences sharedPref;
    protected SharedPreferences.Editor editor;

    public InputOutput(Context context, GameModel model) {
        this.model = model;
        this.sharedPref = context.getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);
        this.editor = sharedPref.edit();

        if (!sharedPref.contains(STAR_KEY)) { setStars(0); }
        if (!sharedPref.contains(LEVEL_KEY)) { setCurrentPuzzle(1); }
        if (!sharedPref.contains(HIGHEST_KEY)) { setHighestPuzzle(1); }
    }

    public void savePoolButtons() {
        StringBuilder sb = new StringBuilder();
        for (GameButton gb : model.poolButtons) {
            sb.append(gb.button.getText());
            if (gb.button.getVisibility() == View.VISIBLE) {
                sb.append("V");
            }
            if (gb.button.getVisibility() == View.INVISIBLE) {
                sb.append("I");
            }
            sb.append(",");
        }
        sb.setLength(sb.length() - 1);
        editor.putString(POOL_KEY, (sb.toString()));
        editor.commit();
    }

    public void saveSolutionButtons() {
        StringBuilder sb = new StringBuilder();
        for (GameButton gb : model.solutionButtons) {
            if (gb.button.getText().equals("")) {
                sb.append("_");
            }
            else {
                sb.append(gb.button.getText());
            }
            //sb.append(gb.button.getText());
            if (gb.button.isClickable()) {
                sb.append("C");
            }
            else {
                sb.append("N");
            }
            sb.append(",");
        }
        sb.setLength(sb.length() - 1);
        editor.putString(SOLUTION_KEY, (sb.toString()));
        editor.commit();
    }

    public void saveAddedToPool() {
        StringBuilder sb = new StringBuilder();
        for (String letter : model.addedToPool) {
            sb.append(letter);
            sb.append(",");
        }
        sb.setLength(sb.length() - 1);
        editor.putString(ADDED_TO_POOL_KEY, (sb.toString()));
        editor.commit();
    }

    public void saveRemoveHint() {
        editor.putBoolean(REMOVE_HINT_KEY, true);
        editor.commit();
    }

    public String loadPoolButtons() {
        return sharedPref.getString(POOL_KEY, "");
    }

    public String loadSolutionButtons() {
        return sharedPref.getString(SOLUTION_KEY, "");
    }

    public String loadAddedToPool() {
        return sharedPref.getString(ADDED_TO_POOL_KEY, "");
    }

    public Boolean loadRemoveHint() {
        if (sharedPref.contains(REMOVE_HINT_KEY)) {
            return sharedPref.getBoolean(REMOVE_HINT_KEY, false);
        }
        else return false;

    }

    public void resetPuzzlePrefs() {
        editor.remove(REMOVE_HINT_KEY);
        editor.remove(POOL_KEY);
        editor.remove(SOLUTION_KEY);
        editor.remove(ADDED_TO_POOL_KEY);
        editor.commit();
    }

    public int getCurrentPuzzle() {
        return sharedPref.getInt(LEVEL_KEY, -1);
    }

    public void setCurrentPuzzle(int current) {
        editor.putInt(LEVEL_KEY, current);
        editor.commit();
    }

    public int getHighestPuzzle() {
        return sharedPref.getInt(HIGHEST_KEY, -1);
    }

    public void setHighestPuzzle(int highest) {
        editor.putInt(HIGHEST_KEY, highest);
        editor.commit();
    }

    public int getStars() {
        return sharedPref.getInt(STAR_KEY, -1);
    }

    public void setStars(int stars) {
        //this.stars = stars;
        editor.putInt(STAR_KEY, stars);
        editor.commit();
    }

    public void addStars(int stars) {
        editor.putInt(STAR_KEY, (getStars() + stars));
        editor.commit();
    }
}
