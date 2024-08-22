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

    private var currentActivity: ActivityEntity? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myActivityViewModel = ViewModelProvider(requireActivity()).get(ActivityViewModel::class.java)

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

        // Ripristina lo stato del cronometro e dei pulsanti
        if (myActivityViewModel.isChronometerRunning) {
            chronometer.base = myActivityViewModel.chronometerBase
            chronometer.start()
            btnStart.isEnabled = false
            btnPauseResume.isEnabled = true
            btnStop.isEnabled = true
        } else if (myActivityViewModel.chronometerBase != 0L) {
            chronometer.base = myActivityViewModel.chronometerBase
            btnStart.isEnabled = false
            btnPauseResume.isEnabled = true
            btnStop.isEnabled = true
        }

        btnStart.setOnClickListener {
            val selectedActivityType = activitySpinner.selectedItem.toString()

            val startTime = System.currentTimeMillis()
            val date = startTime

            currentActivity = ActivityEntity(
                type = selectedActivityType,
                duration = 0L,
                startTime = startTime,
                date = date
            )

            startChronometer()
            btnStart.isEnabled = false
            btnPauseResume.isEnabled = true
            btnStop.isEnabled = true
        }

        btnPauseResume.setOnClickListener {
            if (myActivityViewModel.isChronometerRunning) {
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
        if (!myActivityViewModel.isChronometerRunning) {
            chronometer.base = SystemClock.elapsedRealtime()
            myActivityViewModel.chronometerBase = chronometer.base
            chronometer.start()
            myActivityViewModel.isChronometerRunning = true

            Log.i("RecordFragment", "Cronometro avviato")
        }
    }

    private fun pauseChronometer() {
        if (myActivityViewModel.isChronometerRunning) {
            chronometer.stop()
            myActivityViewModel.pauseOffset = SystemClock.elapsedRealtime() - chronometer.base
            myActivityViewModel.isChronometerRunning = false

            Log.i("RecordFragment", "Cronometro fermato a ${myActivityViewModel.pauseOffset}")
        }
    }

    private fun resumeChronometer() {
        if (!myActivityViewModel.isChronometerRunning) {
            chronometer.base = SystemClock.elapsedRealtime() - myActivityViewModel.pauseOffset
            chronometer.start()
            myActivityViewModel.isChronometerRunning = true

            Log.i("RecordFragment", "Cronometro ripreso da ${myActivityViewModel.pauseOffset}")
        }
    }

    private fun stopChronometer(currentActivity: ActivityEntity?) {
        chronometer.stop()
        if (currentActivity != null) {
            currentActivity.duration = SystemClock.elapsedRealtime() - chronometer.base
            currentActivity.endTime = System.currentTimeMillis()
            insertDataToDatabase(currentActivity)
        }
        resetChronometer()
    }

    private fun resetChronometer() {
        myActivityViewModel.pauseOffset = 0L
        myActivityViewModel.isChronometerRunning = false
        myActivityViewModel.chronometerBase = 0L
        chronometer.base = SystemClock.elapsedRealtime()
    }

    private fun insertDataToDatabase(currentActivity: ActivityEntity?) {
        if (currentActivity != null) {
            myActivityViewModel.addActivity(currentActivity)
            Log.i("RecordFragment", "Insert ${currentActivity.toString()} into db")
        }
    }
}
