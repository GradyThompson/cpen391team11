/*
 * This activity represents the initial connecting screen of the app
 * Has one UI element, a button that when pressed, tries to connect the app
 * to the hardware device through bluetooth/wifi, then goes to the recordings screen
 *
 * As of this moment the button just connects to the recordings activity
 */

package com.example.smarticompanionapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button usbButton = (Button) findViewById(R.id.usb_button);
        usbButton.setOnClickListener(v -> {
            Intent recordingsIntent = new Intent(MainActivity.this, RecordingsActivity.class);
            startActivity(recordingsIntent);
        });

        Button wifiButton = (Button) findViewById(R.id.wifi_button);
        wifiButton.setOnClickListener(v -> {
            final Gson g = new Gson();
            final JSONObject object = new JSONObject();
            final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = "";

            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            List<VideoResult> videoList;
                            try {
                                videoList = Arrays.asList(g.fromJson(response.get("").toString(), VideoResult[].class));
                                Intent recordingsIntent = new Intent(MainActivity.this, RecordingsActivity.class);
                                recordingsIntent.putExtra("videos", (Parcelable) videoList);
                                startActivity(recordingsIntent);
                            } catch (JSONException e) {
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
            Toast.makeText(getApplicationContext(), "Bluetooth",Toast.LENGTH_SHORT).show();
            Intent bluetoothIntent = new Intent(MainActivity.this, BluetoothActivity.class);
            startActivity(bluetoothIntent);
        });
    }
}