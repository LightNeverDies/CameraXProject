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
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageChanger extends AppCompatActivity {
    ImageView imageView;
    Button btnsel, filterOptions, reset, contrast, exit ,colorRGB, saveImage;
    SeekBar mSaturationSeekbar , mRotateSeekbar;

    RelativeLayout MainRelativeLayout;
    Bitmap bitmap;
    Canvas canvasResult;
    Paint paint;
    ColorMatrixColorFilter filter;
    ColorMatrix colorMatrix;
    Bitmap bitmapResult;

    float[] cmData = new float[] {
            1, 0, 0, 0, 0,
            0, 1, 0, 0, 0,
            0, 0, 1, 0, 0,
            0, 0, 0, 1, 0 };

    RadioGroup mAxisRadioGroup;
    RadioButton mAxisRedRadioButton, mAxisGreenRadioButton,
            mAxisBlueRadioButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_changer);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        imageView = findViewById(R.id.image_view);

        btnsel = findViewById(R.id.btnSelect);
        colorRGB = (Button) findViewById(R.id.ColorFilter);
        filterOptions = (Button) findViewById(R.id.FilterOptions) ;
        reset = (Button) findViewById(R.id.Reset);
        contrast = (Button) findViewById(R.id.Contrast);
        exit = (Button) findViewById(R.id.Exit);
        saveImage = (Button) findViewById(R.id.SaveImageFilter);

        mSaturationSeekbar = (SeekBar) findViewById(R.id.seekBarSaturation);
        mSaturationSeekbar.setOnSeekBarChangeListener(Saturation);

        MainRelativeLayout = (RelativeLayout) findViewById(R.id.MainRelativeLayout);

        mRotateSeekbar = (SeekBar) findViewById(R.id.seekBarRotate);
        mRotateSeekbar.setOnSeekBarChangeListener(ColorFilter);

        mAxisRadioGroup = (RadioGroup) findViewById(R.id.axisgroup);
        mAxisRedRadioButton = (RadioButton) findViewById(R.id.radioAxisRed);
        mAxisGreenRadioButton = (RadioButton) findViewById(R.id.radioAxisGreen);
        mAxisBlueRadioButton = (RadioButton) findViewById(R.id.radioAxisBlue);

        imageView.setDrawingCacheEnabled(true);

        btnsel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageFromGallery();
            }
        });


        colorRGB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorARGBFilter();
            }
        });

        filterOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterOptions();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetImageView();
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExitFilterOptions();
            }
        });

        contrast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContrastFilter();
            }
        });

        saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveImageAfterFilter();
            }
        });
    }

    SeekBar.OnSeekBarChangeListener Saturation = new SeekBar.OnSeekBarChangeListener() {

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

    SeekBar.OnSeekBarChangeListener ColorFilter = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            loadRotatedBitmap();
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

    // ARGB
    private void loadRotatedBitmap() {
        if (bitmap != null) {
            int progressRotation = mRotateSeekbar.getProgress();

            float rotationDegree = (float) progressRotation;

            if (mAxisRedRadioButton.isChecked()) {

                imageView.setImageBitmap(updateRotation(bitmap, 0,
                        rotationDegree));
            } else if (mAxisGreenRadioButton.isChecked()) {

                imageView.setImageBitmap(updateRotation(bitmap, 1,
                        rotationDegree));
            } else if (mAxisBlueRadioButton.isChecked()) {

                imageView.setImageBitmap(updateRotation(bitmap, 2,
                        rotationDegree));
            }
        }

    }
    private Bitmap updateRotation(Bitmap src, int axis, float degrees) {
        int width = src.getWidth();
        int height = src.getHeight();

        bitmapResult = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        canvasResult = new Canvas(bitmapResult);
        paint = new Paint();
        colorMatrix = new ColorMatrix();
        colorMatrix.setRotate(axis, degrees);
        filter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(filter);
        canvasResult.drawBitmap(src, 0, 0, paint);
        canvasResult.save();

        return bitmapResult;
    }
    private void ColorARGBFilter(){
        mRotateSeekbar.setVisibility((mRotateSeekbar.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE);
        mAxisRedRadioButton.setVisibility((mAxisRedRadioButton.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE);
        mAxisGreenRadioButton.setVisibility((mAxisGreenRadioButton.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE);
        mAxisBlueRadioButton.setVisibility((mAxisBlueRadioButton.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE);
    }

    // Saturation -> Black and White
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

        bitmapResult = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvasResult = new Canvas(bitmapResult);
        paint = new Paint();
        colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(settingSat);
        filter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(filter);
        canvasResult.drawBitmap(src, 0, 0, paint);
        canvasResult.save();

        return bitmapResult;
    }
    private void ContrastFilter(){
        mSaturationSeekbar.setVisibility((mSaturationSeekbar.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE);
    }


    private void ResetImageView(){
        imageView.setImageBitmap(bitmap);
        mSaturationSeekbar.setProgress(0);
        mRotateSeekbar.setProgress(0);
    }

    private void showFilterOptions(){
        exit.setVisibility((exit.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE);
        contrast.setVisibility((contrast.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE);
        reset.setVisibility((reset.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE);
        colorRGB.setVisibility((colorRGB.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE);
        btnsel.setVisibility((btnsel.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
        filterOptions.setVisibility((filterOptions.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
    }

    private void ExitFilterOptions(){
        exit.setVisibility((exit.getVisibility() == View.VISIBLE) ? View.GONE : View.GONE);
        contrast.setVisibility((contrast.getVisibility() == View.VISIBLE) ? View.GONE : View.GONE);
        reset.setVisibility((reset.getVisibility() == View.VISIBLE) ? View.GONE : View.GONE);
        colorRGB.setVisibility((colorRGB.getVisibility() == View.VISIBLE) ? View.GONE : View.GONE);
        btnsel.setVisibility((btnsel.getVisibility() == View.GONE) ? View.VISIBLE : View.VISIBLE);
        filterOptions.setVisibility((filterOptions.getVisibility() == View.GONE) ? View.VISIBLE : View.VISIBLE);

        if(btnsel.getVisibility() == View.VISIBLE || filterOptions.getVisibility() == View.VISIBLE) {
            mRotateSeekbar.setVisibility((mRotateSeekbar.getVisibility() == View.VISIBLE) ? View.GONE : View.GONE);
            mAxisRedRadioButton.setVisibility((mAxisRedRadioButton.getVisibility() == View.VISIBLE) ? View.GONE : View.GONE);
            mAxisGreenRadioButton.setVisibility((mAxisGreenRadioButton.getVisibility() == View.VISIBLE) ? View.GONE : View.GONE);
            mAxisBlueRadioButton.setVisibility((mAxisBlueRadioButton.getVisibility() == View.VISIBLE) ? View.GONE : View.GONE);
            mSaturationSeekbar.setVisibility((mSaturationSeekbar.getVisibility() == View.VISIBLE) ? View.GONE : View.GONE);
        }
    }


    private void SaveImageAfterFilter(){
        if(imageView.getDrawable() != null) {
            bitmap = Bitmap.createBitmap(MainRelativeLayout.getWidth(), MainRelativeLayout.getHeight(), Bitmap.Config.ARGB_8888);
            canvasResult = new Canvas(bitmap);
            imageView.draw(canvasResult);

            File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Joy" + File.separator + "Camera");
            if (!imagesFolder.exists()) {
                imagesFolder.mkdirs();
            }
            File file = new File(imagesFolder.toString() + File.separator + System.currentTimeMillis() + ".png");
            String msg = "Pic captured at " + file.getAbsolutePath();
            Toast.makeText(ImageChanger.this, msg, Toast.LENGTH_SHORT).show();

            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            } catch (Exception e) {
            }
        }
    }
}
