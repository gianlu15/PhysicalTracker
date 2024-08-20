// Activity.kt
package com.example.physicaltracker.physicalactivity

open class BaseActivity(
    val name: String,
    var duration: Long = 0L, // Durata in millisecondi
    var distance: Float = 0f // Distanza in metri
) {
    // Metodo per avviare l'attività (es. avvio del cronometro)
    open fun start() {
    }

    // Metodo per fermare l'attività
    open fun stop() {
    }

    // Metodo per aggiornare la distanza percorsa
    open fun updateDistance(newDistance: Float) {
        distance += newDistance
    }

    // Metodo per ottenere i dettagli dell'attività
    open fun getDetails(): String {
        return "Activity: $name, Duration: $duration ms, Distance: $distance meters"
    }
}
