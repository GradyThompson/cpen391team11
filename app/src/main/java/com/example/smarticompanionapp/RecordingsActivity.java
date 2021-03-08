/*
 * This activity represents the Recordings screen of the app
 * Contains a scrollable list of recordings that are sorted based on severity
 * Has a button in the top right that brings up the settings activity.
 */

package com.example.smarticompanionapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.arthenica.mobileffmpeg.Config;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class RecordingsActivity extends AppCompatActivity {

    private RecordingsList recordingsList;

    private ArrayList<VideoResult> videoData = new ArrayList<VideoResult>();

    private RecordingsArray recArray = new RecordingsArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton settingsButton = (ImageButton) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(view -> {
            Intent settingsIntent = new Intent(RecordingsActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        });
        /*
        Bundle b = getIntent().getExtras();

        if (b != null) {
            videoData.addAll((ArrayList<VideoResult>)b.get("videos"));
        }
        */
        //recordingsList = new RecordingsList(this, android.R.layout.simple_list_item_1, videoData);


        //will need to populate list with data on runtime, probably put up a loading screen
        //while retrieving recordings

        //placeholder, all videos are raw test_video_0.mp4
        try {
            InputStream vid = RecordingsActivity.this.getResources().openRawResource(R.raw.test_video_0);
            File video = new File(RecordingsActivity.this.getFilesDir(), "test_video_0.mp4");
            OutputStream vidout = new FileOutputStream(video);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = vid.read(buffer)) != -1) {
                vidout.write(buffer, 0, bytesRead);
            }

            for (int x = 0; x <= 20; x++){
                //videoData.add(new VideoResult(null, "test" + x));
                recArray.add(new Recording("date "+ x, (double) x, "vidLength "+ x, Uri.parse(video.getAbsolutePath())));
            }
        } catch (IOException e) {
            Log.i(Config.TAG, "IO fail");
        }

        recordingsList = new RecordingsList(this, android.R.layout.simple_list_item_1, recArray.getVideoDataList(), recArray);

        ListView recordListView = (ListView) findViewById(R.id.recordings_list_view);

        recordListView.setAdapter(recordingsList);

        //recordingsList.notifyDataSetChanged();

    }

}