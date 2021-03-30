package com.example.smarticompanionapp;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class VideoResult implements Parcelable {
    private Uri video;
    private String dateTime;

    public VideoResult(Uri video, String dateTime) {
        this.video = video;
        this.dateTime = dateTime;
    }

    protected VideoResult(Parcel in) {
        video = in.readParcelable(Uri.class.getClassLoader());
        dateTime = in.readString();
    }

    public static final Creator<VideoResult> CREATOR = new Creator<VideoResult>() {
        @Override
        public VideoResult createFromParcel(Parcel in) {
            return new VideoResult(in);
        }

        @Override
        public VideoResult[] newArray(int size) {
            return new VideoResult[size];
        }
    };

    public Uri getVideo(){ return this.video; }
    public String getDateTime(){ return this.dateTime; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(video, flags);
        dest.writeString(dateTime);
    }
}
