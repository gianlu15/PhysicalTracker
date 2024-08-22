package com.example.physicaltracker

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.physicaltracker.data.ActivityViewModel

class HistoryFragment : Fragment(R.layout.fragment_history) {

    private lateinit var myActivityViewModel: ActivityViewModel
    private lateinit var adapter: ListAdapter
    private var isSortedByDuration = false
    private var selectedActivityType: String = "All"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        adapter = ListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        myActivityViewModel = ViewModelProvider(this).get(ActivityViewModel::class.java)

        val spinnerActivityType: Spinner = view.findViewById(R.id.spinnerActivityType)
        spinnerActivityType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedActivityType = parent.getItemAtPosition(position) as String
                applyFilters()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Non fare nulla
            }
        }

        val btnToggleSort: Button = view.findViewById(R.id.btnToggleSort)
        btnToggleSort.setOnClickListener {
            isSortedByDuration = !isSortedByDuration
            btnToggleSort.text = if (isSortedByDuration) "Remove Duration Filter" else "Sort by Duration"
            applyFilters()
        }
    }

    private fun applyFilters() {
        if (selectedActivityType == "All") {
            if (isSortedByDuration) {
                myActivityViewModel.readAllDataSortedByDuration.observe(viewLifecycleOwner, Observer { activities ->
                    adapter.setData(activities)
                })
            } else {
                myActivityViewModel.readAllData.observe(viewLifecycleOwner, Observer { activities ->
                    adapter.setData(activities)
                })
            }
        } else {
            if (isSortedByDuration) {
                myActivityViewModel.getAllDataByTypeSortedByDuration(selectedActivityType).observe(viewLifecycleOwner, Observer { activities ->
                    adapter.setData(activities)
                })
            } else {
                myActivityViewModel.getAllDataByType(selectedActivityType).observe(viewLifecycleOwner, Observer { activities ->
                    adapter.setData(activities)
                })
            }
        }
    }
}

