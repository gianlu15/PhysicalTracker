package com.example.physicaltracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // --------------    NAVBAR
        val chartsFragment = ChartsFragment()
        val recordFragment = RecordFragment()
        val historyFragment = HistoryFragment()

        setCurrentFragment(chartsFragment)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Imposta il listener per la selezione degli elementi
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.myStatistics -> setCurrentFragment(chartsFragment)
                R.id.myNewActivity -> setCurrentFragment(recordFragment)
                R.id.myHistory -> setCurrentFragment(historyFragment)
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFrame, fragment)
            commit()
        }
}
