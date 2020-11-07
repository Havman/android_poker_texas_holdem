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
        image = "card_1";
    }

    public Card(int _figure, char _suit, String _image){
        figure = _figure;
        suit = _suit;
        image = _image;
    }

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

    public final Card[] getDeck(){
        Card[] deck = new Card[52];

        for (int i = 1; i<14; i++) {
            deck[4*(i-1)] = new Card(i, 'A', "card_" + (4*(i-1)));
            deck[4*(i-1)+1] = new Card(i, 'B', "card_" + (4*(i-1)+1));
            deck[4*(i-1)+2] = new Card(i, 'C', "card_" + (4*(i-1)+2));
            deck[4*(i-1)+3] = new Card(i, 'D', "card_" + (4*(i-1)+3));
        }
        return deck;
    }

     public final Card getRandomCard(){
         Random random = new Random();
         int figure = random.nextInt(13) + 1;
         int suit = (random.nextInt(4) + 1);
         return new Card(figure, (char)suit, "card_" + (4*(figure-1)+suit));
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
         Log.d("getNextCard - sut", "" + (char)nextSuit);
         Log.d("getNextCard - image", nextImage);

         this.figure = nextFigure;
         this.suit = (char)nextSuit;
         this.image = nextImage;
         return this;
     }

    @Override
    @SuppressWarnings("NullableProblems")
    public String toString() {
        return "Figure: " + this.figure + " Suit: " + this.suit + " Image: " + this.image;
    }
}
