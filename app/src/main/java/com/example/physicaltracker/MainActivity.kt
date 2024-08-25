package com.example.physicaltracker

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.physicaltracker.calendar.CalendarFragment
import com.example.physicaltracker.charts.ChartsFragment
import com.example.physicaltracker.history.HistoryFragment
import com.example.physicaltracker.record.RecordFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

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
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFrame, fragment)
            commit()
        }

    private fun hasActivityRecognitionPermission() =
        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission(){
        var permissionTORequest = mutableListOf<String>()
        if(!hasActivityRecognitionPermission()){
            permissionTORequest.add(Manifest.permission.ACTIVITY_RECOGNITION)
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
}
