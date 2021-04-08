

package com.example.smarticompanionapp;

import android.net.Uri;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import com.arthenica.mobileffmpeg.Config;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RecordingsArrayTest {
    private Recording rec1, rec2, rec3;
    private RecordingsArray recArray = new RecordingsArray();

    //set up 3 Recordings to be used with the Recordings array
    @Before
    public void Setup(){
        File video1, video2, video3;
        try {
            InputStream vid = InstrumentationRegistry.getInstrumentation().getTargetContext().getResources().openRawResource(R.raw.test_video_0);
            video1 = new File(InstrumentationRegistry.getInstrumentation().getTargetContext().getFilesDir(), "test_video_0.mp4");
            video2 = new File(InstrumentationRegistry.getInstrumentation().getTargetContext().getFilesDir(), "test_video_0.mp4");
            video3 = new File(InstrumentationRegistry.getInstrumentation().getTargetContext().getFilesDir(), "test_video_0.mp4");
            OutputStream vidout1 = new FileOutputStream(video1);
            OutputStream vidout2 = new FileOutputStream(video1);
            OutputStream vidout3 = new FileOutputStream(video1);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = vid.read(buffer)) != -1) {
                vidout1.write(buffer, 0, bytesRead);
                vidout2.write(buffer, 0, bytesRead);
                vidout3.write(buffer, 0, bytesRead);
            }

            //Three recordings will have the same video, but different video data
            //for the sake of testing
            rec1 = new Recording("today", 10.0, "30:00",
                    Uri.parse(video1.getAbsolutePath()));

            rec2 = new Recording("yesterday", 9.0, "20:00",
                    Uri.parse(video2.getAbsolutePath()));

            rec3 = new Recording("tuesday", 8.5, "10:00",
                    Uri.parse(video3.getAbsolutePath()));

        } catch (
                IOException e) {
            Log.i(Config.TAG, "IO fail");
        }

    }

    //Test that adding sort the recordings and recording data in order
    @Test
    public void AddTest(){
        recArray.add(rec3);
        recArray.add(rec1);
        recArray.add(rec2);

        assert(recArray.getRecord(0).equals(rec1));
        assert(recArray.getRecord(1).equals(rec2));
        assert(recArray.getRecord(2).equals(rec3));

        assert(recArray.getVideoDataList().get(0).equals(rec1.videoData));
        assert(recArray.getVideoDataList().get(1).equals(rec2.videoData));
        assert(recArray.getVideoDataList().get(2).equals(rec3.videoData));
    }

    //Test removing, and that it does not delete the file
    @Test
    public void removeTest(){
        recArray.add(rec3);
        recArray.add(rec1);
        recArray.add(rec2);

        recArray.remove(0);

        assert (!(recArray.getRecord(0).equals(rec1)));
        assert(!recArray.getVideoDataList().get(0).equals(rec1.videoData));

        File tempFile = new File(String.valueOf(rec1.uri));
        assert (tempFile.exists());
    }

    //Test deleting, and that it deletes the file
    @Test
    public void deleteTest(){
        recArray.add(rec3);
        recArray.add(rec2);

        recArray.delete(0);

        assert (!(recArray.getRecord(0).equals(rec2)));
        assert(!recArray.getVideoDataList().get(0).equals(rec2.videoData));

        File tempFile = new File(String.valueOf(rec2.uri));
        assert (!tempFile.exists());
    }
}
