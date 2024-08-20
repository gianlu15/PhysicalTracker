package com.example.physicaltracker

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import androidx.fragment.app.Fragment

class RecordFragment : Fragment(R.layout.fragment_record) {

    private lateinit var chronometer: Chronometer   //inizializzato dopo
    private var isRunning = false
    private var pauseOffset: Long = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chronometer = view.findViewById(R.id.chronometer)


        val btnStart: Button = view.findViewById(R.id.btnStart)
        val btnPauseResume: Button = view.findViewById(R.id.btnPauseResume)
        val btnStop: Button = view.findViewById(R.id.btnStop)

        btnPauseResume.isEnabled = false
        btnStop.isEnabled = false

        btnStart.setOnClickListener {
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
            btnPauseResume.text = "Pause"
        }
    }

    private fun startChronometer(btnStart: Button, btnPauseResume: Button, btnStop: Button) {
        if (!isRunning) {
            chronometer.base = SystemClock.elapsedRealtime()    //tempo, in millisecondi dal momento in cui il dispositivo è stato avviato
            chronometer.start()
            isRunning = true

            btnStart.isEnabled = false
            btnPauseResume.isEnabled = true
            btnStop.isEnabled = true

            Log.i("Record Fragment", "Cronometro avviato")
        }
    }

    private fun pauseChronometer() {
        if (isRunning) {
            chronometer.stop()
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.base  //salvo il tempo trascorso
            isRunning = false

            Log.i("Record Fragment", "Cronometro messo in pausa ad $pauseOffset")

        }
    }

    private fun resumeChronometer() {
        if (!isRunning) {
            chronometer.base = SystemClock.elapsedRealtime() - pauseOffset  //riprendo dal tempo di prima
            chronometer.start()
            isRunning = true

            Log.i("Record Fragment", "Cronometro ripreso da ${chronometer.base}")

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

        Log.i("Record Fragment", "Cronometro stoppato")

    }
}
