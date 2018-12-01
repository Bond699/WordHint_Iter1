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
    private Button continueButton;
    private RelativeLayout poolAreaLayout;
    private TextView success;
    private Typeface font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);
        //this.model = new GameModel(this);
        this.model = GameModel.getInstance(this);
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
        ButtonFactory buttonFactory = new ButtonFactory();
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

    // public HintDialog(Context context, GameModel model, MainMenuActivity activity, int blankLetters)
    public void onHintClick(View view) {
        // Setup the dialog box and the view
        new HintDialog(this, model);
        updateStarsView();
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
