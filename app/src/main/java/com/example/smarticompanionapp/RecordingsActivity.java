/*
 * This activity represents the Recordings screen of the app
 * Contains a scrollable list of recordings that are sorted based on severity
 * Has a button in the top right that brings up the settings activity.
 */

package com.example.smarticompanionapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

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

import java.util.ArrayList;
import java.util.List;

public class RecordingsActivity extends AppCompatActivity {

    private RecordingsList recordingsList;

    //private ArrayList<VideoResult> videoData = new ArrayList<VideoResult>();
    private ArrayList<String> videoData = new ArrayList<String>();

    private RecordingsArray videoArray = new RecordingsArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Currently there's a bug where this disappears sometimes when reentering the recordings
        //activity screen
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
        recordingsList = new RecordingsList(this, android.R.layout.simple_list_item_1, videoData);

        ListView recordListView = (ListView) findViewById(R.id.recordings_list_view);

        recordListView.setAdapter(recordingsList);

        //will need to populate list with data on runtime, probably put up a loading screen
        //while retrieving recordings

        //placeholder
        for (int x = 0; x <= 20; x++){
            //videoData.add(new VideoResult(null, "test" + x));

            videoArray.videoDataList.add("test " + x);
        }

        videoData.clear();
        for (int i = 0; i < videoArray.getVideoDataList().size(); i++) {
            videoData.add(videoArray.getVideoData(i));
        }

        recordingsList.notifyDataSetChanged();

    }

}