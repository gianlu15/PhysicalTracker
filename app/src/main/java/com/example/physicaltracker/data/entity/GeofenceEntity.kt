package com.example.physicaltracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "geofence_table")
data class GeofenceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val geofenceId: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Float,
    val transitionType: Int,
    val name: String
)
