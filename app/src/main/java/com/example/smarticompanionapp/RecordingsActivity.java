/*
 * This activity represents the Recordings screen of the app
 * Contains a scrollable list of recordings that are sorted based on severity
 * Has a button in the top right that brings up the settings activity.
 */

package com.example.smarticompanionapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class RecordingsActivity extends AppCompatActivity {

    private RecordingsList recordingsList;

    private ArrayList<VideoResult> videoData = new ArrayList<>();

    private RecordingsArray recArray = new RecordingsArray();

   private RecordingViewModel mRecordViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Recordings");
        setSupportActionBar(toolbar);


        mRecordViewModel = new ViewModelProvider(this).get(RecordingViewModel.class);
        List<RecordingEntity> vidData = mRecordViewModel.getAllRecordings();

        for (int x = 0; x < vidData.size(); x++){
            //Uri uri = videoData.get(x).getVideo();
            Uri uri = Uri.parse(vidData.get(x).uri);
            String date = vidData.get(x).date;
            Double severity = vidData.get(x).severity;
            String length = vidData.get(x).length;
            recArray.add(new Recording(date, severity, length, uri));
        }

        recordingsList = new RecordingsList(this, android.R.layout.simple_list_item_1, recArray.getVideoDataList(), recArray, mRecordViewModel);
        Log.d("TAG", recArray.getVideoData(0));
        System.out.println(recArray.getRecord(0).uri);

        ListView recordListView = (ListView) findViewById(R.id.recordings_list_view);

        recordListView.setAdapter(recordingsList);


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