package edu.bu.met.wordguess_finalproject;

import android.content.SharedPreferences;
import android.content.Context;
import android.view.View;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class InputOutput {
    private final String PUZZLE_FILENAME = "puzzles.xml";
    private final String MY_PREF = "userdata";
    private final String STAR_KEY = "stars";
    private final String LEVEL_KEY = "currentPuzzle";
    private final String HIGHEST_KEY = "highestPuzzle";
    private final String SOLUTION_KEY = "solutionButtons";
    private final String POOL_KEY = "poolButtons";
    private final String ADDED_TO_POOL_KEY = "addedToPool";
    private final String REMOVE_HINT_KEY = "removeHint";
    private final String HINT_PUZZLE_KEY = "hintPuzzle";
    private final String SOUND_KEY = "sound";

    protected List<Puzzle> puzzles;
    private GameModel model;
    protected SharedPreferences sharedPref;
    protected SharedPreferences.Editor editor;
    private Context context;

    public InputOutput(Context context, GameModel model) {
        this.puzzles = new ArrayList<>();
        this.context = context;
        this.model = model;
        this.sharedPref = context.getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);
        this.editor = sharedPref.edit();

        if (!sharedPref.contains(STAR_KEY)) { setStars(0); }
        if (!sharedPref.contains(LEVEL_KEY)) { setCurrentPuzzle(1); }
        if (!sharedPref.contains(HIGHEST_KEY)) { setHighestPuzzle(1); }
        if (!sharedPref.contains(REMOVE_HINT_KEY)) { saveRemoveHint(false); }
        if (!sharedPref.contains(HINT_PUZZLE_KEY)) { saveHintPuzzle(1); }
        if (!sharedPref.contains(SOUND_KEY)) { setSound(true); }
        puzzleParser();
    }

    public void savePoolButtons() {
        StringBuilder sb = new StringBuilder();
        for (GameButton gb : model.poolButtons) {
            sb.append(gb.button.getText());
            if (gb.button.getVisibility() == View.VISIBLE) {
                sb.append("V");
            }
            if (gb.button.getVisibility() == View.INVISIBLE) {
                sb.append("I");
            }
            sb.append(",");
        }
        sb.setLength(sb.length() - 1);
        editor.putString(POOL_KEY, (sb.toString()));
        editor.commit();
    }

    public void saveSolutionButtons() {
        StringBuilder sb = new StringBuilder();
        for (GameButton gb : model.solutionButtons) {
            if (gb.button.getText().equals("")) {
                sb.append("_");
            }
            else {
                sb.append(gb.button.getText());
            }
            //sb.append(gb.button.getText());
            if (gb.button.isClickable()) {
                sb.append("C");
            }
            else {
                sb.append("N");
            }
            sb.append(",");
        }
        sb.setLength(sb.length() - 1);
        editor.putString(SOLUTION_KEY, (sb.toString()));
        editor.commit();
    }

    public void saveAddedToPool() {
        StringBuilder sb = new StringBuilder();
        for (String letter : model.addedToPool) {
            sb.append(letter);
            sb.append(",");
        }
        sb.setLength(sb.length() - 1);
        editor.putString(ADDED_TO_POOL_KEY, (sb.toString()));
        editor.commit();
    }

    public void saveRemoveHint(Boolean status) {
        editor.putBoolean(REMOVE_HINT_KEY, status);
        editor.commit();
    }

    public String loadPoolButtons() {
        return sharedPref.getString(POOL_KEY, "");
    }

    public String loadSolutionButtons() {
        return sharedPref.getString(SOLUTION_KEY, "");
    }

    public String loadAddedToPool() {
        return sharedPref.getString(ADDED_TO_POOL_KEY, "");
    }

    public Boolean loadRemoveHint() {
        return sharedPref.getBoolean(REMOVE_HINT_KEY, false);
    }

    public void resetPuzzlePrefs() {
        editor.remove(REMOVE_HINT_KEY);
        editor.remove(POOL_KEY);
        editor.remove(SOLUTION_KEY);
        editor.remove(ADDED_TO_POOL_KEY);
        editor.commit();
    }

    public int getCurrentPuzzle() {
        return sharedPref.getInt(LEVEL_KEY, -1);
    }

    public void setCurrentPuzzle(int current) {
        editor.putInt(LEVEL_KEY, current);
        editor.commit();
    }

    public int getHighestPuzzle() {
        return sharedPref.getInt(HIGHEST_KEY, -1);
    }

    public void setHighestPuzzle(int highest) {
        editor.putInt(HIGHEST_KEY, highest);
        editor.commit();
    }

    public int getStars() {
        return sharedPref.getInt(STAR_KEY, -1);
    }

    public void setSound(Boolean sound) {
        editor.putBoolean(SOUND_KEY, sound);
        editor.commit();
    }

    public Boolean getSound() {
        return sharedPref.getBoolean(SOUND_KEY, true);
    }

    public void setStars(int stars) {
        //this.stars = stars;
        editor.putInt(STAR_KEY, stars);
        editor.commit();
    }

    public void addStars(int stars) {
        editor.putInt(STAR_KEY, (getStars() + stars));
        editor.commit();
    }


    public int getHintPuzzle() {
        return sharedPref.getInt(HINT_PUZZLE_KEY, -1);
    }

    public void saveHintPuzzle(int hintPuzzle) {
        editor.putInt(HINT_PUZZLE_KEY, hintPuzzle);
        editor.commit();
    }

    // Adapted from: https://www.tutorialspoint.com/android/android_xml_parsers.htm
    private void puzzleParser() {
        try {
            InputStream inputStream = context.getAssets().open(PUZZLE_FILENAME);
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = docFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputStream);

            Element element=doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("puzzle"); // puzzle tag in xml

            for (int i=0; i < nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) node;
                    // corresponds to tags in xml: id, solution, image
                    Puzzle puzzle = new Puzzle(Integer.parseInt(getValue("id", elem)),
                            getValue("solution", elem), getValue("image", elem));
                    puzzles.add(puzzle);
                }
            }

        } catch (Exception ex) {ex.printStackTrace();}
    }

    private String getValue(String tag, Element element) {
        NodeList nList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node n = nList.item(0);
        return n.getNodeValue();
    }

}
