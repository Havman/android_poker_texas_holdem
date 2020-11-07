package com.example.poker;

import androidx.annotation.Nullable;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends Activity {

    ImageView image;
    Button cameraButton;
    Button cardGalleryButton;
    Button playButton;

    Button testButton;

    Camera camera = new Camera(this);

    private void initialize() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        image = findViewById(R.id.imgv);
        cameraButton = findViewById(R.id.cameraButton);
        cardGalleryButton = findViewById(R.id.cardGallery);
        playButton = findViewById(R.id.playButton);
        testButton = findViewById(R.id.testButton);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Camera.REQUEST_IMAGE:
                if (resultCode == RESULT_OK) {
                    Bitmap b = (Bitmap) data.getExtras().get("data");
                    image.setImageBitmap(b);
                } else {
                    showToast("Bad photo");
                }
                break;
        }
    }


    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void goToCardGallery() {
        Intent intent = new Intent(this, CardGallery.class);
        startActivity(intent);
    }

    public void goToRooms() {
        Intent intent = new Intent(this, Rooms.class);
        startActivity(intent);
    }

    public void goToTest(){
        Intent intent = new Intent(this, Test.class);
        startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialize();

        // Button for taking avatar picture with phone camera app
        cameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                camera.takePicture();
            }
        });


        cardGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCardGallery();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRooms();
            }
        });

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToTest();
            }
        });

    }

}