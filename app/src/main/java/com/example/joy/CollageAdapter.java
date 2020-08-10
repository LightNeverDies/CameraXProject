package com.example.joy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CollageAdapter extends BaseAdapter {

    Context context;
    ArrayList<CollageModel> collageModels;

    public CollageAdapter(Context context, ArrayList<CollageModel> collageModels) {
        this.context = context;
        this.collageModels = collageModels;
    }

    @Override
    public int getCount() {
        return  collageModels.size();
    }

    @Override
    public Object getItem(int position) {
        return collageModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.collage_model, parent, false);
        }

        final CollageModel cm = (CollageModel) this.getItem(position);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.CollageImg);
        Picasso.with(context).load(cm.getUri()).centerCrop().resize(200,200).into(imageView);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, cm.getUri().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}
