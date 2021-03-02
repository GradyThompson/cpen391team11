package com.example.smarticompanionapp;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class VideoActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_view);

        VideoView vid;
        MediaController m;
        vid = (VideoView) findViewById(R.id.videoView);
        Uri u = (Uri) getIntent().getExtras().get("vid");
        String s = (String) getIntent().getExtras().get("dt");
        vid.setVideoURI(u);
        vid.start();
    }
}
