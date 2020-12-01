package com.example.poker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Deck {

    private List<Card> deck;
    private int numOfCards = 52;
    private List<Card[]> hands = new ArrayList<>();

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

    public Card pullCard() {
        numOfCards--;
        Card randomCard = this.deck.get(numOfCards);
//        deck.remove(numOfCards);
        return randomCard;
    }

    public Card[] getHand() {
        Card[] hand = new Card[2];
        hand[0] = pullCard();
        hand[1] = pullCard();
        hands.add(hand);
        return hand;
    }

    public Card[] getCommunityCards() {
        Card[] communityCards = new Card[5];
        communityCards[0] = pullCard();
        communityCards[1] = pullCard();
        communityCards[2] = pullCard();
        communityCards[3] = pullCard();
        communityCards[4] = pullCard();
        return communityCards;
    }
}
