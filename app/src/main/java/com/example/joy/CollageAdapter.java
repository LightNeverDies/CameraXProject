package com.example.joy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class CollageAdapter extends BaseAdapter {

    Context context;
    ArrayList<CollageModel> collageModels;
    Bitmap bitmap = null;

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
        TextView nameTxt = (TextView) convertView.findViewById(R.id.CollageName);

        nameTxt.setText("Collage Name:  " + cm.getCollageName());
        Picasso.with(context).load(cm.getUri()).noFade().noPlaceholder().resize(400,400).into(imageView);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, cm.getCollageName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, Collage_Maker.class);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bytes = stream.toByteArray();

                intent.putExtra("Image",bytes);
                context.startActivity(intent);

            }
        });

        return convertView;
    }
}
