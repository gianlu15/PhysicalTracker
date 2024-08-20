// Driving.kt
package com.example.physicaltracker.physicalactivity

class Driving(
    duration: Long = 0L,
    distance: Float = 0f
) : BaseActivity("Driving", duration, distance) {

    override fun start() {
        super.start()
        // Logica specifica per avviare l'attività "Guidare"
    }

    override fun stop() {
        super.stop()
        // Logica specifica per fermare l'attività "Guidare"
    }
}
