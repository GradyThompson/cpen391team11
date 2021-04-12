/*
 * This class is used to interface with recordings, and provide the necessary data to allow
 * the recordings list to function. It represents a recording within our app, with all of
 * the associated data that we wish to display to the user
 */

package com.example.smarticompanionapp;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.Nullable;


public class Recording {

    /*
     *  This data type contains the following entries:
     *  VideoData is a string containing the date the video
     *      was recorded, the length of the video, and the severity
     *  uri contains the file path for the mp4 video obtained from the server
     *  Thumbnail is a thumbnail created from the video, is created when the
     *      recording is instantiated
     *  Severity is the calculated severity of the video
     */
    public String videoData;
    public Uri uri;
    public Bitmap thumbnail;
    public Double severity;

    public Recording(String date, Double severity, String length, Uri uri) {
        videoData = "Severity: " + severity + ", Date: " + date + ", Length: " + length;
        this.severity = severity;
        this.uri = uri;
        thumbnail = ThumbnailUtils.createVideoThumbnail(uri.toString(), MediaStore.Video.Thumbnails.MICRO_KIND);
    }

    public Recording() {
        videoData = "N/a";
        severity = -.1;
        uri = null;
        thumbnail = null;
    }

    /*
     * Simple equality operator, checks that the fields are equal
     * Recording objects that represent the same video, but have the
     * video saved in different location (ie. different URIs), are
     * considered to be different recordings
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj != null && getClass() == obj.getClass()) {
            Recording other = (Recording) obj;

            return this.videoData.equals(other.videoData) && this.severity.equals(other.severity)
                    && this.thumbnail.equals(other.thumbnail) && this.uri.equals(other.uri);
        }
        return false;
    }
}
