package com.example.poker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class Game extends AppCompatActivity {

    private Integer numOfPlayers;
    private Button dealCardBtn;
    private Client client;
    private Server NSDListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        if (b != null){
            client = (Client) b.get("discover");
            NSDListener = (Server) b.get("listener");
//            numOfPlayers = b.getInt("numOfPlayers");
        }
        setContentView(R.layout.game_layout);

        dealCardBtn = findViewById(R.id.startGame);
        dealCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                NSDDiscover.sayHello();
            }
        });

    }
}
