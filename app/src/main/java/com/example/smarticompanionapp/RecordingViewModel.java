/*
 *  This class allows activities to interact with the local
 *  recording database. When a method within an activity wishes
 *  to interact with the database, it should instantiate an instance
 *  of this
 */

package com.example.smarticompanionapp;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.AndroidViewModel;

import java.util.List;

public class RecordingViewModel extends AndroidViewModel {

    private RecordingRepository mRepository;
    private final List<RecordingEntity> mAllRecordings;

    public RecordingViewModel (Application application) {
        super(application);
        mRepository = new RecordingRepository(application);
        mAllRecordings = mRepository.getAll();
    }

    List<RecordingEntity> getAllRecordings() {return mAllRecordings;}

    public void insert(RecordingEntity recordingEntity) {mRepository.insert(recordingEntity);}

    public void delete(RecordingEntity recordingEntity) {mRepository.delete(recordingEntity);}

    public void deleteAll() { mRepository.deleteAll();}

    public void deleteByUri(Uri uri) {mRepository.deleteByUri(uri.toString());}

}
