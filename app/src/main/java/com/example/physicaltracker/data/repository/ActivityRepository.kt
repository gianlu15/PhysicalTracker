package com.example.physicaltracker.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.physicaltracker.data.dao.ActivityDao
import com.example.physicaltracker.data.entity.ActivityEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

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

    //Function to add Unknown Activities when there is no reocord in 30mins
    suspend fun insertUnknownActivities() {
        val activities = activityDao.readAllDataSortedByStartTime()

        for (i in 0 until activities.size - 1) {
            val currentActivity = activities[i]
            val nextActivity = activities[i + 1]

            val endTime = currentActivity.endTime ?: continue
            val startTimeNext = nextActivity.startTime


            // Gap between two activities
            val gap = startTimeNext - endTime

            // Insert Unkonwn if gap > 30mins
            if (gap > TimeUnit.MINUTES.toMillis(30)) {
                val unknownActivity = ActivityEntity(
                    type = "unknown",
                    duration = gap,
                    startTime = endTime,
                    endTime = startTimeNext,
                    date = endTime
                )
                activityDao.addActivity(unknownActivity)
            }
        }

    }

}