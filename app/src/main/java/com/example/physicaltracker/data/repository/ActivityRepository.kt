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

    suspend fun insertUnknownActivities() {
        Log.i("ActivityRepository", "Check effettuato")
        val activities = activityDao.readAllDataSortedByStartTime()

        for (i in 0 until activities.size - 1) {
            val currentActivity = activities[i]
            val nextActivity = activities[i + 1]

            val endTime = currentActivity.endTime ?: continue
            val startTimeNext = nextActivity.startTime


            // Calcola il gap tra le attività
            val gap = startTimeNext - endTime

            // Se il gap è maggiore di una soglia (es. 30 minuti), inserisci "unknown"
            if (gap > TimeUnit.MINUTES.toMillis(30)) {
                val unknownActivity = ActivityEntity(
                    type = "unknown",
                    duration = gap,
                    startTime = endTime,
                    endTime = startTimeNext,
                    date = endTime // Puoi usare endTime o un'altra logica per la data
                )
                activityDao.addActivity(unknownActivity)
            }
        }

    }

    private fun formatTime(timeInMillis: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault()) // Mostra solo l'ora e i minuti
        val date = Date(timeInMillis)
        return sdf.format(date)
    }
}