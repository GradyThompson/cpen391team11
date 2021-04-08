package com.example.smarticompanionapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {RecordingEntity.class}, version = 1, exportSchema = false)
public abstract class RecordingRoomDatabase extends RoomDatabase {

    public abstract RecordingDoa recordingDoa();

    private static volatile RecordingRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static RecordingRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RecordingRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RecordingRoomDatabase.class, "recording_database")
                            .allowMainThreadQueries().build();

                }
            }
        }
        return INSTANCE;
    }

}
