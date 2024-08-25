package com.example.physicaltracker.data

import androidx.lifecycle.LiveData

//Access to multiple data sources

class ActivityRepository(private val activityDao: ActivityDao) {

    val readAllData: LiveData<List<ActivityEntity>> = activityDao.readAllData()
    val readAllDataSortedByDuration: LiveData<List<ActivityEntity>> = activityDao.readAllDataSortedByDuration()

    fun readAllDataByType(activityType: String): LiveData<List<ActivityEntity>> {
        return activityDao.readAllDataByType(activityType)
    }

    fun readAllDataByTypeSortedByDuration(activityType: String): LiveData<List<ActivityEntity>> {
        return activityDao.readAllDataByTypeSortedByDuration(activityType)
    }
    suspend fun addActivity(activity: ActivityEntity){
        activityDao.addActivity(activity)
    }

    fun getActivitiesByDate(startOfDay: Long, endOfDay: Long): LiveData<List<ActivityEntity>> {
        return activityDao.getActivitiesByDate(startOfDay, endOfDay)
    }
}