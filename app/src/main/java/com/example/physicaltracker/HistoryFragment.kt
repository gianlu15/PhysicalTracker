package com.example.physicaltracker

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.physicaltracker.data.ActivityViewModel

class HistoryFragment : Fragment(R.layout.fragment_history) {

    private lateinit var myActivityViewModel: ActivityViewModel
    private lateinit var adapter: ListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inizializza il RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        adapter = ListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Configura il ViewModel
        myActivityViewModel = ViewModelProvider(this).get(ActivityViewModel::class.java)

        // Osserva i dati e aggiorna l'adapter
        myActivityViewModel.readAllData.observe(viewLifecycleOwner, Observer { activities ->
            adapter.setData(activities)
        })
    }
}
