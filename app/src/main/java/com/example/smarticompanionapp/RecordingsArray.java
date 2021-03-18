/*
 *  This Class is used to structure recordings so that recordings as a whole can be more
 *  easily utilized by the app.
 */


package com.example.smarticompanionapp;

import android.net.Uri;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

public class RecordingsArray {

    /*NOTE: When changes are made to either of these arrays, adjustments must be
    * made so that the two arrays always have corresponding ordering
    * Both arrays should be sorted so that the highest severity recordings go in front
    * There should be no duplicate entries*/

    //these will be public now for easy testing, later want them to be private to prevent exposing
    //these arrays directly
    public LinkedList<Recording> RecArray = new LinkedList<>();
    public ArrayList<String> videoDataList = new ArrayList<>();


    /*
     *  Adds a recording to the recordingsArray in the proper position,
     *      and adds the corresponding video data to the videoDataList
     *  A recording is added into an index if the severity of the recording at that
     *      index is equal to or lesser than it
     *  If there are no smaller or equal severity recordings, then the recording will be appended
     *      to the array
     */
    public void add(Recording record) {
        int size = RecArray.size();
        for (int x = 0; x < size; x++){
            if (record.severity >= RecArray.get(x).severity) {
                RecArray.add(x, record);
                videoDataList.add(x, record.videoData);
                break;
            }
        }
        if (size == RecArray.size()) {
            RecArray.add(record);
            videoDataList.add(record.videoData);
        }
    }
    /*
     *  Removes a recording from the structure
     *  Since there should be no repeats, just removes first object matching
     *  Returns 1 if the recording has been removed, 0 if no matching recordings are found
     */
    public int remove(Recording recording){
        for (int x = 0; x < RecArray.size(); x++){
            if (recording.equals(RecArray.get(x))) {
                RecArray.remove(x);
                videoDataList.remove(x);
                return 1;
            }
        }
        return 0;
    }

    public int remove(int x){
        if (x < RecArray.size() && x >= 0) {
            RecArray.remove(x);
            videoDataList.remove(x);
            return 1;
        }
        return 0;
    }

    /*
     *  Removes a recording from the structure and deletes it off app and remote storage
     *  Since there should be no repeats, just deletes first object matching
     *  Returns 1 if the recording has been deletes, 0 if no matching recordings are found
     *  Must send a signal to the cloud database and/or the device to delete the recording
     */
    public int delete(Recording recording) {
        if (remove(recording) == 1) {
            //delete recording from storage
            //send signal to remote database to delete recording
            Uri uri = recording.uri;
            File delete = new File(uri.getPath());
            if (delete.exists()) {
                if (delete.delete()) {
                    System.out.println("file deleted" + uri.getPath());
                }
                else {
                    System.out.println("file not deleted" + uri.getPath());
                }
            }
            return 1;
        }
        return 0;
    }

    public int delete(int x) {

        return delete(RecArray.get(x));
    }

    /*
     *  Returns the recording in the given index
     */
    public Recording getRecord(int x) {
        return RecArray.get(x);
    }

    /*
     *  Returns the videoData for the recording in the given index
     */
    public String getVideoData(int x) {
        return videoDataList.get(x);
    }

    /*
     *  Returns a copy of the videoData array
     *  Be careful when using this, copy does not update with the RecordingsArray
     */
    public ArrayList<String> getVideoDataList(){
        return new ArrayList<>(videoDataList);
    }
}
