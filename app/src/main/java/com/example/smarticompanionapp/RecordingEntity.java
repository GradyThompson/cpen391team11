package com.example.smarticompanionapp;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "RecordingTable")
public class RecordingEntity {
    @PrimaryKey
    @NonNull
    public String uri;

    public String date;
    public Double severity;
    public String length;

}
