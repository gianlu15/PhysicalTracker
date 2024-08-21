package com.example.physicaltracker

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Chronometer
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.physicaltracker.data.ActivityEntity
import com.example.physicaltracker.data.ActivityViewModel

class RecordFragment : Fragment(R.layout.fragment_record) {

    private lateinit var myActivityViewModel: ActivityViewModel

    private lateinit var chronometer: Chronometer
    private var isRunning = false
    private var pauseOffset: Long = 0

    private var currentActivity: ActivityEntity? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myActivityViewModel = ViewModelProvider(this).get(ActivityViewModel::class.java)

        chronometer = view.findViewById(R.id.chronometer)

        val activitySpinner: Spinner = view.findViewById(R.id.spActivityList)
        val activityTypes = resources.getStringArray(R.array.activity_types)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, activityTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        activitySpinner.adapter = adapter

        val btnStart: Button = view.findViewById(R.id.btnStart)
        val btnPauseResume: Button = view.findViewById(R.id.btnPauseResume)
        val btnStop: Button = view.findViewById(R.id.btnStop)

        btnPauseResume.isEnabled = false
        btnStop.isEnabled = false

        btnStart.setOnClickListener {
            val selectedActivityType = activitySpinner.selectedItem.toString()

            currentActivity = ActivityEntity(
                type = selectedActivityType,
                duration = 0L,    // Inizializza a 0, lo aggiornerai quando fermerai il cronometro
            )

            startChronometer()
            btnStart.isEnabled = false
            btnPauseResume.isEnabled = true
            btnStop.isEnabled = true
        }

        btnPauseResume.setOnClickListener {
            if (isRunning) {
                pauseChronometer()
                btnPauseResume.text = "Resume"
            } else {
                resumeChronometer()
                btnPauseResume.text = "Pause"
            }
        }

        btnStop.setOnClickListener {
            stopChronometer(currentActivity)
            btnPauseResume.text = "Pause"

            btnStart.isEnabled = true
            btnPauseResume.isEnabled = false
            btnStop.isEnabled = false
        }
    }

    private fun startChronometer() {
        if (!isRunning) {
            chronometer.base = SystemClock.elapsedRealtime()
            chronometer.start()
            isRunning = true

            Log.i("RecordFragment", "Cronometro avviato")
        }
    }

    private fun pauseChronometer() {
        if (isRunning) {
            chronometer.stop()
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.base
            isRunning = false


            Log.i("RecordFragment", "Cronometro fermato ad $pauseOffset")
        }
    }

    private fun resumeChronometer() {
        if (!isRunning) {
            chronometer.base = SystemClock.elapsedRealtime() - pauseOffset
            chronometer.start()
            isRunning = true

            Log.i("RecordFragment", "Cronometro ripreso da $pauseOffset")
        }
    }

    private fun stopChronometer(currentActivity: ActivityEntity?) {
        chronometer.stop()
        currentActivity?.duration = SystemClock.elapsedRealtime() - chronometer.base
        pauseOffset = 0
        isRunning = false
        chronometer.base = SystemClock.elapsedRealtime()

        Log.i("RecordFragment", "Cronometro stoppato")

        insertDataToDatabase(currentActivity)
    }

    private fun insertDataToDatabase(currentActivity: ActivityEntity?) {
        if (currentActivity != null) {
            myActivityViewModel.addActivity(currentActivity)
            Log.i("RecordFragment", "Insert ${currentActivity.toString()} into db")

        }
    }
}