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
    val readAllDataSortedByDuration: LiveData<List<ActivityEntity>>
    private val repository: ActivityRepository

    var isChronometerRunning: Boolean = false
    var chronometerBase: Long = 0L
    var pauseOffset: Long = 0L

    init {
        val activityDao = ActivityDatabase.getDatabase(application).activityDao()
        repository = ActivityRepository(activityDao)
        readAllData = repository.readAllData
        readAllDataSortedByDuration = repository.readAllDataSortedByDuration
    }

    fun addActivity(activity: ActivityEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addActivity(activity)
        }
    }

    fun getAllDataByType(activityType: String): LiveData<List<ActivityEntity>> {
        return repository.readAllDataByType(activityType)
    }

    fun getAllDataByTypeSortedByDuration(activityType: String): LiveData<List<ActivityEntity>> {
        return repository.readAllDataByTypeSortedByDuration(activityType)
    }

    fun getActivitiesByDate(startOfDay: Long, endOfDay: Long): LiveData<List<ActivityEntity>> {
        return repository.getActivitiesByDate(startOfDay, endOfDay)
    }
}
