package edu.bu.met.wordguess_finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainMenuActivity extends AppCompatActivity {

    private GameModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        this.model = GameModel.getInstance(getApplicationContext());

        TextView tv = findViewById(R.id.main_menu_sound);
        if (model.io.getSound()) {
            tv.setText(getResources().getString(R.string.sound_on));
        }
        else {
            tv.setText(getResources().getString(R.string.sound_off));
        }
    }

    @Override
    public void onResume()
    {   // Executes at startup or pause (like when the user clicks to go to a puzzle, and then clicks
        // the back button on the device. This keeps the Main Menu level in sync.
        super.onResume();
        TextView tv = findViewById(R.id.main_menu_level);
        tv.setText(getResources().getString(R.string.level_level) + model.io.getCurrentPuzzle());
    }

    public void onClickLevel(View view) {
        Intent intent = new Intent(this, LevelChooseActivity.class);
        startActivity(intent);
    }

    public void onClickPlay(View view) {
        Intent intent = new Intent(this, MainGameActivity.class);
        intent.putExtra("puzzle", model.io.getCurrentPuzzle());
        startActivity(intent);
    }

    public void onClickSound(View view) {
        TextView tv = findViewById(R.id.main_menu_sound);
        if (model.io.getSound()) {
            model.io.setSound(false);
            tv.setText(getResources().getString(R.string.sound_off));
        }
        else {
            model.io.setSound(true);
            tv.setText(getResources().getString(R.string.sound_on));
        }
    }

}
