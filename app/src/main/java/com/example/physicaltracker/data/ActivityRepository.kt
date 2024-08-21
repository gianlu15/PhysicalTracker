package com.example.physicaltracker.data

import androidx.lifecycle.LiveData

//Access to multiple data sources

class ActivityRepository(private val activityDao: ActivityDao) {

    val readAllData: LiveData<List<ActivityEntity>> = activityDao.readAllData()

    suspend fun addActivity(activity: ActivityEntity){
        activityDao.addActivity(activity)
    }
}