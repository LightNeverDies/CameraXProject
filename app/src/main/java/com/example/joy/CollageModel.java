package com.example.joy;

import android.net.Uri;

public class CollageModel {

    private Uri uri;
    private String collageName;

    public CollageModel(Uri uri, String collageName) {
        this.uri = uri;
        this.collageName = collageName;
    }

    public CollageModel(){}

    public Uri getUri() {
        return uri;
    }

    public String getCollageName() {
        return collageName;
    }

    public void setCollageName(String collageName) {
        this.collageName = collageName;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
