package com.example.joy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class Collage_Maker extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collage__maker);

        final ImageView imageView = (ImageView) findViewById(R.id.Collage_Maker);
        Intent intent = getIntent();
        byte[] bytes = intent.getByteArrayExtra("Image");
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        imageView.setImageBitmap(bitmap);
    }
}
