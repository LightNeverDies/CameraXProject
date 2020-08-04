package com.example.joy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GalleryAdapter extends BaseAdapter {

    Context context;
    ArrayList<GalleryModel> galleryModels;

    public GalleryAdapter(Context context, ArrayList<GalleryModel> galleryModels) {
        this.context = context;
        this.galleryModels = galleryModels;
    }


    @Override
    public int getCount() {
        return galleryModels.size();
    }

    @Override
    public Object getItem(int position) {
        return  galleryModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       if(convertView == null){
           convertView = LayoutInflater.from(context).inflate(R.layout.gallery_model, parent, false);
       }

       final GalleryModel gm = (GalleryModel) this.getItem(position);
        TextView nameTxt = (TextView) convertView.findViewById(R.id.ImgName);
        TextView dateTxt = (TextView) convertView.findViewById(R.id.ImgDate);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.Img);

        nameTxt.setText(gm.getName());
       // dateTxt.setText(String.valueOf(gm.getDate()));
        dateTxt.setText(gm.getDate());
        Picasso.with(context).load(gm.getUri()).into(imageView);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, gm.getName(), Toast.LENGTH_SHORT).show();
            }
        });

       return convertView;
    }
}
