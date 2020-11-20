package com.example.poker;

import android.util.Log;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Deck {

    private List<Card> deck;
    private int numOfCards = 52;

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
        Log.e("DECK: ", deck.toString());
    }

    public Card dealCard() {
        numOfCards--;
        Card randomCard = this.deck.get(numOfCards);
//        deck.remove(numOfCards);
        return randomCard;
    }
}
