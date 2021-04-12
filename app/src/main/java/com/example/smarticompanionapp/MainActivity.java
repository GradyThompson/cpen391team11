/*
 * This activity represents the initial connecting screen of the app
 * It has three UI elements, a Button connecting leading to a Bluetooth
 * connection screen, one that downloads videos from the remote server,
 * And one that connects to the list of downloaded recordings
 *
 */

package com.example.smarticompanionapp;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

import static com.google.android.gms.tasks.Task.*;
import static com.google.android.gms.tasks.Tasks.await;
import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    SharedPreferences cameraToken;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    private RecordingViewModel mRecordViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraToken = getSharedPreferences("label", Context.MODE_PRIVATE);
        String token = FirebaseInstanceId.getInstance().getToken();

        Button connectButton = (Button) findViewById(R.id.connect_button);
        connectButton.setOnClickListener(v -> {
            if (cameraToken.getString("Token", null) == null) {
                Intent bluetoothIntent = new Intent(MainActivity.this, BluetoothActivity.class);
                startActivity(bluetoothIntent);
            } else {
                Intent recordingsIntent = new Intent(MainActivity.this, RecordingsActivity.class);
                startActivity(recordingsIntent);
            }
        });

        Button bypassButton = (Button) findViewById(R.id.bypass_button);
        bypassButton.setOnClickListener(v -> {
            getVideos();
        });

        Button viewRecButton = (Button) findViewById(R.id.view_rec_button);
        viewRecButton.setOnClickListener(v -> {
            Intent recordingsIntent = new Intent(MainActivity.this, RecordingsActivity.class);
            startActivity(recordingsIntent);
        });


        mRecordViewModel = new ViewModelProvider(this).get(RecordingViewModel.class);
    }

    /*
     * This method downloads recordings and their associated data from the server.
     *
     * It obtains the fields of interest, saving the video to local storage and placing
     * the associated data into a RecordingEntity that gets placed into a local database,
     * to be used by the RecordingsActivity
     */
    private void getVideos() {
        final Gson g = new Gson();
        final JSONArray object = new JSONArray();
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://35.239.13.217:3000/getVid";

        Toast toast = Toast.makeText(MainActivity.this,
                "downloading videos", Toast.LENGTH_SHORT);
        toast.show();

        final JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, object,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<VideoResult> videoList = new ArrayList<>();

                        try {
                            Log.d("JsonArray", response.toString());
                            mRecordViewModel.deleteAll();

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jresponse = response.getJSONObject(i);
                                String url = jresponse.get("Url").toString();
                                //System.out.println(url);
                                String date = jresponse.get("Date").toString();
                                String severity = jresponse.get("Severity").toString();
                                String length = jresponse.get("Length").toString();

                                StorageReference ref = storage.getReferenceFromUrl(url);
                                File localFile = File.createTempFile("video" + i, "mp4");
                                Uri u = Uri.parse(localFile.getAbsolutePath());

                                FileDownloadTask task = ref.getFile(localFile);

                                task.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        // local temp file has been created
                                        System.out.println("download successful");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // handle any errors
                                    }
                                });

                                //crude way to do this, but design requires that download tasks
                                //are complete before progressing
                                while(!task.isComplete()){
                                }

                                videoList.add(new VideoResult(u, date, severity, length));

                                System.out.println(url);
                                System.out.println(date);
                                System.out.println("iterate loop");

                                RecordingEntity recordingEntity = new RecordingEntity();
                                recordingEntity.uri = u.toString();
                                recordingEntity.date = date;
                                recordingEntity.severity = Double.parseDouble(severity);
                                recordingEntity.length = length;
                                mRecordViewModel.insert(recordingEntity);

                            }
                            Toast toast = Toast.makeText(MainActivity.this,
                                    "Videos downloaded", Toast.LENGTH_SHORT);
                            toast.show();

                            Log.d("GETREQUEST", "successful");
                        } catch (JSONException | IOException e) {
                            Log.d("GETREQUEST", "didn't go through");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });
        queue.add(jsonObjectRequest);
    }
}
