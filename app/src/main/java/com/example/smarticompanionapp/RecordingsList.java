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
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.arthenica.mobileffmpeg.FFprobe;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;
import static com.arthenica.mobileffmpeg.Config.getPackageName;


public class RecordingsList extends ArrayAdapter<String> {
    private Context context;
    private ArrayList<VideoResult> VideoArray;
    private RecordingsArray recArray;
    private ArrayList<String> videoData;

    /*
    public RecordingsList (Context context, int textViewResourceId, ArrayList<VideoResult> VideoArray) {
        super(context, textViewResourceId, VideoArray);
        this.context = context;
        this.VideoArray = VideoArray;
    }
    */
    public RecordingsList (Context context, int textViewResourceId, ArrayList<String> VideoData, RecordingsArray recArray) {
        super(context, textViewResourceId, VideoData);
        this.context = context;
        //this.VideoData = VideoArray;
        this.recArray = recArray;
        this.videoData = recArray.getVideoDataList();
    }

    @Override
    public View getView(int position, View contextView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.list_row, parent, false);

        ImageView thumbnail = (ImageView) row.findViewById(R.id.thumbnail);
        //thumbnail.setImageResource(R.drawable.ic_launcher_foreground); //placeholder, should have video thumbnail
        thumbnail.setImageBitmap(recArray.getRecord(position).thumbnail);
        thumbnail.setVisibility(View.VISIBLE);

        TextView data = (TextView) row.findViewById(R.id.videoData);
        //data.setText((VideoArray.get(position).getDateTime()));
        data.setText(videoData.get(position));

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
                Intent videoIntent = new Intent(parent.getContext(), VideoActivity.class);
                /*
                videoIntent.putExtra("vid", VideoArray.get(position).getVideo());
                videoIntent.putExtra("dt", VideoArray.get(position).getDateTime());
                 */
                videoIntent.putExtra("vid", recArray.getRecord(position).uri);
                videoIntent.putExtra("dt", videoData.get(position));
                context.startActivity(videoIntent);
            });
            builder.setPositiveButton("Export", (dialog, which) -> {
                Toast toast = Toast.makeText(context.getApplicationContext(),"export video placeholder", Toast.LENGTH_SHORT);
                toast.show();

                //trying to get file exportation to work, not yet functional
                /*
                File expFile = new File(recArray.getRecord(position).uri.getPath());
                Intent shareFileInt = new Intent(Intent.ACTION_SEND);

                shareFileInt.setType(URLConnection.guessContentTypeFromName(expFile.getName()));
                shareFileInt.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+ expFile.getAbsolutePath()));
                shareFileInt.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(Intent.createChooser(shareFileInt, "Share File"));
                */
            });
            builder.setNegativeButton("Delete", (dialog, which) -> {
                Toast toast = Toast.makeText(context.getApplicationContext(),"delete video placeholder", Toast.LENGTH_SHORT);
                toast.show();
                this.remove(this.getItem(position));
                recArray.delete(position);
                this.notifyDataSetChanged();
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        return row;
    }
}
