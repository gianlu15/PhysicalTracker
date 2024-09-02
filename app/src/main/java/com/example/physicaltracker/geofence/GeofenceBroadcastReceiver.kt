package com.example.physicaltracker.geofence

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.physicaltracker.MainActivity
import com.example.physicaltracker.R
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "geofence_channel"
        const val NOTIFICATION_ID = 1001
    }

    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent != null) {
            if (geofencingEvent.hasError()) {
                val errorMessage = geofencingEvent.errorCode
                Log.e("GeofenceBroadcastReceiver", "Error code: $errorMessage")
                return
            }
        }

        val geofenceTransition = geofencingEvent?.geofenceTransition

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            showNotification(context, "Entered geofence area")
            Log.i("GeofenceBroadcastReceiver", "Entered geofence area")
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            showNotification(context, "Exited geofence area")
            Log.i("GeofenceBroadcastReceiver", "Exited geofence area")
        } else {
            Log.e("GeofenceBroadcastReceiver", "Unknown geofence transition")
        }
    }

    private fun showNotification(context: Context, message: String) {
        createNotificationChannel(context)

        // Controlla se l'app ha il permesso per inviare notifiche
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_physical)
                .setContentTitle("Geofence Alert")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                notify(NOTIFICATION_ID, builder.build())
            }
        } else {
            Log.e("GeofenceBroadcastReceiver", "Permission for notifications not granted")
            // Qui puoi anche aggiungere una logica per gestire il caso in cui il permesso non sia concesso
        }
    }


    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Geofence Channel"
            val descriptionText = "Channel for geofence alerts"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
