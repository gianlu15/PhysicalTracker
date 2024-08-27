package com.example.physicaltracker.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.physicaltracker.data.ActivityEntity
import com.example.physicaltracker.databinding.ItemActivityCalendarBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class CalendarAdapter : RecyclerView.Adapter<CalendarAdapter.MyViewHolder>() {

    private var activityList = emptyList<ActivityEntity>()

    inner class MyViewHolder(val binding: ItemActivityCalendarBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemActivityCalendarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return activityList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = activityList[position]
        holder.binding.apply {
            tvStartTime.text = formatTime(currentItem.startTime)
            tvEndTime.text = formatTime(currentItem.endTime!!)
            tvType.text = currentItem.type
            tvDuration.text = formatDuration(currentItem.duration)
        }
    }

    fun setData(activities: List<ActivityEntity>) {
        this.activityList = activities
        notifyDataSetChanged()
    }

    // Formatta la durata in ore, minuti e secondi
    private fun formatDuration(duration: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(duration)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    // Formatta il tempo in una stringa leggibile
    private fun formatTime(timeInMillis: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault()) // Mostra solo l'ora e i minuti
        val date = Date(timeInMillis)
        return sdf.format(date)
    }
}
