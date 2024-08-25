package com.example.physicaltracker.calendar

import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.physicaltracker.R
import com.example.physicaltracker.data.ActivityViewModel
import com.example.physicaltracker.history.ListAdapter // Importa il tuo ListAdapter
import java.util.Calendar

class CalendarFragment : Fragment(R.layout.fragment_calendar) {

    private lateinit var calendarView: CalendarView
    private lateinit var recyclerViewActivities: RecyclerView
    private lateinit var activityViewModel: ActivityViewModel
    private lateinit var adapter: ListAdapter // Usa il tuo ListAdapter esistente

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarView = view.findViewById(R.id.calendarView)
        recyclerViewActivities = view.findViewById(R.id.rvCalendar)

        // Configura il RecyclerView con ListAdapter
        adapter = ListAdapter() // Inizializza il ListAdapter
        recyclerViewActivities.adapter = adapter
        recyclerViewActivities.layoutManager = LinearLayoutManager(requireContext())

        // Ottieni l'istanza del ViewModel
        activityViewModel = ViewModelProvider(this).get(ActivityViewModel::class.java)

        // Listener per la selezione della data nel calendario
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = getDateInMillis(year, month, dayOfMonth)
            fetchActivitiesForDate(selectedDate)
        }
    }

    // Funzione per convertire una data in millisecondi
    private fun getDateInMillis(year: Int, month: Int, dayOfMonth: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    // Funzione per recuperare le attività per una data specifica
    private fun fetchActivitiesForDate(dateInMillis: Long) {
        val startOfDay = getStartOfDayInMillis(dateInMillis)
        val endOfDay = getEndOfDayInMillis(dateInMillis)

        activityViewModel.getActivitiesByDate(startOfDay, endOfDay).observe(viewLifecycleOwner, Observer { activities ->
            activities?.let {
                adapter.setData(it) // Utilizza il metodo setData del ListAdapter
            }
        })
    }

    // Funzioni helper per ottenere l'inizio e la fine del giorno in millisecondi
    private fun getStartOfDayInMillis(dateInMillis: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getEndOfDayInMillis(dateInMillis: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
}