package com.example.physicaltracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.physicaltracker.data.ActivityEntity
import com.example.physicaltracker.databinding.ItemActivityBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ListAdapter : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    private var activityList = emptyList<ActivityEntity>()

    inner class MyViewHolder(val binding: ItemActivityBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return activityList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = activityList[position]
        holder.binding.apply {
            tvType.text = currentItem.type
            tvStartTime.text = formatTime(currentItem.startTime)
            tvEndTime.text = currentItem.endTime?.let { formatTime(it) } ?: "In Progress"
            tvDuration.text = formatDuration(currentItem.duration)
        }
    }

    fun setData(activities: List<ActivityEntity>) {
        this.activityList = activities
        notifyDataSetChanged()
    }

    // Funzione per formattare la durata in ore, minuti e secondi
    private fun formatDuration(duration: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(duration)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    // Funzione per formattare il tempo in una data leggibile
    private fun formatTime(timeInMillis: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val date = Date(timeInMillis)
        return sdf.format(date)
    }
}
