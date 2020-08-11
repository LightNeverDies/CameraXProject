package com.example.joy;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.GridView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Gallery extends AppCompatActivity {

    private String[] extensions = {"jpg", "png", "svg"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_gallery);
        final GridView gridView = (GridView) findViewById(R.id.month_grid);
        gridView.setAdapter(new GalleryAdapter(Gallery.this,getData()));

    }

   private ArrayList<GalleryModel> getData(){

        ArrayList<GalleryModel> galleryModels = new ArrayList<>();

        File imageFolder = new File(Environment.getExternalStorageDirectory() + File.separator + "Joy" + File.separator + "Camera");

        GalleryModel galleryModel ;

        if(imageFolder.exists()){
            File[] files = imageFolder.listFiles();

            for(int i =0; i<files.length; i++){
                File file = files[i];

                galleryModel = new GalleryModel();
                galleryModel.setName(file.getName());
                long milSeconds = file.lastModified();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                String resultDate = sdf.format(milSeconds);
                galleryModel.setDate(resultDate);
                galleryModel.setUri(Uri.fromFile(file));

                Uri png = Uri.fromFile(new File(String.valueOf(file)));
                String fileExtension = MimeTypeMap.getFileExtensionFromUrl(png.toString());
                for(String endPart : extensions){
                    if(fileExtension.endsWith(endPart)) {
                        galleryModels.add(galleryModel);
                    }
                }

            }
        }

       return galleryModels;
   }
}
