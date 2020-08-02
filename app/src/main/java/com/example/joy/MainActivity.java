package com.example.joy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button camera, collage , gallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camera = (Button) findViewById(R.id.CameraStart);
        collage = (Button) findViewById(R.id.Collage);
        gallery = (Button) findViewById(R.id.Gallery);


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    openCamera();
            }
        });
    }

    // function for opening the CameraActivity - > CameraStartP
    public void openCamera(){
        Intent cam = new Intent(this,CameraStartP.class);
        startActivity(cam);
    }
}
