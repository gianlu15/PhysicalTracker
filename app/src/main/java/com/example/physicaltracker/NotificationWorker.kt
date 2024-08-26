package com.example.physicaltracker

import android.app.Application
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.physicaltracker.data.ActivityViewModel

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        // Leggi lo stato del cronometro da SharedPreferences
        val sharedPreferences = applicationContext.getSharedPreferences("activity_tracker_prefs", Context.MODE_PRIVATE)
        val isChronometerRunning = sharedPreferences.getBoolean("is_chronometer_running", false)

        Log.i("NotificationWorker", "Il cronometro è $isChronometerRunning")

        // Controlla se il cronometro è in esecuzione
        if (!isChronometerRunning)  {
            showNotification()
        }
        return Result.success()
    }

    private fun showNotification() {
        val channelId = "activity_reminder_channel"
        val notificationId = 1
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification: Notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_physical)
            .setContentTitle("Time to Record Your Activity")
            .setContentText("Don't forget to log your physical activities!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}
