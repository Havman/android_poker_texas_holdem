package com.example.poker;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Random;

public class Test extends Activity{

    ImageView image;

    Button randomButton;
    Button nextCardButton;
    Button deckButton;

    Card card = new Card(1, 'A');
    Deck deck = new Deck();

    private void initialize() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.test_layout);

        image = findViewById(R.id.rCard);

        randomButton = findViewById(R.id.randomCard);
        nextCardButton = findViewById(R.id.nextCard);
        deckButton = findViewById(R.id.fullDeck);
    }


    public void setImageByCard(final ImageView imgView, final Card card) {
        String img = card.getImage();
        int resID = getResources().getIdentifier(img , "drawable", getPackageName());
        imgView.setImageResource(resID);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialize();

        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card = card.getRandomCard();
                setImageByCard(image, card);
            }
        });

        nextCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card = card.getNextCard();
                setImageByCard(image, card);
            }
        });

        deckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deck.shuffleDeck();
                Log.d("onClick", deck.toString());
            }
        });
    }

}