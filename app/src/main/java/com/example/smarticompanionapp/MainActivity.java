/*
 * This activity represents the initial connecting screen of the app
 * Has one UI element, a button that when pressed, tries to connect the app
 * to the hardware device through bluetooth/wifi, then goes to the recordings screen
 *
 * As of this moment the button just connects to the recordings activity
 */

package com.example.smarticompanionapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseStorage storage = FirebaseStorage.getInstance();
    SharedPreferences cameraToken;

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
            if (cameraToken.getString("Token", null) == null) {
                Intent bluetoothIntent = new Intent(MainActivity.this, BluetoothActivity.class);
                startActivity(bluetoothIntent);
            } else {
                getVideos();
            }
        });

        Button bypassButton = (Button) findViewById(R.id.bypass_button);
        bypassButton.setOnClickListener(v -> {
            getVideos();
        });
    }

    private void getVideos() {
        final Gson g = new Gson();
        final JSONArray object = new JSONArray();
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://18.222.192.144:3000/getVid";

        final JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, object,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<VideoResult> videoList = new ArrayList<>();
                        try {
                            Log.d("JsonArray", response.toString());
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jresponse = response.getJSONObject(i);
                                String url = jresponse.get("Uri").toString();
                                System.out.println(url);
                                String date = jresponse.get("Date").toString();

                                //String severity = jresponse.get("Severity").toString();
                                //String length = jresponse.get("Length").toString();
                                String severity = "0.0";
                                String length = "length placeholder";

                                StorageReference ref = storage.getReferenceFromUrl(url);
                                File localFile = File.createTempFile("video" + i, "mp4");
                                Uri u = Uri.parse(localFile.getAbsolutePath());

                                ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
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

                                videoList.add(new VideoResult(u, date, severity, length));

                                System.out.println(url);
                                System.out.println(date);
                                System.out.println("iterate loop");
                            }

                            //videoList = Arrays.asList(g.fromJson(response.get("").toString(), VideoResult[].class));
                            Intent recordingsIntent = new Intent(MainActivity.this, RecordingsActivity.class);
                            recordingsIntent.putParcelableArrayListExtra("videos", (ArrayList<? extends Parcelable>) videoList);
                            startActivity(recordingsIntent);
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