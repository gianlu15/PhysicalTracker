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

    @Query("SELECT * FROM activity_table")
    fun readAllData(): LiveData<List<ActivityEntity>>
}
