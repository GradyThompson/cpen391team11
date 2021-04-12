/*
 * This activity represents the initial connecting screen of the app
 * Has one UI element, a button that when pressed, tries to connect the app
 * to the hardware device through bluetooth/wifi, then goes to the recordings screen
 *
 * As of this moment the button just connects to the recordings activity
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
        //Log.d("TOKEN", token);
        //the line above caused the app to crash, commented it out to work on other parts of the app

        Button connectButton = (Button) findViewById(R.id.connect_button);
        connectButton.setOnClickListener(v -> {
            //if app has not been paired with a camera yet go to bluetooth
            if (cameraToken.getString("Token", null) == null) {
                Intent bluetoothIntent = new Intent(MainActivity.this, BluetoothActivity.class);
                startActivity(bluetoothIntent);
            } else {
                getVideos();
            }
        });

        //bypasses bluetooth if testing on emulator
        Button bypassButton = (Button) findViewById(R.id.bypass_button);
        bypassButton.setOnClickListener(v -> {
            getVideos();
        });

        Button viewRecButton = (Button) findViewById(R.id.view_rec_button);
        viewRecButton.setOnClickListener(v -> {
            Intent recordingsIntent = new Intent(MainActivity.this, RecordingsActivity.class);
            //recordingsIntent.putParcelableArrayListExtra("videos", (ArrayList<? extends Parcelable>) videoList);
            startActivity(recordingsIntent);
        });


        mRecordViewModel = new ViewModelProvider(this).get(RecordingViewModel.class);
    }

    private void getVideos() {
        final Gson g = new Gson();
        final JSONArray object = new JSONArray();
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://35.239.13.217:3000/getVid";


        final JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, object,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<VideoResult> videoList = new ArrayList<>();

                        try {
                            Log.d("JsonArray", response.toString());
                            mRecordViewModel.deleteAll();
                            for (int i = 0; i < response.length(); i++) {
                                Toast toast = Toast.makeText(MainActivity.this,
                                        "downloading videos", Toast.LENGTH_SHORT);
                                toast.show();
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
                            //videoList = Arrays.asList(g.fromJson(response.get("").toString(), VideoResult[].class));
                            //Intent recordingsIntent = new Intent(MainActivity.this, RecordingsActivity.class);
                            //recordingsIntent.putParcelableArrayListExtra("videos", (ArrayList<? extends Parcelable>) videoList);
                            //startActivity(recordingsIntent);


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
