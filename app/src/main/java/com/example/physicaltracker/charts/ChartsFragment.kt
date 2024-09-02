package com.example.physicaltracker.charts

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.physicaltracker.data.entity.ActivityEntity
import com.example.physicaltracker.data.ActivityViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.*
import androidx.core.content.ContextCompat
import com.example.physicaltracker.R
import com.github.mikephil.charting.formatter.PercentFormatter

class ChartsFragment : Fragment(R.layout.fragment_charts) {

    private lateinit var activityViewModel: ActivityViewModel
    private lateinit var pieChart: PieChart
    private lateinit var barChart: BarChart

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pieChart = view.findViewById(R.id.pieChart)
        barChart = view.findViewById(R.id.horizontalBarChart)

        activityViewModel = ViewModelProvider(requireActivity()).get(ActivityViewModel::class.java)

        // Osserva i dati e aggiorna i grafici
        activityViewModel.readAllData.observe(viewLifecycleOwner, Observer { activities ->
            setUpPieChart(activities)
            setUpBarChart(activities) // Corretto per usare setUpBarChart
        })
    }

    // Metodo per configurare il grafico a torta
    private fun setUpPieChart(activities: List<ActivityEntity>) {
        val entries = mutableListOf<PieEntry>()
        val activityMap = mutableMapOf<String, Int>()

        // Conta le occorrenze di ciascun tipo di attività
        activities.forEach { activity ->
            activityMap[activity.type] = activityMap.getOrDefault(activity.type, 0) + 1
        }

        // Popola le voci del grafico a torta
        activityMap.forEach { (type, count) ->
            entries.add(PieEntry(count.toFloat(), type))
        }

        val dataSet = PieDataSet(entries, "Activity Distribution")
        dataSet.colors = listOf(
            // Usa ContextCompat per ottenere i colori
            ContextCompat.getColor(requireContext(), R.color.firstActivityPie),
            ContextCompat.getColor(requireContext(), R.color.secondActivityPie),
            ContextCompat.getColor(requireContext(), R.color.thirdActivityPie),
            ContextCompat.getColor(requireContext(), R.color.fourthActivityPie)
        )

        dataSet.setDrawValues(true)
        dataSet.valueTextSize = 18f
        dataSet.valueFormatter = PercentFormatter(pieChart) // Per mostrare percentuali
        pieChart.setUsePercentValues(true) // Imposta l'uso dei valori percentuali



        val data = PieData(dataSet)
        pieChart.data = data

        pieChart.description.isEnabled = false

        pieChart.legend.isEnabled = false

        pieChart.setDrawCenterText(true)
        pieChart.centerText = "Activity"
        pieChart.setCenterTextSize(28f)
        pieChart.setEntryLabelTextSize(16f)



        pieChart.invalidate() // Refresh del grafico
    }

    // Metodo per configurare il grafico a barre
    private fun setUpBarChart(activities: List<ActivityEntity>) {
        val entries = mutableListOf<BarEntry>() // Usa BarEntry per BarChart

        // Esempio di riempimento delle voci del grafico a barre per il conteggio dei passi
        activities.filter { it.type == "Walking" }.forEachIndexed { index, activity ->
            entries.add(BarEntry(index.toFloat(), activity.steps?.toFloat() ?: 0f))
        }

        val dataSet = BarDataSet(entries, "Daily Steps") // Usa BarDataSet per BarChart
        dataSet.color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        dataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)

        val barData = BarData(dataSet) // Usa BarData per BarChart
        barChart.data = barData
        barChart.invalidate() // Refresh del grafico
    }
}
