package edu.bu.met.wordguess_finalproject;

public class Puzzle {
    private int id;
    private String solution;
    private String image;

    public Puzzle(int id, String solution, String image) {
        this.id = id;
        this.solution = solution;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getSolution() {
        return solution;
    }

    public String getImage() {
        return image;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public void setImage(String image) {
        this.image = image;
    }


}
