package com.example.joy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button camera, collage , gallery, filters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        camera = (Button) findViewById(R.id.CameraStart);
        collage = (Button) findViewById(R.id.Collage);
        gallery = (Button) findViewById(R.id.Gallery);
        filters = (Button) findViewById(R.id.Filter);


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    openCamera();
            }
        });
        collage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCollage();
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        filters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {openFilters();}
        });
    }

    // function for opening the CameraActivity - > CameraStartP
    public void openCamera(){
        Intent cam = new Intent(this,CameraStartP.class);
        startActivity(cam);
    }
    public void openGallery(){
        Intent gallery = new Intent(this,Gallery.class);
        startActivity(gallery);
    }
    public void openCollage(){
        Intent collage = new Intent(this,Collage.class);
        startActivity(collage);
    }
    public void openFilters(){
        Intent imageChanger = new Intent(this, ImageChanger.class);
        startActivity(imageChanger);
    }
}
