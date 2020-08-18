package com.example.joy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageChanger extends AppCompatActivity {
    ImageView imageView;
    Button btnsel, changeColor;
    SeekBar mSaturationSeekbar;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_changer);

        imageView = findViewById(R.id.image_view);
        btnsel = findViewById(R.id.btnSelect);
        changeColor = (Button) findViewById(R.id.ColorFilter);

        mSaturationSeekbar = (SeekBar) findViewById(R.id.seekBarSaturation);
        mSaturationSeekbar.setOnSeekBarChangeListener(seekBarChangeListener);

        btnsel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageFromGallery();
            }
        });


        changeColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeColorByFilter();
            }
        });

    }

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            loadSaturationBitmap();
        }

    };


    private void pickImageFromGallery(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            List<Bitmap> bitmaps = new ArrayList<>();

            Uri imageUri = data.getData();

            try {

                InputStream is = getContentResolver().openInputStream(imageUri);
                bitmap = BitmapFactory.decodeStream(is);
                bitmaps.add(bitmap);
                imageView.setImageBitmap(bitmap);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    private void loadSaturationBitmap() {
        // TODO Auto-generated method stub
        if (bitmap != null) {

            int progressSat = mSaturationSeekbar.getProgress();

            // Saturation, 0=gray-scale. 1=identity
            float saturation = (float) progressSat / 256;

            imageView.setImageBitmap(updateSaturation(bitmap,
                    saturation));
        }
    }


    private Bitmap updateSaturation(Bitmap src, float settingSat) {

        int width = src.getWidth();
        int height = src.getHeight();

        Bitmap bitmapResult = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvasResult = new Canvas(bitmapResult);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(settingSat);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(filter);
        canvasResult.drawBitmap(src, 0, 0, paint);

        return bitmapResult;
    }

    private void changeColorByFilter(){
        mSaturationSeekbar.setVisibility((mSaturationSeekbar.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE);
    }
}


