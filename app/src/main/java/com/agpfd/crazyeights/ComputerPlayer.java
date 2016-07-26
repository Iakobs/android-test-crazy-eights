package com.agpfd.crazyeights;

import java.util.List;

/**
 * @author <a mailto="jacobibanez@jacobibanez.com">Jacob Ibáñez Sánchez</a>
 * @since 24/07/2016
 */
public class ComputerPlayer {
    public int makePlay (List<Card> hand, int suit, int rank) {
        int play = 0;
        for (int i = 0; i < hand.size(); i++) {
            int tempId = hand.get(i).getId();
            int tempRank = hand.get(i).getRank();
            int tempSuit = hand.get(i).getSuit();
            if (rank == 8) {
                if (suit == tempSuit) {
                    play = tempId;
                }
            } else if (suit == tempSuit || rank == tempRank ||
                    tempId == 108 || tempId == 208 || tempId == 308 || tempId == 408) {
                play = tempId;
            }
        }
        return play;
    }

    public int chooseSuit (List<Card> hand) {
        int suit = 100;
        return suit;
    }
}