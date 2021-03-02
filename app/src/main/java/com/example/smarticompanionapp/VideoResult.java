package com.example.smarticompanionapp;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class VideoResult {
    private Uri video;
    private String dateTime;

    public VideoResult(Uri video, String dateTime) {
        this.video = video;
        this.dateTime = dateTime;
    }

    public Uri getVideo(){ return this.video; }
    public String getDateTime(){ return this.dateTime; }
}
