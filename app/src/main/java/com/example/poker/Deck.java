package com.example.poker;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Deck {

    private List<Card> deck;

    public Deck(){
        deck = Arrays.asList(Card.getDeck());
    }

    @Override
    public String toString(){
        String toReturn = "";
        for (Card card : deck){
            toReturn += card.toString();
        }
        return toReturn;
    }

    public void shuffleDeck(){
        Collections.shuffle(this.deck);
    }

}
