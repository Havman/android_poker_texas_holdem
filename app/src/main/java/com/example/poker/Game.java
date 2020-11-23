package com.example.poker;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class Game extends AppCompatActivity {

    private Integer numOfPlayers;
    private Button dealCardBtn;
    private NSDDiscover NSDDiscover;
    private NSDListen NSDListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        if (b != null){
            NSDDiscover = (NSDDiscover) b.get("discover");
            NSDListener = (NSDListen) b.get("listener");
//            numOfPlayers = b.getInt("numOfPlayers");
        }
        setContentView(R.layout.game_layout);

        dealCardBtn = findViewById(R.id.dealCards);
        dealCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                NSDDiscover.sayHello();
            }
        });

    }
}
