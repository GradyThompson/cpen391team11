/*
 * This activity represents the Recordings screen of the app
 * Contains a scrollable list of recordings that are sorted based on severity
 * Has a button in the top right that brings up the settings activity.
 */

package com.example.smarticompanionapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class BluetoothActivity extends AppCompatActivity {

    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    private ListView lv;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    SharedPreferences cameraToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_activity);

        lv = findViewById(R.id.listView);
        cameraToken = getSharedPreferences("label", Context.MODE_PRIVATE);
        BA = BluetoothAdapter.getDefaultAdapter();

        if (BA == null) {
            Toast toast = Toast.makeText(this.getApplicationContext(),"Bluetooth Not Supported", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
        }
    }

    public void visible(View v){
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getVisible, 0);
    }

    public void list(View v){
        pairedDevices = BA.getBondedDevices();

        ArrayList list = new ArrayList();

        for(BluetoothDevice bt : pairedDevices) list.add(bt.getName());
        Toast.makeText(getApplicationContext(), "Showing Paired Devices",Toast.LENGTH_SHORT).show();

        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);

        lv.setAdapter(adapter);

        lv.setClickable(false);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BluetoothDevice camera = BA.getRemoteDevice((byte[]) lv.getItemAtPosition(i));
                try {
                    BluetoothSocket socket = camera.createInsecureRfcommSocketToServiceRecord(camera.getUuids()[0].getUuid());
                    socket.connect();
                    cameraToken.edit().putString("Token", socket.getInputStream().toString());

                    getVideos();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getVideos(){
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
                                Toast toast = Toast.makeText(BluetoothActivity.this,
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
                            }

                            //videoList = Arrays.asList(g.fromJson(response.get("").toString(), VideoResult[].class));
                            Intent recordingsIntent = new Intent(BluetoothActivity.this, RecordingsActivity.class);
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