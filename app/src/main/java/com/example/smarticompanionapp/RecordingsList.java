 /*
 * This class is used to fill in the rows of the list view in RecordingsActivity that represent videos
 * Takes as argument, in constructor the Recordings Activity context, textview resource id
 * and an ArrayList with strings consisting of the data about a video that we wish to display.
 *
 * Has three components, the video thumbnail, video data and a options menu button
 * The options menu popup supports the playing, exporting and deleting of recordings
 */

package com.example.smarticompanionapp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.arthenica.mobileffmpeg.FFprobe;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;
import static com.arthenica.mobileffmpeg.Config.getPackageName;


public class RecordingsList extends ArrayAdapter<Recording> {
    private Context context;
    private RecordingsArray recArray;
    public ArrayList<String> videoData;
    private RecordingViewModel mRecordViewModel;

    public RecordingsList (Context context, int textViewResourceId, ArrayList<String> VideoData,
                           RecordingsArray, RecordingViewModel mRecordViewModel) {
        super(context, textViewResourceId, recArray.RecArray);
        //super(context, textViewResourceId, VideoData);
        this.context = context;
        this.recArray = recArray;
        this.videoData = recArray.getVideoDataList();
        this.mRecordViewModel = mRecordViewModel;
    }

    @Override
    public View getView(int position, View contextView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.list_row, parent, false);

        ImageView thumbnail = (ImageView) row.findViewById(R.id.thumbnail);
        thumbnail.setImageBitmap(recArray.getRecord(position).thumbnail);
        thumbnail.setVisibility(View.VISIBLE);

        TextView data = (TextView) row.findViewById(R.id.videoData);
        data.setText(videoData.get(position));

        ImageButton optionIcon = (ImageButton) row.findViewById(R.id.optionsIcon);
        optionIcon.setImageResource(R.drawable.ic_action_name);


        /*
         * This is used to generate the dialog box with play/export/delete options
         */
        optionIcon.setOnClickListener(v -> {

            //creates and switches to the videoPlayer
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setNeutralButton("Play", (dialog, which) -> {
                Intent videoIntent = new Intent(parent.getContext(), VideoActivity.class);

                videoIntent.putExtra("vid", recArray.getRecord(position).uri);
                videoIntent.putExtra("dt", videoData.get(position));
                //System.out.println(recArray.getRecord(position).uri);
                context.startActivity(videoIntent);
            });

            /*
             *  This provides the functionality to export videos to the android
             *      device's image gallery
             *  Takes the video of interest, then writes it to a location viewable
             *      by the android gallery, where the user is free to do with, as they please
             */
            builder.setPositiveButton("Export", (dialog, which) -> {

                String state = Environment.getExternalStorageState();
                if (!Environment.MEDIA_MOUNTED.equals(state)) {
                    Log.i("", "external storage not mounted");
                }

                File expFile = new File(recArray.getRecord(position).uri.getPath());
                String fileName = expFile.getName();

                ContentValues valuesvideos;

                valuesvideos = new ContentValues();
                valuesvideos.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/" + "SmartIRecordings");
                valuesvideos.put(MediaStore.Video.Media.TITLE, fileName);
                valuesvideos.put(MediaStore.Video.Media.DISPLAY_NAME, fileName);
                valuesvideos.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
                valuesvideos.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000.0);
                valuesvideos.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
                valuesvideos.put(MediaStore.Video.Media.IS_PENDING, 1);
                ContentResolver resolver = context.getContentResolver();
                Uri collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                Uri uriSavedVideo = resolver.insert(collection, valuesvideos);

                ParcelFileDescriptor pfd;

                try {
                    pfd = context.getContentResolver().openFileDescriptor(uriSavedVideo, "w");
                    FileOutputStream out = new FileOutputStream(pfd.getFileDescriptor());

                    FileInputStream in = new FileInputStream(expFile);

                    byte[] buff = new byte[8192];
                    int len;
                    while ((len = in.read(buff)) > 0) {
                        out.write(buff, 0, len);
                    }
                    out.close();
                    in.close();
                    pfd.close();
                    Toast success = Toast.makeText(context.getApplicationContext(),"Recording successfully saved to photos", Toast.LENGTH_SHORT);
                    success.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                valuesvideos.clear();
                valuesvideos.put(MediaStore.Video.Media.IS_PENDING, 0);
                context.getContentResolver().update(uriSavedVideo, valuesvideos, null, null);

            });
            /*
             *  Provides the functionality to delete a recording and its local data
             *  Must remove the entry from this RecordingList,
             *  remove the associated RecordingEntity from the database,
             *  remove the associated recording from the recordingArray,
             *  then update the UI view to reflect changes
             */
            builder.setNegativeButton("Delete", (dialog, which) -> {
                this.remove(this.getItem(position));
                mRecordViewModel.deleteByUri(recArray.getRecord(position).uri);
                recArray.delete(position);
                videoData = recArray.getVideoDataList();
                this.notifyDataSetChanged();
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        return row;
    }
}
