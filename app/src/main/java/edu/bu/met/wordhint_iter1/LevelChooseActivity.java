package edu.bu.met.wordhint_iter1;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LevelChooseActivity extends AppCompatActivity {
    private GameModel model;
    private Typeface font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_choose);
        this.model = GameModel.getInstance(this);
        this.font = ResourcesCompat.getFont(this, R.font.hug_me_tight);
        populateLevels();
    }

    public void populateLevels() {
        for (int i = 0; i < model.io.puzzles.size(); i++) {
            if (i < model.io.getHighestPuzzle()) {
                createTextView(i + 1, true);
            }
            else {
                createTextView(i + 1, false);
            }
        }
    }

    // Textview buttons
    public void createTextView(final int level, Boolean greenButton) {
        LinearLayout ll = (LinearLayout)findViewById(R.id.level_layout);
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        convertPixelsToDp(50));
        layoutParams.setMargins(convertPixelsToDp(40), convertPixelsToDp(10),
                convertPixelsToDp(40), 0);

        TextView tv = new TextView(this);
        tv.setSoundEffectsEnabled(false);
        if (greenButton) {
            tv.setBackgroundResource(R.drawable.level_button);
            tv.setText(getResources().getString(R.string.level_level) + level);
        }
        else {
            tv.setBackgroundResource(R.drawable.level_button_grey);
            tv.setText(getResources().getString(R.string.level_level) + level +
                    getResources().getString(R.string.level_locked));
        }

        tv.setTextColor(getResources().getColor(R.color.white_letter));
        tv.setTypeface(font);
        tv.setTextSize(convertPixelsToDp(9));
        tv.setGravity(Gravity.CENTER);
        ll.addView(tv, layoutParams);

        View.OnClickListener onclicklistener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainGameActivity.class);
                intent.putExtra("puzzle", level);
                startActivity(intent);
                }
        };
        if (greenButton) {
            tv.setOnClickListener(onclicklistener);
        }

    }

    public int convertPixelsToDp(int pixels) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (pixels * scale + 0.5f);
    }

}
