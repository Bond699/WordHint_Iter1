package edu.bu.met.wordhint_iter1;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.io.Serializable;

// Concrete class used by the ButtonFactory
public class LetterButton extends GameButton {

    private RelativeLayout poolAreaLayout;
    private int idCount;

    public LetterButton(MainGameActivity activity, int id, GameModel model, String letter) {
        super(activity, id, model, letter);
        poolAreaLayout = activity.findViewById(R.id.pool_area);
        idCount = poolAreaLayout.getId() + ID_OFFSET;
        button.setText("" + model.pool.get(id));
        button.setBackgroundResource(R.drawable.pool_button);
        button.setId(idCount + id);
        button.setTextColor(activity.getResources().getColor(R.color.white_letter));

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width,height);
        calcPoolButtonPosition(params, id);
        params.setMargins(LETTERMARGIN, LETTERMARGIN, LETTERMARGIN, LETTERMARGIN);

        button.setLayoutParams(params);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeButton(button);
            }
        });
        poolAreaLayout.addView(button);
    }

    public void calcPoolButtonPosition(RelativeLayout.LayoutParams p, int id) {
        // Refactor this mess. Controls the line and relative position of each letter.

        // First letter
        if (id == 0) {
            p.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            p.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            return;
        }

        // Midpoint letter
        if (id == (model.pool.size() / 2)) {
            p.addRule(RelativeLayout.BELOW, poolAreaLayout.getId() + ID_OFFSET);
            return;
        }

        // Letters greater than midpoint
        if (id > (model.pool.size() / 2)) {
            p.addRule(RelativeLayout.BELOW, poolAreaLayout.getId() + ID_OFFSET);
        }

        // All letters not 0 or midpoint
        p.addRule(RelativeLayout.RIGHT_OF, idCount + id - 1);
    }


    public void removeButton(Button callingButton) {
        // See if there's a blank button in the solution area
        Button button = model.findFirstBlankButton();
        if (button == null) {
            return;
        }

        //Update View (and the object is also updated in the model list) by virtue of changing the
        // object since the list contains a list of pointers to objects
        callingButton.setVisibility(View.INVISIBLE);
        button.setBackgroundResource(R.drawable.pool_button);
        button.setTextColor(activity.getResources().getColor(R.color.white_letter));
        button.setText(callingButton.getText());

        if (checkSolutionFull()) {  // Max number of letters in solution?
            if ( model.checkSolution()) {
                activity.puzzleSuccess();
            }
            else {
                // Idea from here: http://droid-blog.net/2012/05/15/two-simple-ways-to-make-your-users-aware-of-incorrect-input/
                Animation shake = AnimationUtils.loadAnimation(poolAreaLayout.getContext(),
                        R.anim.wrong_answer);
                for (GameButton gb : model.solutionButtons) {
                    gb.button.startAnimation(shake);
                }
            }
        }
    }

    private Boolean checkSolutionFull() {
        for (GameButton gb : model.solutionButtons) {
            if (gb.button.getText().equals("")) {
                return false;
            }
        }
        return true;
    }


}
