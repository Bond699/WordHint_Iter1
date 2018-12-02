package edu.bu.met.wordhint_iter1;

public class ButtonFactory {

    public void createButtons(MainGameActivity activity, GameModel model) {
        // Word buttons
        for (int i = 0; i < model.word.size(); i++) {
            model.solutionButtons.add(new BlankButton(activity, i, model));
            GameButton button = new BlankButton(activity, i, model);
        }

        // Pool buttons
        for (int i = 0; i < model.pool.size(); i++) {
            model.poolButtons.add(new LetterButton(activity, i, model));
            GameButton button = new LetterButton(activity, i, model);
        }
    }
}

