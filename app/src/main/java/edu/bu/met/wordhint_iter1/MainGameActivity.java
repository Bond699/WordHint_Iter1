package edu.bu.met.wordhint_iter1;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainGameActivity extends Activity {
    private final int PUZZLE_AWARD = 5;
    private GameModel model;
    private ButtonFactory buttonFactory;
    private Button continueButton;
    private RelativeLayout poolAreaLayout;
    private TextView success;
    private Dialog dialog;
    private Typeface font;
    private View dialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);
        this.model = new GameModel(this);
        this.font = Typeface.createFromAsset(this.getAssets(), "hug_me_tight.ttf");

        Intent intent = getIntent();
        model.setCurrentPuzzle(intent.getIntExtra("puzzle", -1));
        model.getNextPuzzle(intent.getIntExtra("puzzle", -1));
        initPuzzle();
        processSavedAddedToPool();
        processSavedPoolButtons();
        processSavedSolutionButtons();
        processSavedRemoveHint();
        updateLevelView();
        updateStarsView();
    }

    private void initPuzzle() {
        this.buttonFactory = new ButtonFactory();
        buttonFactory.createButtons(this, model);
        ImageView iv = findViewById(R.id.image_puzzle_display);
        iv.setImageResource(getResources().getIdentifier("puzzle_" + model.getImage(),
                "drawable", this.getPackageName()));
    }

    protected void puzzleSuccess() {
        setHintClickable(false); // Prevent clicking "Hint" under puzzle when showing Continue button

        // Resets the non-scoring and non-level data, like pool, solution, etc.
        model.io.resetPuzzlePrefs();

        updateStarsModel();
        updateStarsView();
        updateLevelModel();
        removePoolButtons();
        drawContinueButton();
        drawSuccessMessage();

        // Onclick handler for Continue Button
        continueButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                removeSolutionButtons();
                removePoolSuccessElements();
                updateLevelView();
                model.setRemoveHint(false);
                setHintClickable(true);
                model.getNextPuzzle(model.getCurrentPuzzle());
                initPuzzle();
            }
        });
        poolAreaLayout.addView(continueButton);
    }

    private void removeSolutionButtons() {
        // Remove Solution buttons so the next Puzzle isn't impacted.
        for (GameButton gb : model.solutionButtons) {
            gb.button.setVisibility(View.GONE);
        }
    }

    private void removePoolSuccessElements() {
        // Remove Success message (currently set to Correct!)
        poolAreaLayout.removeView(success);
        // Remove Continue button
        poolAreaLayout.removeView(continueButton);
    }

    private void removePoolButtons() {
        // Remove the buttons from the pool area (because we're going to put a success message and
        // continue button there.
        poolAreaLayout = findViewById(R.id.pool_area);
        for (GameButton gb : model.poolButtons) {
            gb.button.setVisibility(View.GONE);
            poolAreaLayout.removeView(gb.button);
        }
    }

    private void updateStarsModel() {
        model.addStars(PUZZLE_AWARD);
    }

    private void updateStarsView() {
        TextView star = findViewById(R.id.star_count);
        star.setText(Integer.toString(model.getStars()));
    }


    private void updateLevelView() {
        TextView level = findViewById(R.id.level);
        level.setText("WORD " + Integer.toString(model.getCurrentPuzzle()));
    }

    private void updateLevelModel() {
        model.setCurrentPuzzle(model.getCurrentPuzzle() + 1);
        if (model.getCurrentPuzzle() > model.getHighestPuzzle()) {
            model.setHighestPuzzle(model.getCurrentPuzzle());
        }
    }

    private void drawContinueButton() {
        //Continue Button
        continueButton = new Button(poolAreaLayout.getContext());
        continueButton.setTextColor(getResources().getColor(R.color.white_letter));
        continueButton.setText(getResources().getString(R.string.continue_button));
        continueButton.setBackgroundResource(R.drawable.continue_button);
        continueButton.setTypeface(font);
        continueButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(500,200);
        params.addRule(RelativeLayout.BELOW, 200);
        params.setMargins(6, 6, 6, 6);
        continueButton.setLayoutParams(params);

    }

    private void drawSuccessMessage() {
        // Success message
        success = new TextView(poolAreaLayout.getContext());
        success.setText(getResources().getString(R.string.puzzle_correct));
        success.setId(200);

        success.setTypeface(font);
        success.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        success.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
        success.setTextColor(getResources().getColor(R.color.white_letter));

        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(500,100);
        p.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        p.setMargins(0,0,0,40);
        success.setLayoutParams(p);
        poolAreaLayout.addView(success);
    }

    public void onHintClick(View view) {
        // Setup the dialog box and the view
        dialog = new Dialog(this);
        dialogView = dialog.getLayoutInflater().inflate(R.layout.activity_hint, null);
        dialog.setContentView(dialogView);

        // Determine screen size to determine size of the dialog box
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = (int)(dm.heightPixels * .55);

        // Make Hint buttons gray if they can't be used.
        if (model.getRemoveHint()) {
            Button hintRemove = (Button) dialogView.findViewById(R.id.hint_remove);
            hintRemove.setBackgroundResource(R.drawable.hint_button_grey);
        }

        if (getBlankLetters().size() < 2) {
            Button hintRemove = (Button) dialogView.findViewById(R.id.hint_reveal);
            hintRemove.setBackgroundResource(R.drawable.hint_button_grey);
        }

        dialog.getWindow().setLayout(width, height);
        dialog.show();

        // Assign on click listeners and handlers for the two Hint buttons
        onHintRevealClick();
        onHintRemoveClick();
    }

    // REVEAL button functionality
    private void onHintRevealClick() {
        Button hintReveal = (Button) dialogView.findViewById(R.id.hint_reveal);
        hintReveal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Get a list of blank letters in the solution area
                List<GameButton> blankLetters = getBlankLetters();

                // Don't allow reveal to be used with up to N-2 letters remaining
                if (blankLetters.size() < 2) {
                    dialog.dismiss();
                    return;
                }

                // Find a random blank letter to reveal in the list of blank Solution Letters.
                GameButton reveal = blankLetters.get(new Random().nextInt(blankLetters.size()));
                // Subtract the offset from the button id to find it's button's position in array.
                int buttonPosition = reveal.button.getId() - (GameButton.ID_OFFSET * 2);

                // Find a button in the pool area with the same alphabetical letter using position
                GameButton gb = findButtonFromLetter(model.word.get(buttonPosition));
                gb.button.setVisibility(View.INVISIBLE);
                reveal.button.setBackgroundResource(R.drawable.pool_button);
                reveal.button.setTextColor(getResources().getColor(R.color.white_letter));
                reveal.button.setText(gb.button.getText());
                reveal.button.setClickable(false); // User can't remove this letter

                deductHintCost();
                // Save the puzzle state so if user doesn't finish puzzle now, they  still see hint
                model.io.savePoolButtons();
                model.io.saveSolutionButtons();
                model.io.saveAddedToPool();
                dialog.dismiss();
            }
        });
    }

    // REMOVE button functionality
    private void onHintRemoveClick() {
        Button hintRemove = (Button) dialogView.findViewById(R.id.hint_remove);
        hintRemove.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (model.getRemoveHint()) { // Don't allow Remove all to run twice
                    dialog.dismiss();
                    return;
                }
                model.setRemoveHint(true);

                //Loop through each letter in the solution word.
                for (int i = 0; i < model.word.size(); i++) {
                    // Do nothing to blank buttons
                    if (model.solutionButtons.get(i).button.getText().equals("")) {
                        continue;
                    }

                    // If there's a wrong letter in the solution, send the button back into pool
                    if (!model.solutionButtons.get(i).button.getText().toString().equals(model.word.get(i))) {
                        GameButton gb = model.solutionButtons.get(i);
                        gb.removeButton(gb.button);
                    }
                }

                // Remove all letters from the pool of available letters if it's not in the solution.
                for (String letter : model.addedToPool) {
                    GameButton remove = findButtonFromLetter(letter);
                    if (remove == null) {
                        continue;
                    }
                    remove.button.setVisibility(View.INVISIBLE);
                }

                deductHintCost();
                // Save the puzzle state so if user doesn't finish puzzle now, they  still see hint
                model.io.savePoolButtons();
                model.io.saveSolutionButtons();
                model.io.saveAddedToPool();
                model.io.saveRemoveHint();
                dialog.dismiss();
            }
        });
    }

    // Returns a list of blank letters in the solution area--that is, letters that have no letter
    // showing.
    private List<GameButton> getBlankLetters() {
        List <GameButton>onlyBlank = new ArrayList<>();
        for (GameButton gb : model.solutionButtons) {
            if (gb.button.getText().equals("")) {
                onlyBlank.add(gb);
            }
        }
        return onlyBlank;
    }

    // Find a button in the pool area based on a String letter (A, B, C, etc.)
    private GameButton findButtonFromLetter(String letter) {
        for (GameButton gb : model.poolButtons) {
            if (gb.button.getVisibility() == View.INVISIBLE) {
                continue;
            }

            // Return a matching button
            if (gb.button.getText().toString().equals(letter)) {
                return gb;
            }
        }
        // Nothing found
        return null;
    }

    private void processSavedPoolButtons() {
        String pool = model.io.loadPoolButtons();
        if (pool.equals("")) {
            return;
        }

        // Separate the String by commas into an arraylist
        List<String> elements = Arrays.asList(pool.split("\\s*,\\s*"));
        // Loop through the arraylist, separating the letter from the visibility marker.
        for (int i = 0; i < elements.size(); i++) {
            String letter = elements.get(i).substring(0, 1);
            String visibility = elements.get(i).substring(elements.get(i).length() - 1);

            // For each button in the pool area, change it's text/visibility
            GameButton gb = model.poolButtons.get(i);
            gb.button.setText(letter);
            if (visibility.equals("I")) {
                gb.button.setVisibility(View.INVISIBLE);
            }
            else {
                gb.button.setVisibility(View.VISIBLE);
            }
        }
    }

    private void processSavedSolutionButtons() {
        String solution = model.io.loadSolutionButtons();
        if (solution.equals("")) {
            return;
        }
        List<String> elements = Arrays.asList(solution.split("\\s*,\\s*"));

        //List<GameButton> poolButtons = new ArrayList<GameButton>();
        for (int i = 0; i < elements.size(); i++) {
            String letter = elements.get(i).substring(0, 1);
            String clickable = elements.get(i).substring(elements.get(i).length() - 1);
            GameButton gb = model.solutionButtons.get(i);
            if (letter.equals("_")) {
                gb.button.setText("");
            }
            else {
                gb.button.setText(letter);
                gb.button.setBackgroundResource(R.drawable.pool_button);
                gb.button.setTextColor(getResources().getColor(R.color.white_letter));
            }

            if (clickable.equals("C")) {
                gb.button.setClickable(true);
            }
            else {
                gb.button.setClickable(false);
            }
        }
    }

    private void processSavedAddedToPool() {
        String pool = model.io.loadAddedToPool();
        if (pool.equals("")) {
            return;
        }
        List<String> elements = Arrays.asList(pool.split("\\s*,\\s*"));

        model.addedToPool.clear();
        for (String letter : elements) {
            //Log.d("AddedToPoolTest", letter);
            model.addedToPool.add(letter);
        }
    }

    private void processSavedRemoveHint() {
        model.setRemoveHint(model.io.loadRemoveHint());
    }

    private void deductHintCost() {
        //Update model
        if (model.getStars() > 0 ) {
            model.setStars(model.getStars() - 1);
            updateStarsView();
        }
    }

    private void setHintClickable(Boolean clickable) {
        // Guard against user pressing Hint button as/after the Continue Button appears
        Button hintButton = (Button) findViewById(R.id.main_hint_button);
        hintButton.setClickable(clickable);
    }

    public void onClickBack(View view) {
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }
}
