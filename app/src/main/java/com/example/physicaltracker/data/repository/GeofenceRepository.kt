package com.example.physicaltracker.data.repository

import androidx.lifecycle.LiveData
import com.example.physicaltracker.data.dao.GeofenceDao
import com.example.physicaltracker.data.entity.GeofenceEntity

class GeofenceRepository(private val geofenceDao: GeofenceDao) {

    val allGeofences: LiveData<List<GeofenceEntity>> = geofenceDao.getAllGeofences()

    suspend fun insert(geofence: GeofenceEntity) {
        geofenceDao.insertGeofence(geofence)
    }

    suspend fun delete(geofence: GeofenceEntity) {
        geofenceDao.deleteGeofence(geofence)
    }

    suspend fun getGeofenceById(geofenceId: String): GeofenceEntity? {
        return geofenceDao.getGeofenceById(geofenceId)
    }
}
