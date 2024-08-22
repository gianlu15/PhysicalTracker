package com.example.physicaltracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_table")
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,
    var duration: Long,
    val steps: Int? = null,
    val startTime: Long,
    var endTime: Long? = null,
    val date: Long
)
