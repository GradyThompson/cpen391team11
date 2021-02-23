/*
 * This activity represents the initial connecting screen of the app
 * Has one UI element, a button that when pressed, tries to connect the app
 * to the hardware device through bluetooth/wifi, then goes to the recordings screen
 *
 * As of this moment the button just connects to the recordings activity
 */

package com.example.smarticompanionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

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
            Intent recordingsIntent = new Intent(MainActivity.this, RecordingsActivity.class);
            startActivity(recordingsIntent);
        });

        Button bluetoothButton = (Button) findViewById(R.id.bluetooth_button);
        bluetoothButton.setOnClickListener(v -> {
            Intent recordingsIntent = new Intent(MainActivity.this, RecordingsActivity.class);
            startActivity(recordingsIntent);
        });
    }
}