package com.example.smarticompanionapp;

import android.net.Uri;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import com.arthenica.mobileffmpeg.Config;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class RecordingTest {

    @Test
    public void RecordingTest() throws InterruptedException {


        Recording recording = new Recording();
        File video;
        try {
            InputStream vid = InstrumentationRegistry.getInstrumentation().getTargetContext().getResources().openRawResource(R.raw.test_video_0);
            video = new File(InstrumentationRegistry.getInstrumentation().getTargetContext().getFilesDir(), "test_video_0.mp4");
            OutputStream vidout = new FileOutputStream(video);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = vid.read(buffer)) != -1) {
                vidout.write(buffer, 0, bytesRead);
            }

            recording = new Recording("today", 10.0, "30:00",
                    Uri.parse(video.getAbsolutePath()));
        } catch (
                IOException e) {
            Log.i(Config.TAG, "IO fail");
        }

        assert(recording.videoData.equals("Severity: 10.0, Date: today, Length: 30:00"));
        assert(recording.thumbnail != null);
        assert(recording.uri != null);
        assert(recording.severity.equals(10.0));
    }
}
