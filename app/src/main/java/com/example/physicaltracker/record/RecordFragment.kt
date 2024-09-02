package com.example.physicaltracker.record

import android.content.Context
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
    private var isWalking = false

    private var currentActivity: ActivityEntity? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myActivityViewModel = ViewModelProvider(requireActivity()).get(ActivityViewModel::class.java)

        chronometer = view.findViewById(R.id.chronometer)
        tvStepsCounter = view.findViewById(R.id.tvStepsCounter)

        // Inizializza il sensore di passi
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
            isWalking = selectedActivityType == "Walking"

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

            // Avvia il contapassi solo se l'attività è "Walking"
            if (isWalking) {
                steps = 0 // Resetta il contatore dei passi
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
            // Ferma il contapassi
            if (isWalking) {
                sensorManager.unregisterListener(sensorEventListener)
                currentActivity?.steps = steps
            }

            stopChronometer(currentActivity)
            saveChronometerState(false)
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
            myActivityViewModel.checkAndInsertUnknownActivities()

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

    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                if (steps == 0) {
                    steps = event.values[0].toInt()
                }
                val currentSteps = event.values[0].toInt() - steps
                tvStepsCounter.text = "$currentSteps steps"
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            // Non è necessario gestire questo caso per il contapassi
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
