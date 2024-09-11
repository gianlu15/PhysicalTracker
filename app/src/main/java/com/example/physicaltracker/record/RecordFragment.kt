package com.example.physicaltracker.record

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Chronometer
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.physicaltracker.R
import com.example.physicaltracker.data.entity.ActivityEntity
import com.example.physicaltracker.data.ActivityViewModel

class RecordFragment : Fragment(R.layout.fragment_record) {

    private lateinit var myActivityViewModel: ActivityViewModel
    private lateinit var chronometer: Chronometer
    private lateinit var tvStepsCounter: TextView

    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    private var steps: Int = 0
    private var currentSteps: Int = 0
    private var isWalking = false

    private var currentActivity: ActivityEntity? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myActivityViewModel = ViewModelProvider(requireActivity()).get(ActivityViewModel::class.java)

        chronometer = view.findViewById(R.id.chronometer)
        tvStepsCounter = view.findViewById(R.id.tvStepsCounter)

        // Get sensor for steps
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

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

        //When user change fragment while chronometer is running
        if (myActivityViewModel.isChronometerRunning) {
            chronometer.base = myActivityViewModel.chronometerBase
            chronometer.start()
            btnStart.isEnabled = false
            btnPauseResume.isEnabled = true
            btnStop.isEnabled = true
            activitySpinner.isEnabled = false
        } else if (myActivityViewModel.chronometerBase != 0L) {
            chronometer.base = myActivityViewModel.chronometerBase
            btnStart.isEnabled = false
            btnPauseResume.isEnabled = true
            btnStop.isEnabled = true
            activitySpinner.isEnabled = false
        }

        btnStart.setOnClickListener {
            val selectedActivityType = activitySpinner.selectedItem.toString()
            isWalking = (selectedActivityType == "Walking" || selectedActivityType == "Running")

            val startTime = System.currentTimeMillis()
            val date = startTime

            currentActivity = ActivityEntity(
                type = selectedActivityType,
                duration = 0L,
                startTime = startTime,
                date = date
            )

            startChronometer()

            saveChronometerState(true)

            activitySpinner.isEnabled = false

            //Get the steps only if activity are Walking or Running
            if (isWalking) {
                steps = 0
                currentSteps = 0
                sensorManager.registerListener(sensorEventListener, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL)
            }

            btnStart.isEnabled = false
            btnPauseResume.isEnabled = true
            btnStop.isEnabled = true
        }

        btnPauseResume.setOnClickListener {
            if (myActivityViewModel.isChronometerRunning) {
                pauseChronometer()
                saveChronometerState(false)
                btnPauseResume.text = "Resume"
            } else {
                resumeChronometer()
                saveChronometerState(true)
                btnPauseResume.text = "Pause"
            }
        }

        btnStop.setOnClickListener {
            if (isWalking) {
                sensorManager.unregisterListener(sensorEventListener)
                currentActivity?.steps = currentSteps
                currentSteps = 0
                steps = 0
            }

            stopChronometer(currentActivity)
            saveChronometerState(false)
            btnPauseResume.text = "Pause"

            btnStart.isEnabled = true
            btnPauseResume.isEnabled = false
            btnStop.isEnabled = false
            activitySpinner.isEnabled = true
        }
    }

    private fun startChronometer() {
        if (!myActivityViewModel.isChronometerRunning) {
            chronometer.base = SystemClock.elapsedRealtime()
            myActivityViewModel.chronometerBase = chronometer.base
            chronometer.start()
            myActivityViewModel.isChronometerRunning = true
        }
    }

    private fun pauseChronometer() {
        if (myActivityViewModel.isChronometerRunning) {
            chronometer.stop()
            myActivityViewModel.pauseOffset = SystemClock.elapsedRealtime() - chronometer.base
            myActivityViewModel.isChronometerRunning = false
        }
    }

    private fun resumeChronometer() {
        if (!myActivityViewModel.isChronometerRunning) {
            chronometer.base = SystemClock.elapsedRealtime() - myActivityViewModel.pauseOffset
            chronometer.start()
            myActivityViewModel.isChronometerRunning = true
        }
    }

    private fun stopChronometer(currentActivity: ActivityEntity?) {
        chronometer.stop()
        if (currentActivity != null) {
            currentActivity.duration = SystemClock.elapsedRealtime() - chronometer.base
            currentActivity.endTime = System.currentTimeMillis()

            //Save the new activity
            insertDataToDatabase(currentActivity)

            //Check if the previous activity was unknown
            myActivityViewModel.checkAndInsertUnknownActivities()
        }
        resetChronometer()
    }

    private fun resetChronometer() {
        myActivityViewModel.pauseOffset = 0L
        myActivityViewModel.isChronometerRunning = false
        myActivityViewModel.chronometerBase = 0L
        chronometer.base = SystemClock.elapsedRealtime()
        tvStepsCounter.text = "0 steps"
    }

    private fun insertDataToDatabase(currentActivity: ActivityEntity?) {
        if (currentActivity != null) {
            myActivityViewModel.addActivity(currentActivity)
            Log.i("RecordFragment", "Insert ${currentActivity.toString()} into db")

            Toast.makeText(requireContext(), "Activity added: ${currentActivity.type}", Toast.LENGTH_SHORT).show()
        }
    }

    private val sensorEventListener = object : SensorEventListener {
        @SuppressLint("SetTextI18n")
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                if (steps == 0) {
                    steps = event.values[0].toInt()
                }
                currentSteps = event.values[0].toInt() - steps
                tvStepsCounter.text = "$currentSteps steps"
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            // Doesn't need to handle
        }
    }

    private fun saveChronometerState(isRunning: Boolean) {
        val sharedPreferences = requireContext().getSharedPreferences("activity_tracker_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("is_chronometer_running", isRunning)
            apply()
        }
    }

}
