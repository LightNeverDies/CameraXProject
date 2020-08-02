package com.example.joy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

public class Fragment_Activity extends Fragment {
    // Adding Fragments Dynamically
/*    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_);
    }*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_fragment_,parent,false);
        TextureView textureView = (TextureView) view.findViewById(R.id.cap_img);
        return textureView;
    }



}
