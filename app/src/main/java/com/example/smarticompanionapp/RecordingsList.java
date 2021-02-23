/*
 * This class is used to fill in the rows of the list view in RecordingsActivity that represent videos
 * Takes as argument, in constructor the Recordings Activity context, textview resource id
 * and an ArrayList with strings consisting of the data about a video that we wish to display.
 *
 * Has three components, the video thumbnail, video data and a options menu button
 * currently the video thumbnail is a placeholder
 * The options menu popup also has placeholder functionality
 * Will need to add interaction with a video data type
 */

package com.example.smarticompanionapp;

import android.content.Context;
import android.net.Uri;
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

import androidx.appcompat.app.AlertDialog;

import java.io.File;
import java.util.ArrayList;

public class RecordingsList extends ArrayAdapter<String> {
    private Context context;
    private ArrayList<String> StringArray;

    public RecordingsList (Context context, int textViewResourceId, ArrayList<String> StringArray) {
        super(context, textViewResourceId,StringArray);
        this.context = context;
        this.StringArray = StringArray;
    }

    @Override
    public View getView(int position, View contextView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.list_row, parent, false);

        ImageView thumbnail = (ImageView) row.findViewById(R.id.thumbnail);
        thumbnail.setImageResource(R.drawable.ic_launcher_foreground); //placeholder, should have video thumbnail
        thumbnail.setVisibility(View.VISIBLE);

        TextView data = (TextView) row.findViewById(R.id.videoData);
        data.setText((StringArray.get(position)));

        ImageButton optionIcon = (ImageButton) row.findViewById(R.id.optionsIcon);
        //optionIcon.setVisibility(View.VISIBLE);
        optionIcon.setImageResource(R.drawable.ic_action_name);


        /*
         * This is used to generate the dialog box with play/export/delete options
         * Currently just displays toasts that buttons have been pressed
         */
        optionIcon.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setNeutralButton("Play", (dialog, which) -> {
                Toast toast = Toast.makeText(context.getApplicationContext(),"play video placeholder", Toast.LENGTH_SHORT);
                toast.show();

                VideoView vid;
                MediaController m;
                View video = inflater.inflate(R.layout.video_view, parent, false);
                vid = (VideoView) video.findViewById(R.id.videoView);
                Uri u = Uri.parse("");
                vid.setVideoURI(u);
                vid.start();
            });
            builder.setPositiveButton("Export", (dialog, which) -> {
                Toast toast = Toast.makeText(context.getApplicationContext(),"export video placeholder", Toast.LENGTH_SHORT);
                toast.show();
            });
            builder.setNegativeButton("delete", (dialog, which) -> {
                Toast toast = Toast.makeText(context.getApplicationContext(),"delete video placeholder", Toast.LENGTH_SHORT);
                toast.show();
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        return row;
    }
}
