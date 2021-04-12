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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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

        //videoData = getIntent().getParcelableArrayListExtra("videos");

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

        recordingsList = new RecordingsList(this, android.R.layout.simple_list_item_1, recArray.getVideoDataList(), recArray);
        Log.d("TAG", recArray.getVideoData(0));
        System.out.println(recArray.getRecord(0).uri);

        ListView recordListView = (ListView) findViewById(R.id.recordings_list_view);

        recordListView.setAdapter(recordingsList);

        //recordingsList.notifyDataSetChanged();



        Spinner spinner = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sort_recordings, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sortAll(spinner, i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                recordingsList.sort(new Comparator<Recording>() {
                    @Override
                    public int compare(Recording recording, Recording t1) {
                        return recording.severity.compareTo(t1.severity);
                    }
                });
            }
        });
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

    private void sortAll(Spinner spinner, int i) {
        recordingsList.sort(new Comparator<Recording>() {
            @Override
            public int compare(Recording recording, Recording t1) {
                String date1 = recording.videoData.split(", Date: ")[1].split(", Length: ")[0];
                String date2 = t1.videoData.split(", Date: ")[1].split(", Length: ")[0];
                SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
                Date d1 = null;
                Date d2 = null;
                try {
                    d1 = format.parse(date1);
                    d2 = format.parse(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (spinner.getItemAtPosition(i).equals("Most Recent")) {
                    return -d1.compareTo(d2);
                } else if (spinner.getItemAtPosition(i).equals("Least Recent")) {
                    return d1.compareTo(d2);
                } else {
                    return -recording.severity.compareTo(t1.severity);
                }
            }
        });

        Collections.sort(recordingsList.videoData, new Comparator<String>() {
            @Override
            public int compare(String string, String t1) {
                String date1 = string.split(", Date: ")[1].split(", Length: ")[0];
                String date2 = t1.split(", Date: ")[1].split(", Length: ")[0];
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d1 = null;
                Date d2 = null;
                try {
                    d1 = format.parse(date1);
                    d2 = format.parse(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (spinner.getItemAtPosition(i).equals("Most Recent")) {
                    return -d1.compareTo(d2);
                } else if (spinner.getItemAtPosition(i).equals("Least Recent")) {
                    return d1.compareTo(d2);
                } else {
                    String severity1 = string.split("Severity: ")[1].split(", Date: ")[0];
                    String severity2 = t1.split("Severity: ")[1].split(", Date: ")[0];
                    return -severity1.compareTo(severity2);
                }
            }
        });

        recordingsList.notifyDataSetChanged();
    }

}