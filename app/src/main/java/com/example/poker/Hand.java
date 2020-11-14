//package com.example.poker;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import java.lang.reflect.Array;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.Iterator;
//import java.util.List;
//import java.util.ListIterator;
//
//public class Hand {
//
//    private Card hand[];
//
//    public void initialize(Card [] cards){
//        hand = cards;
//    }
//
//    public final static int rankHand(Hand hand) {
//
//        Card card1 = new Card(1,1, 'card_1');
//
//        Card cardArray [] = {new Card(), new Card(), new Card(), new Card(), new Card()};
//        int figures [] =
//
//        boolean[] hands = new boolean[NUM_HANDS];
//        boolean foundHand = false;
//        int[] ranks = new int[Card.NUM_RANKS];
//        int[] suits = new int[Card.NUM_SUITS];
//        int[] kickers = {Card.BAD_CARD, Card.BAD_CARD, Card.BAD_CARD,
//                Card.BAD_CARD, Card.BAD_CARD};
//
//        // Parse with one pass and update ranks and suits registers
//        for(int i = 0; i < hand.getNumOfCards(); i++) {
//            int index = hand.getCardIndex(i);
//            ranks[Card.getRank(index)]++;
//            suits[Card.getSuit(index)]++;
//        }
//
//        // Check for flush
//        int flushSuit = -1;
//        for(int i = 0; i < Card.NUM_SUITS; i++) {
//            if(suits[i] >= 5) {
//                hands[FLUSH] = true;
//                foundHand = true;
//                flushSuit = i;
//                break;
//            }
//        }
//        if(hands[FLUSH]) {
//            // Add kickers
//            for(int i = 0; i < hand.getNumOfCards(); i++) {
//                int index = hand.getCardIndex(i);
//                int cardRank = Card.getRank(index);
//                int cardSuit = Card.getSuit(index);
//                if(cardSuit == flushSuit) {
//                    for(int j = 0; j < kickers.length; j++) {
//                        if(kickers[j] > cardRank) {
//                            continue;
//                        } else {
//                            // Shift
//                            for(int k = (kickers.length-2); k >= j; k--) {
//                                kickers[k + 1] = kickers[k];
//                            }
//                            kickers[j] = cardRank;
//                            break;
//                        }
//                    }
//                }
//            }
//        }
//
//        // Check for straight
//        int consecutive = (ranks[Card.ACE] > 0) ? 1 : 0;
//        int highestRank = -1;
//        for(int i = 0; i < Card.NUM_RANKS; i++) {
//            if(ranks[i] > 0) {
//                consecutive++;
//                if(consecutive >= 5) {
//                    hands[STRAIGHT] = true;
//                    foundHand = true;
//                    highestRank = i;
//                }
//            } else {
//                consecutive = 0;
//            }
//        }
//
//        // Check for straight-flush
//        if(hands[FLUSH] && hands[STRAIGHT]) {
//            int counter = 0;
//            for(int i = 0; i < hand.getNumOfCards(); i++) {
//                int index = hand.getCardIndex(i);
//                int cardRank = Card.getRank(index);
//                int cardSuit = Card.getSuit(index);
//                if(cardSuit == flushSuit) {
//                    if(highestRank == 3 && cardRank == Card.ACE) {
//                        counter++;
//                        continue;
//                    }
//                    if((cardRank <= highestRank) && cardRank >= (highestRank - 4)) {
//                        counter++;
//                    }
//                }
//            }
//            if(counter >= 5) {
//                hands[STRAIGHT_FLUSH] = true;
//                foundHand = true;
//            }
//        }
//
//        // Check for full house, four cards, three cards, two pairs or one pair
//        int pairOne = -1;
//        int pairTwo = -1;
//        int trips = -1;
//        int quads = -1;
//        int kickerOne = -1;
//        int kickerTwo = -1;
//        int kickerThree = -1;
//        for(int i = 0; i < Card.NUM_RANKS; i++) {
//            if(ranks[i] > 1) {
//                if(ranks[i] == 4) {
//                    hands[FOUR] = true;
//                    foundHand = true;
//                    quads = i;
//                    break;
//                } else if(ranks[i] == 3) {
//                    trips = i;
//                } else {
//                    if(pairOne == -1) {
//                        pairOne = i;
//                    } else {
//                        pairTwo = pairOne;
//                        pairOne = i;
//                    }
//                }
//            } else if(ranks[i] == 1) {
//                if(kickerOne < i) {
//                    kickerThree = kickerTwo;
//                    kickerTwo = kickerOne;
//                    kickerOne = i;
//                } else if(kickerTwo < i) {
//                    kickerThree = kickerTwo;
//                    kickerTwo = i;
//                } else if(kickerThree < i) {
//                    kickerThree = i;
//                }
//            }
//        }
//        if(trips != -1 && pairOne != -1) {
//            hands[FULL_HOUSE] = true;
//            foundHand = true;
//        } else if(trips != -1 && pairOne == -1) {
//            hands[TRIPS] = true;
//            foundHand = true;
//        } else if(pairOne != -1 && pairTwo != -1) {
//            hands[TWO_PAIRS] = true;
//            foundHand = true;
//        } else if(pairOne != -1) {
//            hands[PAIR] = true;
//            foundHand = true;
//        }
//
//        if(!foundHand) {
//            // High card
//            hands[HIGH_CARD] = true;
//            // Add kickers
//            for(int i = 0; i < hand.getNumOfCards(); i++) {
//                int index = hand.getCardIndex(i);
//                int cardRank = Card.getRank(index);
//                for(int j = 0; j < kickers.length; j++) {
//                    if(kickers[j] >= cardRank) {
//                        continue;
//                    } else {
//                        // Shift
//                        for(int k = (kickers.length - 2); k >= j; k--) {
//                            kickers[k + 1] = kickers[k];
//                        }
//                        kickers[j] = cardRank;
//                        break;
//                    }
//                }
//            }
//        }
//
//        // Calculate rank
//        int rank = -1;
//        if(hands[STRAIGHT_FLUSH]) { // STRAIGHT FLUSH
//            rank = STRAIGHT_FLUSH * SHIFT;
//            rank += highestRank;
//        } else if(hands[FOUR]) { // FOUR OF A KIND
//            rank = FOUR * SHIFT;
//            rank += quads;
//        } else if(hands[FULL_HOUSE]) { // FULL HOUSE
//            rank = FULL_HOUSE * SHIFT;
//            rank += trips * Card.NUM_RANKS;
//            rank += pairOne;
//        } else if(hands[FLUSH]) { // FLUSH
//            rank = FLUSH * SHIFT;
//            int power = 1;
//            for(int i = (kickers.length-1); i >= 0; i--) {
//                rank += kickers[i] * power;
//                power *= Card.NUM_RANKS;
//            }
//        } else if(hands[STRAIGHT]) { //STRAIGHT
//            rank = STRAIGHT * SHIFT;
//            rank += highestRank;
//        } else if(hands[TRIPS]) { // TRIPS
//            rank = TRIPS * SHIFT;
//            rank += trips * Card.NUM_RANKS * Card.NUM_RANKS;
//            rank += kickerOne * Card.NUM_RANKS;
//            rank += kickerTwo;
//        } else if(hands[TWO_PAIRS]) { // TWO PAIRS
//            rank = TWO_PAIRS * SHIFT;
//            rank += pairOne * Card.NUM_RANKS * Card.NUM_RANKS;
//            rank += pairTwo * Card.NUM_RANKS;
//            rank += kickerOne;
//        } else if(hands[PAIR]) { // ONE PAIR
//            rank = PAIR * SHIFT;
//            rank += pairOne * Card.NUM_RANKS * Card.NUM_RANKS * Card.NUM_RANKS;
//            rank += kickerOne * Card.NUM_RANKS * Card.NUM_RANKS;
//            rank += kickerTwo * Card.NUM_RANKS;
//            rank += kickerThree;
//        } else if(hands[HIGH_CARD]) { // HIGH CARD
//            rank = HIGH_CARD * SHIFT;
//            int power = 1;
//            for(int i = (kickers.length-1); i >= 0; i--) {
//                rank += kickers[i] * power;
//                power *= Card.NUM_RANKS;
//            }
//        }
//        return rank;
//    }
//
//}
