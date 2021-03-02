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
import java.util.stream.IntStream;

public class RecordingsList extends ArrayAdapter<String> {
    private Context context;
    //private ArrayList<VideoResult> VideoArray;
    private ArrayList<String> StringArray;

    /*
    public RecordingsList (Context context, int textViewResourceId, ArrayList<VideoResult> VideoArray) {
        super(context, textViewResourceId, getStringList(VideoArray));
        this.context = context;
        this.VideoArray = VideoArray;
    }

     */

    public RecordingsList (Context context, int textViewResourceId, ArrayList<String> StringArray) {
        super(context, textViewResourceId,StringArray);
        this.context = context;
        this.StringArray = StringArray;
    }

    /*
    private static ArrayList<String> getStringList(ArrayList<VideoResult> videoArray) {
        ArrayList<String> StringArray = new ArrayList<>();
        for (VideoResult v : videoArray) {
            StringArray.add(v.getDateTime());
        }
        return StringArray;
    }

     */

    @Override
    public View getView(int position, View contextView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.list_row, parent, false);

        ImageView thumbnail = (ImageView) row.findViewById(R.id.thumbnail);
        thumbnail.setImageResource(R.drawable.ic_launcher_foreground); //placeholder, should have video thumbnail
        thumbnail.setVisibility(View.VISIBLE);

        TextView data = (TextView) row.findViewById(R.id.videoData);
        //data.setText((VideoArray.get(position).getDateTime()));
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

                //This probably should be moved a separate fragment
                MediaController m = new MediaController(context);
                View video = inflater.inflate(R.layout.video_view, parent, false);
                VideoView vid = (VideoView) video.findViewById(R.id.videoView);

                Uri u = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.test_video_0);
                //Uri u = VideoArray.get(position).getVideo();
                vid.setVideoURI(u);
                vid.setMediaController(m);
                vid.start();
                Toast toast = Toast.makeText(context.getApplicationContext(),"play video placeholder", Toast.LENGTH_SHORT);
                toast.show();
            });
            builder.setPositiveButton("Export", (dialog, which) -> {
                Toast toast = Toast.makeText(context.getApplicationContext(),"export video placeholder", Toast.LENGTH_SHORT);
                toast.show();
            });
            builder.setNegativeButton("Delete", (dialog, which) -> {
                Toast toast = Toast.makeText(context.getApplicationContext(),"delete video placeholder", Toast.LENGTH_SHORT);
                toast.show();
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        return row;
    }

}
