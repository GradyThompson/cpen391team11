/*
 *  This class represents a recording entry in the local
 *  recordings database
 *
 *  Entries are defined and identified by their uri,
 *  the uri for different entries MUST be different.
 *  All other fields can be repeated
 */

package com.example.smarticompanionapp;


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
