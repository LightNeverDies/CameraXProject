package com.example.joy;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.GridView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Gallery extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        final GridView gridView = (GridView) findViewById(R.id.month_grid);
        gridView.setAdapter(new GalleryAdapter(Gallery.this,getData()));

    }

   private ArrayList<GalleryModel> getData(){

        ArrayList<GalleryModel> galleryModels = new ArrayList<>();

        File imageFolder = new File(Environment.getExternalStorageDirectory() + File.separator + "Joy");

        GalleryModel galleryModel;

        if(imageFolder.exists()){
            File[] files = imageFolder.listFiles();

            for(int i =0; i<files.length; i++){
                File file = files[i];

                galleryModel = new GalleryModel();
                galleryModel.setName(file.getName());
                long miliSeconds = file.lastModified();
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
                Date resultdate = new Date(miliSeconds);
                galleryModel.setDate(resultdate.toString());
                galleryModel.setUri(Uri.fromFile(file));

                galleryModels.add(galleryModel);
            }
        }

       return galleryModels;
   }
}