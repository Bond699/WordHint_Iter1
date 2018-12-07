package edu.bu.met.wordhint_iter1;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.Serializable;


// Concrete class used by the ButtonFactory
public class BlankButton extends GameButton {

    private LinearLayout wordAreaLayout;

    public BlankButton(final MainGameActivity activity, int id, final GameModel model, String letter) {
        super(activity, id, model, letter);

        wordAreaLayout = activity.findViewById(R.id.word_area);
        button.setSoundEffectsEnabled(false);
        button.setText("");
        button.setId(ID_OFFSET + id);
        button.setBackgroundResource(R.drawable.blank_button);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(width, height);
        p.setMargins(LETTERMARGIN, 1, LETTERMARGIN, 1);
        button.setLayoutParams(p);

        //Assigned during constructor of BlankButton
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sound.playClick(activity, model);
                removeButton(button);
            }
        });
        wordAreaLayout.addView(button);
    }

    public void removeButton(Button callingButton) {
        // Don't allow clicking on blank buttons or buttons that have been revealed in a hint
        if (callingButton.getText().equals("") || !callingButton.isClickable()) {
            return;
        }

        // Update View, which also updates the model. We're transforming a green letter button
        // (callingbutton) in the solution into a blank letter. We're then finding the
        // first "empty" (invisible) pool button and making it visible and defining it's letter as
        // what was in the solution area. This gives the illusion that the letter disappears from
        // the solution area and returns to the pool area.
        for (GameButton gb : model.poolButtons) {
            if (gb.button.getVisibility() == View.INVISIBLE) {
                gb.button.setText(callingButton.getText());
                gb.button.setVisibility(View.VISIBLE);
                callingButton.setBackgroundResource(R.drawable.blank_button);
                callingButton.setText("");
                return;
            }
        }
    }
}