package com.example.smarticompanionapp;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class RecordingRepository {
    private RecordingDoa mRecordingDoa;
    private List<RecordingEntity> mAllRecordings;

    RecordingRepository(Application application) {
        RecordingRoomDatabase db = RecordingRoomDatabase.getDatabase(application);
        mRecordingDoa = db.recordingDoa();
        mAllRecordings = mRecordingDoa.getAll();
    }

    List<RecordingEntity> getAll() {
        return mAllRecordings;
    }

    void insert(RecordingEntity recordingEntity) {
        RecordingRoomDatabase.databaseWriteExecutor.execute(() -> {
            mRecordingDoa.insert(recordingEntity);
        });
    }

    void delete(RecordingEntity recordingEntity) {
        RecordingRoomDatabase.databaseWriteExecutor.execute(() -> {
            mRecordingDoa.delete(recordingEntity);
        });
    }

    void deleteAll() {
        RecordingRoomDatabase.databaseWriteExecutor.execute(() ->{
            mRecordingDoa.deleteAll();
        });
    }

    void deleteByUri(String uri) {
        RecordingRoomDatabase.databaseWriteExecutor.execute(() -> {
            mRecordingDoa.deleteByUri(uri);
        });
    }
}
