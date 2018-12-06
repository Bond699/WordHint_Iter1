package edu.bu.met.wordhint_iter1;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HintDialog extends Dialog {
    public final double SCALE = .55;
    protected View dialogView;
    private GameModel model;

    public HintDialog(Context context, GameModel model) {
        super(context);
        this.model = model;

        // Setup the dialog box and the view
        dialogView = getLayoutInflater().inflate(R.layout.activity_hint, null);
        setContentView(dialogView);

        // Determine screen size to determine size of the dialog box
        DisplayMetrics dm = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = (int)(dm.heightPixels * SCALE);

        // Make Hint buttons gray if they can't be used.
        if (model.io.loadRemoveHint()) {
            Button hintRemove = (Button) dialogView.findViewById(R.id.hint_remove);
            hintRemove.setBackgroundResource(R.drawable.hint_button_grey);
        }

        if (getBlankLetters().size() < 2) {
            Button hintRemove = (Button) dialogView.findViewById(R.id.hint_reveal);
            hintRemove.setBackgroundResource(R.drawable.hint_button_grey);
        }

        getWindow().setLayout(width, height);
        show();

        // Assign on click listeners and handlers for the two Hint buttons
        onHintRevealClick();
        onHintRemoveClick();
    }

    // REVEAL button functionality
    private void onHintRevealClick() {
        Button hintReveal = (Button) dialogView.findViewById(R.id.hint_reveal);
        hintReveal.setSoundEffectsEnabled(false);
        hintReveal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Get a list of blank letters in the solution area
                List<GameButton> blankLetters = getBlankLetters();

                // Don't allow reveal to be used with up to N-2 letters remaining
                if (blankLetters.size() < 2) {
                    dismiss();
                    return;
                }

                // Find a random blank letter to reveal in the list of blank Solution Letters.
                GameButton reveal = blankLetters.get(new Random().nextInt(blankLetters.size()));

                // Find a button in the pool area with the same alphabetical letter
                GameButton gb = findButtonFromLetter(reveal.getLetter());
                gb.button.setVisibility(View.INVISIBLE);
                reveal.button.setBackgroundResource(R.drawable.pool_button);
                reveal.button.setTextColor(getContext().getResources().getColor(R.color.white_letter));
                reveal.button.setText(gb.button.getText());
                reveal.button.setClickable(false); // User can't remove this letter

                deductHintCost();
                // Save the puzzle state so if user doesn't finish puzzle now, they  still see hint
                model.io.savePoolButtons();
                model.io.saveSolutionButtons();
                model.io.saveAddedToPool();
                model.io.saveHintPuzzle(model.io.getCurrentPuzzle());
                Sound.playHint(getContext(), model);
                dismiss();
            }
        });
    }

    // REMOVE button functionality
    private void onHintRemoveClick() {
        Button hintRemove = (Button) dialogView.findViewById(R.id.hint_remove);
        hintRemove.setSoundEffectsEnabled(false);
        hintRemove.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (model.io.loadRemoveHint()) { // Don't allow Remove all to run twice
                    dismiss();
                    return;
                }
                model.io.saveRemoveHint(true);
                model.io.saveHintPuzzle(model.io.getCurrentPuzzle());

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
                model.io.saveRemoveHint(true);
                Sound.playHint(getContext(), model);
                dismiss();
            }
        });
    }


    private void deductHintCost() {
        //Update model
        if (model.io.getStars() > 0 ) {
            model.io.setStars(model.io.getStars() - 1);
            //updateStarsView();
        }
    }

    // Find a button in the pool area based on a String letter (A, B, C, etc.)
    private GameButton findButtonFromLetter(String letter) {

        //Loop through each letter in the solution word.
        for (int i = 0; i < model.currentPuzzle.getSolution().length(); i++) {
            // Do nothing to blank buttons
            if (model.solutionButtons.get(i).button.getText().equals("")) {
                continue;
            }

            // If there's a wrong letter in the solution, send the button back into pool
            if (!model.solutionButtons.get(i).button.getText().toString().equals(
                    Character.toString(model.currentPuzzle.getSolution().charAt(i)))) {
                //Character.toString(model.currentPuzzle.getSolution().charAt(i))));
                GameButton gb = model.solutionButtons.get(i);
                gb.removeButton(gb.button);
            }
        }

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
}