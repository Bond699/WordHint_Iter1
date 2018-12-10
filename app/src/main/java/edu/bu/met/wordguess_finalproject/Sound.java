package edu.bu.met.wordguess_finalproject;

import android.content.Context;
import android.media.MediaPlayer;

public class Sound {

    public static void playClick(Context context) {
        if (GameModel.getInstance(context).io.getSound())
        MediaPlayer.create(context, R.raw.click).start();
    }

    public static void playIncorrect(Context context) {
        if (GameModel.getInstance(context).io.getSound())
            MediaPlayer.create(context, R.raw.puzzle_incorrect).start();
    }

    public static void playCorrect(Context context) {
        if (GameModel.getInstance(context).io.getSound())
        MediaPlayer.create(context, R.raw.puzzle_correct).start();
    }

    public static void playStart(Context context) {
        if (GameModel.getInstance(context).io.getSound())
            MediaPlayer.create(context, R.raw.puzzle_start).start();
    }

    public static void playHint(Context context) {
        if (GameModel.getInstance(context).io.getSound())
            MediaPlayer.create(context, R.raw.puzzle_hint).start();
    }

}
