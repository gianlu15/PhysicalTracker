// Walking.kt
package com.example.physicaltracker.physicalactivity

class Walking(
    duration: Long = 0L,
    distance: Float = 0f,
    var steps: Int = 0 // Numero di passi
) : BaseActivity("Walking", duration, distance) {

    override fun start() {
        super.start()
        // Logica specifica per avviare la camminata (es. inizio conteggio passi)
    }

    override fun stop() {
        super.stop()
        // Logica specifica per fermare la camminata (es. fine conteggio passi)
    }

    fun updateSteps(newSteps: Int) {
        steps += newSteps
    }

    override fun getDetails(): String {
        return "Activity: $name, Duration: $duration ms, Distance: $distance meters, Steps: $steps"
    }
}
