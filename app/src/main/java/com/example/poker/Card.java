package com.example.poker;

import android.util.Log;

import java.util.Random;

public class Card {

    private Integer figure;
    private Character suit;
    private String image;

    public Card(){
        figure = 1;
        suit = 'A';
    }

    public Card(int _figure, char _suit){
        figure = _figure;
        suit = _suit;
        image = "card_" + (4*(_figure-1) + ((int)suit - 64));
    }

    public int getFigure() { return this.figure; }
    public String getImage() {
        return this.image;
    }

    private enum figures {
        ACE(1), TWO(2), THREE(3),
        FOUR(4), FIVE(5), SIX(6),
        SEVEN(7), EIGHT(8), NINE(9),
        TEN(10), JACK(11), QUEEN(12), KING(13);

        int figureId;
        private figures(int id){ figureId = id; }
    }
    enum suits {
        SPADES('A'), HEARTS('B'), DIAMONDS('C'), CLUBS('D');
        char suitID;
        private suits(char id) { suitID = id; }
    }

    public static final Card[] getDeck(){
        Card[] deck = new Card[52];

        for (int i = 1; i<14; i++) {
            deck[4*(i-1)] = new Card(i, 'A');
            deck[4*(i-1)+1] = new Card(i, 'B');
            deck[4*(i-1)+2] = new Card(i, 'C');
            deck[4*(i-1)+3] = new Card(i, 'D');
        }
        return deck;
    }

     public final Card getRandomCard(){
         Random random = new Random();
         int figure = random.nextInt(13) + 1;
         int suit = (random.nextInt(3) + 65);
         return new Card(figure, (char)suit);
     }

     public final Card getNextCard() {
         int nextFigure = this.figure;

         int nextSuit = ((int)this.suit) + 1;
         if (this.suit == 'D')  {
             nextFigure = this.figure + 1;
             nextSuit = 65;
         }

         if (this.figure == 14) nextFigure = 1;

         int nr = (Integer.parseInt(this.image.substring(5)) + 1)%53;
         if (nr == 0) nr = 1;
         String nextImage = "card_" + nr;
         Log.d("getNextCard - fig", "" + nextFigure);
         Log.d("getNextCard - sut", "" + nextSuit);
         Log.d("getNextCard - image", nextImage);

         this.figure = nextFigure;
         this.suit = (char)nextSuit;
         this.image = nextImage;
         return this;
     }

     @Override
     public boolean equals(Object object){
        if(this == object)
            return true;
        else if(object == null)
            return false;
        Card otherCard = (Card)object;
        return (this.figure.equals(otherCard.figure) && this.suit.equals(otherCard.suit));
     }

     @Override
     @SuppressWarnings("NullableProblems")
     public String toString() {
        return "\nFigure: " + this.figure + " Suit: " + this.suit + " Image: " + this.image;
     }
}
