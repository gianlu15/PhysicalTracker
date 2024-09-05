package com.example.physicaltracker.charts

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.*
import androidx.viewpager2.widget.ViewPager2
import com.example.physicaltracker.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.Calendar

class ChartsFragment : Fragment(R.layout.fragment_charts) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager: ViewPager2 = view.findViewById(R.id.viewPager)
        val tabLayout: TabLayout = view.findViewById(R.id.tabLayout)

        val adapter = ChartsPagerAdapter(requireActivity())
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Pie Chart"
                1 -> "Bar Chart"
                2 -> "Line Chart"
                else -> null
            }
        }.attach()
    }
}

