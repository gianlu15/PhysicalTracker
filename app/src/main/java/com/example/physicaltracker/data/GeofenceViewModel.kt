package com.example.physicaltracker.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.physicaltracker.data.db.AppDatabase
import com.example.physicaltracker.data.entity.GeofenceEntity
import com.example.physicaltracker.data.repository.GeofenceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GeofenceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GeofenceRepository
    val allGeofences: LiveData<List<GeofenceEntity>>

    init {
        val geofenceDao = AppDatabase.getDatabase(application).geofenceDao()
        repository = GeofenceRepository(geofenceDao)
        allGeofences = repository.allGeofences
    }

    fun insert(geofence: GeofenceEntity) {
        Log.i("Geofence", "evento inserito")
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(geofence)
        }
    }

    fun delete(geofence: GeofenceEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(geofence)
        }
    }

    fun getGeofenceById(geofenceId: String, callback: (GeofenceEntity?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val geofence = repository.getGeofenceById(geofenceId)
            callback(geofence)
        }
    }
}
