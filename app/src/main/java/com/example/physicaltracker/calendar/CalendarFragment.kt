package com.example.physicaltracker.calendar

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.physicaltracker.R
import com.example.physicaltracker.data.ActivityViewModel
import java.util.Calendar

class CalendarFragment : Fragment(R.layout.fragment_calendar) {

    private lateinit var calendarView: CalendarView
    private lateinit var recyclerViewActivities: RecyclerView
    private lateinit var activityViewModel: ActivityViewModel
    private lateinit var adapter: CalendarAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarView = view.findViewById(R.id.calendarView)
        recyclerViewActivities = view.findViewById(R.id.rvCalendar)

        adapter = CalendarAdapter()
        recyclerViewActivities.adapter = adapter
        recyclerViewActivities.layoutManager = LinearLayoutManager(requireContext())

        activityViewModel = ViewModelProvider(this).get(ActivityViewModel::class.java)

        val today = Calendar.getInstance()
        val todayInMillis = getDateInMillis(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH))
        fetchActivitiesForDate(todayInMillis)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = getDateInMillis(year, month, dayOfMonth)
            fetchActivitiesForDate(selectedDate)
        }
    }

    private fun getDateInMillis(year: Int, month: Int, dayOfMonth: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth, 0, 0, 0) // Imposta l'ora a mezzanotte
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun fetchActivitiesForDate(dateInMillis: Long) {
        val startOfDay = getStartOfDayInMillis(dateInMillis)
        val endOfDay = getEndOfDayInMillis(dateInMillis)

        Log.i("CalendarFragment", "Start of Day: $startOfDay, End of Day: $endOfDay")

        activityViewModel.getActivitiesByDate(startOfDay, endOfDay).observe(viewLifecycleOwner, Observer { activities ->
            activities?.let {
                Log.i("CalendarFragment", "Number of activities fetched: ${activities.size}")
                adapter.setData(it)
            }
        })
    }

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
