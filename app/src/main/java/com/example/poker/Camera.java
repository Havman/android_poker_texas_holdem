package com.example.poker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;

public class Camera {
    public static final int REQUEST_IMAGE = 1;
    Activity activity;

    public Camera(Context context){
        activity = (Activity) context;
    }

    public void takePicture(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(intent, REQUEST_IMAGE);

    }
}
