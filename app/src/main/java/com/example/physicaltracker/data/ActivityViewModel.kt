package com.example.physicaltracker.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Provide data to UI and survive configuration changes, act as communication center between Repository and UI

class ActivityViewModel(application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<List<ActivityEntity>>
    private val repository: ActivityRepository

    var isChronometerRunning: Boolean = false
    var chronometerBase: Long = 0L
    var pauseOffset: Long = 0L

    init {
        val activityDao = ActivityDatabase.getDatabase(application).activityDao()
        repository = ActivityRepository(activityDao)
        readAllData = repository.readAllData
    }

    fun addActivity(activity: ActivityEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addActivity(activity)
        }
    }
}
