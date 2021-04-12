/*
 * This activity represents the video player, allowing the viewing
 * of recordings within the app
 */

package com.example.smarticompanionapp;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class VideoActivity extends AppCompatActivity {

    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_view);

        VideoView vid;
        MediaController m = new MediaController(this);
        vid = (VideoView) findViewById(R.id.videoView);

        Uri u = (Uri) getIntent().getExtras().get("vid");
        String s = (String) getIntent().getExtras().get("dt");
        vid.setVideoURI(u);
        vid.setMediaController(m);
        m.setAnchorView(vid);

        vid.start();
    }
}
