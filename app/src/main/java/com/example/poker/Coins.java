package com.example.poker;

public class Coins {

    private int balance;

    public Coins (int startCoins){
        balance = startCoins;
    }

    public void updateBalance(int outcomeCoins){
        balance += outcomeCoins;
    }
}
