package com.example.joy;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

public class Collage extends AppCompatActivity {

    private StorageReference mStorageRef;
    private GridView gridView;
    private CollageAdapter collageAdapter;
    private String url = "gs://joyfirebaseproject-855bc.appspot.com/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getData();
        setContentView(R.layout.activity_collage);
        gridView = (GridView) findViewById(R.id.collage_grid);
    }


    private void LoadFiles(){
        ArrayList<CollageModel> collageModels = new ArrayList<>();
        CollageModel collageModel;
        File collageFolder = new File (Environment.getExternalStorageDirectory(), "Joy" + File.separator + "Download");
        File[] files = collageFolder.listFiles();
        for(int i =0; i < files.length; i++)
        {
            File file = files[i];
            collageModel = new CollageModel();
            collageModel.setUri(Uri.fromFile(file));
            collageModel.setCollageName(file.getName());
            collageModels.add(collageModel);
        }
        gridView.setAdapter(new CollageAdapter(Collage.this,collageModels));
        gridView.invalidate();
    }

    private void getData(){
        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url);
        File collageFolder = new File (Environment.getExternalStorageDirectory(), "Joy" + File.separator + "Download");
        if(!collageFolder.exists()){collageFolder.mkdirs();}
        if(collageFolder.exists()) {
            mStorageRef.listAll()
                    .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                        @Override
                        public void onSuccess(ListResult listResult) {
                            for (StorageReference item : listResult.getItems()) {
                                final File localFile = new File(collageFolder.toString() + File.separator + item.getName());
                                if (!localFile.exists()) {
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
