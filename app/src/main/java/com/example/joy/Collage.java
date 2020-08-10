package com.example.joy;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Collage extends AppCompatActivity {

    private StorageReference mStorageRef;
    private GridView gridView;
    private CollageAdapter collageAdapter;
    private String url = "gs://joyfirebaseproject-855bc.appspot.com/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData();
        setContentView(R.layout.activity_collage);
        gridView = (GridView) findViewById(R.id.collage_grid);
    }


    private void LoadFiles(){
        ArrayList<CollageModel> collageModels = new ArrayList<>();
        CollageModel collageModel;
        File collageFolder = new File (Environment.getExternalStorageDirectory(), "Joy" + File.separator + "Collage");
        File[] files = collageFolder.listFiles();
        for(int i =0; i < files.length; i++)
        {
            File file = files[i];
            collageModel = new CollageModel();
            collageModel.setUri(Uri.fromFile(file));
            collageModels.add(collageModel);
        }
        gridView.setAdapter(new CollageAdapter(Collage.this,collageModels));
    }

    private void getData(){
        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url);
        File collageFolder = new File (Environment.getExternalStorageDirectory(), "Joy" + File.separator + "Collage");
        if(collageFolder.exists()) {
            collageFolder.mkdirs();
            mStorageRef.listAll()
                    .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                        @Override
                        public void onSuccess(ListResult listResult) {
                            for (StorageReference item : listResult.getItems()) {
                                Log.println(2, "Hell", item.toString());
                                final File localFile = new File(collageFolder.toString() + File.separator + item.getName());
                                if(!localFile.exists()) {
                                    StorageReference mStorageReference = mStorageRef.child(item.getName());
                                    mStorageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            String msg = "Pic captured at " + localFile.getPath();
                                            Toast.makeText(Collage.this, msg, Toast.LENGTH_LONG).show();
                                            LoadFiles();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Collage.this, "Download Error", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            }
                            LoadFiles();
                        }
                    });
        }
    }
}
