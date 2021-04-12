/*
 *  This Interface is a data access object that serves
 *  to provide the template for the interaction methods
 *  with the local recordings database
 *
 *  The methods are straightforward in function, based
 * on their names
 */

package com.example.smarticompanionapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecordingDoa {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RecordingEntity recordingEntity);

    @Delete
    void delete(RecordingEntity recordingEntity);

    @Query("DELETE FROM RecordingTable")
    void deleteAll();

    @Query("SELECT * FROM RecordingTable")
    List<RecordingEntity> getAll();

    @Query("DELETE FROM RecordingTable WHERE uri = :uri")
    abstract void deleteByUri(String uri);
}
