package com.example.physicaltracker.geofence

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.physicaltracker.R
import com.example.physicaltracker.data.GeofenceViewModel
import com.example.physicaltracker.data.entity.GeofenceEntity
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class GeofenceFragment : Fragment(R.layout.fragment_geofence), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var geofencePendingIntent: PendingIntent
    private lateinit var geofenceViewModel: GeofenceViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var selectedLatLng: LatLng? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        geofenceViewModel = ViewModelProvider(this).get(GeofenceViewModel::class.java)
        geofencingClient = LocationServices.getGeofencingClient(requireActivity())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Inizializza la mappa
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Inizializza i pulsanti
        val btnAddGeofence: Button = view.findViewById(R.id.btnAddGeofence)
        val btnRemoveGeofences: Button = view.findViewById(R.id.btnRemoveGeofence)

        btnAddGeofence.setOnClickListener {
            if (selectedLatLng != null) {
                addGeofence(selectedLatLng!!)
            } else {
                Toast.makeText(requireContext(), "Please select a location on the map", Toast.LENGTH_SHORT).show()
            }
        }

        btnRemoveGeofences.setOnClickListener {
            removeGeofences()
        }

        // Prepara l'intento per il GeofenceBroadcastReceiver
        geofencePendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            Intent(requireContext(), GeofenceBroadcastReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true

            // Richiedi la posizione corrente
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }

            // Osserva i geofence dal database e disegna sulla mappa
            geofenceViewModel.allGeofences.observe(viewLifecycleOwner) { geofences ->
                // Cancella la mappa prima di ridisegnare
                map.clear()
                geofences.forEach { geofence ->
                    val latLng = LatLng(geofence.latitude, geofence.longitude)
                    map.addMarker(MarkerOptions().position(latLng).title("Geofence Location"))
                    map.addCircle(
                        CircleOptions()
                            .center(latLng)
                            .radius(geofence.radius.toDouble())
                            .strokeColor(0xFFFF0000.toInt())
                            .fillColor(0x22FF0000)
                            .strokeWidth(2f)
                    )
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                1001
            )
        }

        map.setOnMapClickListener { latLng ->
            selectedLatLng = latLng
            map.clear()
            map.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
            map.addCircle(
                CircleOptions()
                    .center(latLng)
                    .radius(100.0)
                    .strokeColor(0xFFFF0000.toInt())
                    .fillColor(0x22FF0000)
                    .strokeWidth(2f)
            )
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }
    }


    private fun addGeofence(latLng: LatLng) {
        val geofenceId = "GEOFENCE_ID_${System.currentTimeMillis()}"
        val geofence = Geofence.Builder()
            .setRequestId(geofenceId)
            .setCircularRegion(latLng.latitude, latLng.longitude, 100f)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Richiedi i permessi se non sono già stati concessi
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
            return
        }

        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
            addOnSuccessListener {
                val geofenceEntity = GeofenceEntity(
                    geofenceId = geofenceId,
                    latitude = latLng.latitude,
                    longitude = latLng.longitude,
                    radius = 100f,
                    transitionType = Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT
                )
                geofenceViewModel.insert(geofenceEntity)
                Toast.makeText(requireContext(), "Geofence added successfully", Toast.LENGTH_SHORT).show()
            }
            addOnFailureListener { e ->
                val errorMessage = when (e) {
                    is SecurityException -> "Permission denied: ${e.message}"
                    else -> "Unknown error: ${e.message}"
                }
                Toast.makeText(requireContext(), "Failed to add geofence: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun removeGeofences() {
        geofencingClient.removeGeofences(geofencePendingIntent).run {
            addOnSuccessListener {
                // Rimuovi tutti i geofence dal database in una Coroutine
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    val geofencesToDelete = geofenceViewModel.allGeofences.value ?: emptyList()
                    geofencesToDelete.forEach { geofence ->
                        geofenceViewModel.delete(geofence)
                    }
                }
                Toast.makeText(requireContext(), "Geofences removed", Toast.LENGTH_SHORT).show()
            }
            addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to remove geofences", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
