// Sitting.kt
package com.example.physicaltracker.physicalactivity

class Sitting(
    duration: Long = 0L,
    distance: Float = 0f
) : BaseActivity("Sitting", duration, distance) {

    override fun start() {
        super.start()
        // Logica specifica per avviare l'attività "Stare Seduti"
    }

    override fun stop() {
        super.stop()
        // Logica specifica per fermare l'attività "Stare Seduti"
    }
}
