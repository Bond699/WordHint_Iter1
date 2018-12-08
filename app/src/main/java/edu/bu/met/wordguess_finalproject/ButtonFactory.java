package edu.bu.met.wordguess_finalproject;

public class ButtonFactory {

    public void createButtons(MainGameActivity activity, GameModel model) {
        // Word/Solution buttons
        for (int i = 0; i < model.currentPuzzle.getSolution().length(); i++) {
            model.solutionButtons.add(new BlankButton(activity, i, model,
                    Character.toString(model.currentPuzzle.getSolution().charAt(i))));
        }

        // Pool buttons
        for (int i = 0; i < model.pool.size(); i++) {
            model.poolButtons.add(new LetterButton(activity, i, model, ""));
        }
    }
}

