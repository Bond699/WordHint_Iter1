package edu.bu.met.wordhint_iter1;

import android.graphics.Point;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Display;
import android.widget.Button;

// Abstract class used by the Button Factory
public abstract class GameButton {
    private String letter;
    final int LETTERMARGIN = 6;
    protected Button button;
    protected int width;
    protected int height;
    protected GameModel model;
    protected MainGameActivity activity;
    public static final int ID_OFFSET = 100;
    public final int BUTTON_TEXT_SIZE = 28;

    public GameButton(MainGameActivity activity, int id, GameModel model, String letter) {
        this.model = model;
        this.letter = letter;
        this.activity = activity;
        button = new Button(activity);
        button.setTypeface(null, Typeface.BOLD);
        button.setLetterSpacing(2);
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, BUTTON_TEXT_SIZE);
        button.setIncludeFontPadding(false);
        button.setTag(Integer.valueOf(id));

        // First attempt at scaling buttons to fit the space available
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int numLetters = model.word.size();
        width = (size.x - (numLetters * (LETTERMARGIN * 2))) / numLetters;
        //int width = size.x / 12;
        height = (int) (width * 1.333); // TODO: revisit this 1.33 calculation/aspect ratio
    }

    public String getLetter() {
        return letter;
    }
    
    // Governs what happens when a button is removed--implemented by concrete classes
    public abstract void removeButton(Button callingButton);
}




