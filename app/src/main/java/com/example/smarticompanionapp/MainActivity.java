/*
 * This activity represents the initial connecting screen of the app
 * Has one UI element, a button that when pressed, tries to connect the app
 * to the hardware device through bluetooth/wifi, then goes to the recordings screen
 *
 * As of this moment the button just connects to the recordings activity
 */

package com.example.smarticompanionapp;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String token = FirebaseInstanceId.getInstance().getToken();
        //Log.d("TOKEN", token);
            //the line above caused the app to crash, commented it out to work on other parts of the app

        Button usbButton = (Button) findViewById(R.id.usb_button);
        usbButton.setOnClickListener(v -> {
            Intent recordingsIntent = new Intent(MainActivity.this, RecordingsActivity.class);
            startActivity(recordingsIntent);
        });

        Button wifiButton = (Button) findViewById(R.id.wifi_button);
        wifiButton.setOnClickListener(v -> {
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
                                    Uri uri = Uri.parse(jresponse.get("Uri").toString());
                                    String date = jresponse.get("Date").toString();
                                    System.out.println(uri);
                                    System.out.println(date);
                                    videoList.add(new VideoResult(uri, date));
                                }
                                //videoList = Arrays.asList(g.fromJson(response.get("").toString(), VideoResult[].class));
                                Intent recordingsIntent = new Intent(MainActivity.this, RecordingsActivity.class);
                                recordingsIntent.putParcelableArrayListExtra("videos", (ArrayList<? extends Parcelable>) videoList);
                                startActivity(recordingsIntent);
                                Log.d("GETREQUEST", "successful");
                            } catch (JSONException e) {
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
        });

        Button bluetoothButton = (Button) findViewById(R.id.bluetooth_button);
        bluetoothButton.setOnClickListener(v -> {
            //if (BluetoothAdapter.getDefaultAdapter() == null) {
               // Toast toast = Toast.makeText(this.getApplicationContext(),"Bluetooth Not Supported", Toast.LENGTH_SHORT);
               // toast.show();
            //} else {
                Intent bluetoothIntent = new Intent(MainActivity.this, BluetoothActivity.class);
                startActivity(bluetoothIntent);
            //}
        });
    }
}