package com.example.joy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Collage_Maker extends AppCompatActivity {

    Button image_loader, image_saver, information;
    TextView textInformation;
    ImageView pickedImage;
    RelativeLayout rl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_collage__maker);

        final ImageView imageView = (ImageView) findViewById(R.id.Collage_Maker);
        Intent intent = getIntent();
        byte[] bytes = intent.getByteArrayExtra("Image");
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        imageView.setImageBitmap(bitmap);

        rl = (RelativeLayout)findViewById(R.id.MainRelativeLayout);

        image_loader = (Button) findViewById(R.id.LoadGallery);
        image_saver = (Button) findViewById(R.id.SaveImage);
        information = (Button) findViewById(R.id.Information);

        pickedImage = (ImageView) findViewById(R.id.PickedImage);

        textInformation = (TextView) findViewById(R.id.TextInformation);

        information.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                textInformation.setText(
                        "How to use Collage Option" + "\n" +
                        "Step 1: Click on \"Load Image\". \n" +
                        "Step 2: Choose one or multiple photos from your gallery.\n" +
                        "Step 3: Move your selected photos to their places in the collage.\n" +
                        "Step 4: Save your collage.");
                textInformation.setVisibility((textInformation.getVisibility() == v.GONE) ? v.VISIBLE : v.GONE);
                imageView.setVisibility((imageView.getVisibility() == v.VISIBLE) ? v.GONE : v.VISIBLE);
                pickedImage.setVisibility((pickedImage.getVisibility() == v.VISIBLE) ? v.GONE: v.VISIBLE);
            }
        });

        image_loader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageView.getVisibility() == v.GONE){
                    imageView.setVisibility(v.VISIBLE);
                }
                else{
                    LoadGallery();
                }
            }
        });
    }

    private void LoadGallery(){
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setType("image/*");
            startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            pickedImage.setVisibility(View.VISIBLE);

            List<Bitmap> bitmaps = new ArrayList<>();

            ClipData clipData = data.getClipData();

            if(clipData != null){
                    for(int i = 0; i <clipData.getItemCount(); i++)
                    {
                        Uri imageUri = clipData.getItemAt(i).getUri();

                        try {

                            InputStream is = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            bitmaps.add(bitmap);

                            ImageView newImage = new ImageView(getApplicationContext());

                            LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                            lp.addRule(RelativeLayout.ALIGN_BOTTOM,1);
                            lp.addRule(RelativeLayout.ALIGN_LEFT,1);
                            lp.addRule(RelativeLayout.ALIGN_RIGHT,1);
                            lp.addRule(RelativeLayout.ALIGN_TOP,1);

                            newImage.setLayoutParams(lp);

                            rl.addView(newImage);

                            newImage.setImageBitmap(bitmap);

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
            }
            else{
                Uri imageUri = data.getData();

                try {

                    InputStream is = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    bitmaps.add(bitmap);

                    ImageView newImage = new ImageView(getApplicationContext());

                    LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    lp.addRule(RelativeLayout.ALIGN_BOTTOM,1);
                    lp.addRule(RelativeLayout.ALIGN_LEFT,1);
                    lp.addRule(RelativeLayout.ALIGN_RIGHT,1);
                    lp.addRule(RelativeLayout.ALIGN_TOP,1);

                    newImage.setLayoutParams(lp);

                    rl.addView(newImage);

                    newImage.setImageBitmap(bitmap);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
