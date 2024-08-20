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
import com.example.physicaltracker.physicalactivity.BaseActivity
import com.example.physicaltracker.physicalactivity.Driving
import com.example.physicaltracker.physicalactivity.Sitting
import com.example.physicaltracker.physicalactivity.Walking

class RecordFragment : Fragment(R.layout.fragment_record) {

    private lateinit var chronometer: Chronometer
    private var isRunning = false
    private var pauseOffset: Long = 0

    private var currentActivity: BaseActivity? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

            currentActivity = when (selectedActivityType) {
                "Walking" -> Walking()
                "Sitting" -> Sitting()
                "Driving" -> Driving()
                else -> null
            }

            currentActivity?.start()
            startChronometer(btnStart, btnPauseResume, btnStop)
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
            stopChronometer(btnStart, btnPauseResume, btnStop)
            currentActivity?.stop()
            Log.i("RecordFragment", currentActivity?.getDetails() ?: "No activity recorded")
            btnPauseResume.text = "Pause"
        }
    }

    private fun startChronometer(btnStart: Button, btnPauseResume: Button, btnStop: Button) {
        if (!isRunning) {
            chronometer.base = SystemClock.elapsedRealtime()
            chronometer.start()
            isRunning = true

            btnStart.isEnabled = false
            btnPauseResume.isEnabled = true
            btnStop.isEnabled = true
        }
    }

    private fun pauseChronometer() {
        if (isRunning) {
            chronometer.stop()
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.base
            isRunning = false
        }
    }

    private fun resumeChronometer() {
        if (!isRunning) {
            chronometer.base = SystemClock.elapsedRealtime() - pauseOffset
            chronometer.start()
            isRunning = true
        }
    }

    private fun stopChronometer(btnStart: Button, btnPauseResume: Button, btnStop: Button) {
        chronometer.stop()
        pauseOffset = 0
        isRunning = false
        chronometer.base = SystemClock.elapsedRealtime()

        btnStart.isEnabled = true
        btnPauseResume.isEnabled = false
        btnStop.isEnabled = false
    }
}
