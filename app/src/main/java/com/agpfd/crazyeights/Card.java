package com.agpfd.crazyeights;

import android.graphics.Bitmap;

/**
 * @author <a mailto="jacobibanez@jacobibanez.com">Jacob Ibáñez Sánchez</a>
 * @since 24/07/2016
 */
public class Card {

    private int id;
    private int suit;
    private int rank;
    private int scoreValue;
    private Bitmap bmp;

    public Card(int newId) {
        id = newId;
        suit = Math.round((id / 100) * 100);
        rank = id - suit;
        if (rank == 8) {
            scoreValue = 50;
        } else if (rank == 14) {
            scoreValue = 1;
        } else if (rank > 9 && rank < 14) {
            scoreValue = 10;
        } else {
            scoreValue = rank;
        }
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }

    public Bitmap getBmp() {
        return bmp;
    }

    public int getId() {
        return id;
    }

    public int getSuit() {
        return suit;
    }

    public int getRank() {
        return rank;
    }

    public int getScoreValue() {
        return scoreValue;
    }
}
