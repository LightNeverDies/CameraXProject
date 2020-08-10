package com.example.joy;

import android.net.Uri;

public class CollageModel {

    private Uri uri;

    public CollageModel(Uri uri) {
        this.uri = uri;
    }

    public CollageModel(){}

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
