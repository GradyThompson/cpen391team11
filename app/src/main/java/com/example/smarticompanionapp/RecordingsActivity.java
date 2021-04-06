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
import com.google.firebase.storage.FirebaseStorage;

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

    private ArrayList<VideoResult> videoData = new ArrayList<>();

   static private RecordingsArray recArray = new RecordingsArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Recordings");
        setSupportActionBar(toolbar);


        //will need to populate list with data on runtime, probably put up a loading screen
        //while retrieving recordings

       // try {
            /*
            InputStream vid = RecordingsActivity.this.getResources().openRawResource(R.raw.test_video_0);
            File video = new File(RecordingsActivity.this.getFilesDir(), "test_video_0.mp4");
            OutputStream vidout = new FileOutputStream(video);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = vid.read(buffer)) != -1) {
                vidout.write(buffer, 0, bytesRead);
            }
            */
            videoData = getIntent().getParcelableArrayListExtra("videos");

            for (int x = 0; x < videoData.size(); x++){
                Uri uri = videoData.get(x).getVideo();
                String date = videoData.get(x).getDateTime();
                String severity = videoData.get(x).getSeverity();
                String length = videoData.get(x).getLength();
                recArray.add(new Recording(date, Double.parseDouble(severity) + x, length, uri));
            }

        recordingsList = new RecordingsList(this, android.R.layout.simple_list_item_1, recArray.getVideoDataList(), recArray);
        Log.d("TAG", recArray.getVideoData(0));
        System.out.println(recArray.getRecord(0).uri);

        ListView recordListView = (ListView) findViewById(R.id.recordings_list_view);

        recordListView.setAdapter(recordingsList);

        //recordingsList.notifyDataSetChanged();

    }

    @Override
    protected void onStart() {
        super.onStart();
        ImageButton settingsButton = (ImageButton) findViewById(R.id.settingsButton);
        settingsButton.setImageResource(R.drawable.ic_settings_icon);
        settingsButton.setVisibility(View.VISIBLE);
        settingsButton.setOnClickListener(view -> {
            Intent settingsIntent = new Intent(RecordingsActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        });
    }
}