package com.example.physicaltracker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.physicaltracker.data.dao.ActivityDao
import com.example.physicaltracker.data.dao.GeofenceDao
import com.example.physicaltracker.data.entity.ActivityEntity
import com.example.physicaltracker.data.entity.GeofenceEntity

// Contiene il database
@Database(entities = [ActivityEntity::class, GeofenceEntity::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun activityDao(): ActivityDao
    abstract fun geofenceDao(): GeofenceDao // Aggiungi questo metodo per il DAO Geofence

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE

            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
