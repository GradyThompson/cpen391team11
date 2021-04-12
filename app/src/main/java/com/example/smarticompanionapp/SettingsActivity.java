/*
 * This class represents the Settings screen and its the methods required to send
 * settings to the desired remote location
 *
 * Powered by 2 xml files, settings_activity which provides the
 * Buttons at the bottom of the screen, and root_preferences.xml,
 * which provides for the different settings options
 */

package com.example.smarticompanionapp;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import android.app.Fragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Settings");
        }

        //submits the saved preferences to the required remote location
        Button submitButton = (Button) findViewById(R.id.submit);
        submitButton.setOnClickListener(v -> {
            final Gson g = new Gson();
            final JSONObject object = new JSONObject();
            final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = "";

            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(getApplicationContext(),"Settings Changed", Toast.LENGTH_SHORT);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println(error);
                }
            });
            queue.add(jsonObjectRequest);
        });

        //Returns all settings to their defaults and reloads the preference fragment
        Button defaultButton = (Button) findViewById(R.id.defaultButton);
        defaultButton.setOnClickListener(v -> {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.commit();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        });
    }


    public static class SettingsFragment extends PreferenceFragmentCompat {
        private ListPreference maxTime;
        private ListPreference minTime;
        private ListPreference retTime;
        private ListPreference bitrate;
        private SwitchPreferenceCompat pushNotif;
        private SwitchPreferenceCompat physNotif;
        private ListPreference severityThres;
        private SettingsResult settings;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }

        /*
         * this method obtains the required preference fields and puts them into an object
         * to store them temporarily
         */
        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            settings = new SettingsResult();

            maxTime = getPreferenceManager().findPreference("max_time");
            maxTime.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    settings.setMaxLength(Integer.parseInt((String) newValue));
                    return true;
                }
            });

            minTime = getPreferenceManager().findPreference("min_time");
            minTime.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    settings.setMinLength(Integer.parseInt((String) newValue));
                    return true;
                }
            });

            retTime = getPreferenceManager().findPreference("retention_time");
            retTime.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    settings.setSaveTime(Integer.parseInt((String) newValue));
                    return true;
                }
            });

            bitrate = getPreferenceManager().findPreference("bitrate");
            bitrate.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    settings.setBitRate(Integer.parseInt((String) newValue));
                    return true;
                }
            });

            pushNotif = getPreferenceManager().findPreference("Push_notifications");
            pushNotif.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    settings.setPushNotifs((Boolean) newValue);
                    return true;
                }
            });

            physNotif = getPreferenceManager().findPreference("Physical_notifications");
            physNotif.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    settings.setPhysNotifs((Boolean) newValue);
                    return true;
                }
            });

            severityThres = getPreferenceManager().findPreference("severity_threshold");
            severityThres.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    settings.setSeverityThres(Integer.parseInt((String) newValue));
                    return true;
                }
            });
        }
    }
}