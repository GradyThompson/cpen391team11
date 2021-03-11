/*
 * This class is used to interface with recordings, and provide the necessary data to allow
 * the recordings list to function
 */

package com.example.smarticompanionapp;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

import java.io.File;

public class Recording {

    /*
     *  This data type contains the following entries:
     *  VideoData is a string containing the date the video
     *      was recorded, the length of the video, and the severity
     *  uri contains the file path for the video, should not be a path
     *      to an mpeg-2 file since the android videoplayer does not support
     *      this format, the video will need to be converted into a compatible format
     *      (mp4 maybe) before being  inserted
     *  Thumbnail is a thumbnail created from the video
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
