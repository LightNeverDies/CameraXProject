package com.example.joy;

import android.net.Uri;

public class GalleryModel {

    private String name;
    private String date;
    private Uri uri;

    public GalleryModel(String name, String date, Uri uri) {
        this.name = name;
        this.date = date;
        this.uri = uri;
    }
    public GalleryModel(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
