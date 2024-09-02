package com.example.physicaltracker.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import com.example.physicaltracker.data.entity.GeofenceEntity

@Dao
interface GeofenceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeofence(geofence: GeofenceEntity)

    @Delete
    suspend fun deleteGeofence(geofence: GeofenceEntity)

    @Query("SELECT * FROM geofence_table WHERE geofenceId = :geofenceId")
    suspend fun getGeofenceById(geofenceId: String): GeofenceEntity?

    @Query("SELECT * FROM geofence_table")
    fun getAllGeofences(): LiveData<List<GeofenceEntity>>
}
