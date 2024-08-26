package com.example.physicaltracker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.physicaltracker.calendar.CalendarFragment
import com.example.physicaltracker.charts.ChartsFragment
import com.example.physicaltracker.history.HistoryFragment
import com.example.physicaltracker.record.RecordFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // --------------    NAVBAR
        val chartsFragment = ChartsFragment()
        val recordFragment = RecordFragment()
        val historyFragment = HistoryFragment()
        var calendarFragment = CalendarFragment()

        setCurrentFragment(chartsFragment)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Imposta il listener per la selezione degli elementi
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.myStatistics -> setCurrentFragment(chartsFragment)
                R.id.myNewActivity -> setCurrentFragment(recordFragment)
                R.id.myHistory -> setCurrentFragment(historyFragment)
                R.id.myCalendar -> setCurrentFragment(calendarFragment)
            }
            true
        }

        requestPermission()
        createNotificationChannel(this)
        schedulePeriodicNotification()
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFrame, fragment)
            commit()
        }

    private fun hasActivityRecognitionPermission() =
        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED

    private fun hasNotificationPermission() =
        ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission(){
        var permissionTORequest = mutableListOf<String>()
        if(!hasActivityRecognitionPermission()){
            permissionTORequest.add(Manifest.permission.ACTIVITY_RECOGNITION)
        }

        if(!hasNotificationPermission()){
            permissionTORequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if(permissionTORequest.isNotEmpty()){
            ActivityCompat.requestPermissions(this, permissionTORequest.toTypedArray(), 0)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 0 && grantResults.isNotEmpty()){
            for(i in grantResults.indices){
                if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    Log.i("PERMISSION REQUEST", "${permissions[i]} granted.")
                }
            }
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "activity_reminder_channel"
            val channelName = "Activity Reminder"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Channel for activity reminders"
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun schedulePeriodicNotification() {
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(1, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(this).enqueue(workRequest)
    }
}
