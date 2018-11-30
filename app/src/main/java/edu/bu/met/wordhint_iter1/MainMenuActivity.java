package edu.bu.met.wordhint_iter1;

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
        this.model = new GameModel(this);
    }

    @Override
    public void onResume()
    {   // Executes at starup or pause (like when the user clicks to go to a puzzle, and then clicks
        // the back button on the device. This keeps the Main Menu level in sync.
        super.onResume();
        TextView tv = findViewById(R.id.main_menu_level);
        tv.setText(getResources().getString(R.string.level_level) + model.getCurrentPuzzle());
    }

    public void onClickLevel(View view) {
        Intent intent = new Intent(this, LevelChooseActivity.class);
        startActivity(intent);
    }

    public void onClickPlay(View view) {
        Intent intent = new Intent(this, MainGameActivity.class);
        intent.putExtra("puzzle", model.getCurrentPuzzle());
        startActivity(intent);
    }

}
