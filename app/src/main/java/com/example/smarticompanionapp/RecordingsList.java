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
import android.content.Intent;
import android.net.Uri;
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

import androidx.appcompat.app.AlertDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.arthenica.mobileffmpeg.FFprobe;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;
import static com.arthenica.mobileffmpeg.Config.getPackageName;


public class RecordingsList extends ArrayAdapter<VideoResult> {
    private Context context;
    private ArrayList<VideoResult> VideoArray;

    public RecordingsList (Context context, int textViewResourceId, ArrayList<VideoResult> VideoArray) {
        super(context, textViewResourceId, VideoArray);
        this.context = context;
        this.VideoArray = VideoArray;
    }

    @Override
    public View getView(int position, View contextView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.list_row, parent, false);

        ImageView thumbnail = (ImageView) row.findViewById(R.id.thumbnail);
        thumbnail.setImageResource(R.drawable.ic_launcher_foreground); //placeholder, should have video thumbnail
        thumbnail.setVisibility(View.VISIBLE);

        TextView data = (TextView) row.findViewById(R.id.videoData);
        data.setText((VideoArray.get(position).getDateTime()));

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

                Intent videoIntent = new Intent(parent.getContext(), VideoActivity.class);
                videoIntent.putExtra("vid", VideoArray.get(position).getVideo());
                videoIntent.putExtra("dt", VideoArray.get(position).getDateTime());
                context.startActivity(videoIntent);
            });
            builder.setPositiveButton("Export", (dialog, which) -> {
                Toast toast = Toast.makeText(context.getApplicationContext(),"export video placeholder", Toast.LENGTH_SHORT);
                toast.show();
                //using this to test converting video formats, should be moved
                //and adapted when downloading videos works

                //At the moment, this converts an mpeg-2 file from the raw folder to an mp4 and
                //plays it in the video view.
                try {
                    //the video stream code is used to retrieve a file from the res/raw folder
                    //largely unnecessary in final implementation
                    InputStream vid = context.getResources().openRawResource(R.raw.testvid1);
                    File video = new File(context.getFilesDir(), "testvid1.mpeg");
                    OutputStream vidout = new FileOutputStream(video);

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while((bytesRead = vid.read(buffer)) != -1) {
                        vidout.write(buffer, 0, bytesRead);
                    }

                    //need to designate an mp4 file with a path to put the converted video
                    File MP4vid = new File(context.getFilesDir(), "testvid2.mp4");

                    //this command converts the video found in the first path to the one in the second
                    String ffmepegCommand = String.format("-y -i %s %s", video.getAbsolutePath(), MP4vid.getAbsolutePath());

                    int rc = FFmpeg.execute(ffmepegCommand);

                    if (rc == RETURN_CODE_SUCCESS) {
                        Log.i(Config.TAG, "success");
                        Intent videoIntent = new Intent(parent.getContext(), VideoActivity.class);
                        videoIntent.putExtra("vid", Uri.parse(MP4vid.getAbsolutePath()));
                        videoIntent.putExtra("dt", "test mp4");
                        context.startActivity(videoIntent);
                    } else {
                        Log.i(Config.TAG, "failure");
                    }
                    vid.close();
                    vidout.close();

                }
                catch (IOException e) {
                    Log.i(Config.TAG, "IO fail");
                }
            });
            builder.setNegativeButton("Delete", (dialog, which) -> {
                Toast toast = Toast.makeText(context.getApplicationContext(),"delete video placeholder", Toast.LENGTH_SHORT);
                toast.show();
                this.remove(this.getItem(position));
                this.notifyDataSetChanged();
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        return row;
    }
}
