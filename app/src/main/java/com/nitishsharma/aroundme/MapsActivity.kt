package com.nitishsharma.aroundme

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.Debug.getLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.nitishsharma.aroundme.databinding.ActivityMapsBinding
import com.nitishsharma.aroundme.utils.Constants
import com.nitishsharma.aroundme.utils.Utility
import com.nitishsharma.aroundme.utils.Utility.isLocationPermissionGiven
import com.nitishsharma.aroundme.utils.Utility.toast

open class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var markersOnMap = ArrayList<Marker>()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLocation()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        initClickListeners()
    }

    private fun initClickListeners() {
        binding.apply {
            locateMe.setOnClickListener {
                setCurrentLocationPointer()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setCurrentLocationPointer() {
        if (isLocationPermissionGiven()) {
            Utility.askLocationPermission(this)
        } else {
            showCurrentLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun showCurrentLocation() {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                removePrevMarkersAddNewMarkers(currentLatLng, "Current Location")
                zoomToMarker(currentLatLng, "Current Location")
            } else {
                toast("Location not available")
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val chandigarh = LatLng(Constants.CHANDIGARH_LAT, Constants.CHANDIGARH_LONG)
        removePrevMarkersAddNewMarkers(chandigarh, "Chandigarh Sector-17")
        zoomToMarker(chandigarh, "Chandigarh Sector-17")
    }

    private fun removePrevMarkersAddNewMarkers(location: LatLng, title: String) {
        if (markersOnMap.isNotEmpty()) {
            markersOnMap.forEach {
                markersOnMap.remove(it)
                it.remove()
            }
        }
        val marker = mMap.addMarker(
            MarkerOptions().position(location)
                .title(title)
        )
        marker?.let {
            markersOnMap.add(it)
        }
    }

    private fun zoomToMarker(location: LatLng, title: String) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
    }

}

