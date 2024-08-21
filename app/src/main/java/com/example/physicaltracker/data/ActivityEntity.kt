package com.example.physicaltracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_table")
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,   // Tipo di attività, es. "Walking", "Sitting", "Driving"
    var duration: Long, // Durata dell'attività in millisecondi
    val steps: Int? = null // Numero di passi, opzionale
)
