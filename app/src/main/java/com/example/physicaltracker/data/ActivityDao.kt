package com.example.physicaltracker.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ActivityDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addActivity(newActivity: ActivityEntity)

    @Query("SELECT * FROM activity_table ORDER BY date DESC")
    fun readAllData(): LiveData<List<ActivityEntity>>

    @Query("SELECT * FROM activity_table ORDER BY duration DESC")
    fun readAllDataSortedByDuration(): LiveData<List<ActivityEntity>>

    @Query("SELECT * FROM activity_table WHERE type = :activityType")
    fun readAllDataByType(activityType: String): LiveData<List<ActivityEntity>>

    @Query("SELECT * FROM activity_table WHERE type = :activityType ORDER BY duration DESC")
    fun readAllDataByTypeSortedByDuration(activityType: String): LiveData<List<ActivityEntity>>

    @Query("SELECT * FROM activity_table WHERE date BETWEEN :startOfDay AND :endOfDay")
    fun getActivitiesByDate(startOfDay: Long, endOfDay: Long): LiveData<List<ActivityEntity>>

    @Query("SELECT * FROM activity_table ORDER BY startTime ASC")
    fun readAllDataSortedByStartTime(): List<ActivityEntity>
}

