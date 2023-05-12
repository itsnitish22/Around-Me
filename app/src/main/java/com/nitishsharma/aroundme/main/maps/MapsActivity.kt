package com.nitishsharma.aroundme.main.maps

import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.nitishsharma.aroundme.R
import com.nitishsharma.aroundme.databinding.ActivityMapsBinding
import com.nitishsharma.aroundme.utils.Constants
import com.nitishsharma.aroundme.utils.SearchBarHelper
import com.nitishsharma.aroundme.utils.Utility
import com.nitishsharma.aroundme.utils.Utility.isLocationPermissionGiven
import com.nitishsharma.aroundme.utils.Utility.toast

open class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var markersOnMap = ArrayList<Marker>()
    private var searchText = ""
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var geoCoder: Geocoder
    private val mapsActivityVM: MapsActivityVM by viewModels()
    private val placesToGo = SearchBarHelper.PLACES_TO_GO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initGoogleMap()
        initComposeView()
        initClickListeners()
        initSearchBar()
        initObservers()
    }

    private fun initComposeView() {
        binding.composeView.setContent {
            SetupLazyColumn()
        }
    }

    @Composable
    fun SetupLazyColumn() {
        LazyRow() {
            items(items = placesToGo) {
                PlaceItem(it.second, it.first)
            }
        }
    }

    @Composable
    fun PlaceItem(placeName: String, placeImg: Int) {
        Box(
            modifier = Modifier
                .padding(5.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable(onClick = {
                    /* Handle click event */
                })
        ) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = placeImg),
                    contentDescription = "",
                    tint = Color.Black,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = placeName,
                    fontSize = 15.sp,
                    color = Color.Black
                )
            }
        }
    }

    private fun initGoogleMap() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLocation()
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapsFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        geoCoder = Geocoder(this)
    }

    private fun initObservers() {
        mapsActivityVM.placesOnMap.observe(this, Observer { pointsOnMap ->
            removePreviousMarkers()
            pointsOnMap.forEach {
                addNewMarkers(it.value, it.key)
            }
        })
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
            Utility.askLocationPermission(this@MapsActivity)
        } else {
            showCurrentLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun showCurrentLocation() {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                removePreviousMarkers()
                mMap.isMyLocationEnabled = true
                mMap.uiSettings.isMyLocationButtonEnabled = false
                zoomToMarker(currentLatLng, "Current Location")
            } else {
                toast("Location not available")
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val chandigarh = LatLng(Constants.CHANDIGARH_LAT, Constants.CHANDIGARH_LONG)
        removePreviousMarkers()
        addNewMarkers(chandigarh, "Chandigarh Sector-17")
        zoomToMarker(chandigarh, "Chandigarh Sector-17")
    }

    private fun removePreviousMarkers() {
        if (markersOnMap.isNotEmpty()) {
            markersOnMap.forEach {
                it.remove()
            }
            markersOnMap.removeAll(markersOnMap)
        }
    }

    private fun addNewMarkers(location: LatLng, title: String) {
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

    private fun initSearchBar() {
        binding.searchPlace.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (p0 != null) {
                    searchText = p0.replace(" ", "_")
                    Log.i("MainActivity", searchText)
                    searchLocation(searchText)
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }
        })
    }

    private fun searchLocation(toSearch: String) {
        val addresses = geoCoder.getFromLocationName(toSearch, 1)
        addresses?.let {
            val location = LatLng(addresses[0].latitude, addresses[0].longitude)
            removePreviousMarkers()
            addNewMarkers(location, toSearch)
            zoomToMarker(location, toSearch)
        }
    }
}

