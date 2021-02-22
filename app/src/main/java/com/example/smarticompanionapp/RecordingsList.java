package com.example.smarticompanionapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RecordingsList extends ArrayAdapter<String> {
    private Context context;
    private ArrayList<String> StringArray;

    public RecordingsList (Context context, int textViewResourceId, ArrayList<String> StringArray) {
        super(context, textViewResourceId,StringArray);
        this.context = context;
        this.StringArray = StringArray;
    }

    @Override
    public View getView(int position, View contextView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.list_row, parent, false);

        ImageView thumbnail = (ImageView) row.findViewById(R.id.thumbnail);
        thumbnail.setImageResource(R.drawable.ic_launcher_foreground); //placeholder, should have video thumbnail
        thumbnail.setVisibility(View.VISIBLE);

        TextView data = (TextView) row.findViewById(R.id.videoData);
        data.setText((StringArray.get(position)));

        ImageView optionIcon = (ImageView) row.findViewById(R.id.optionsIcon);
        optionIcon.setImageResource(R.drawable.ic_launcher_background); //placeholder, should be a menu icon
        return row;
    }
}
