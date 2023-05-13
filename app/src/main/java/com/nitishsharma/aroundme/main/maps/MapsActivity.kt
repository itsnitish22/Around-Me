package com.nitishsharma.aroundme.main.maps

import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nitishsharma.aroundme.R
import com.nitishsharma.aroundme.databinding.ActivityMapsBinding
import com.nitishsharma.aroundme.main.maps.bottomsheet.DetailedBottomSheet
import com.nitishsharma.aroundme.utils.BitmapHelper
import com.nitishsharma.aroundme.utils.SearchBarHelper
import com.nitishsharma.aroundme.utils.Utility
import com.nitishsharma.aroundme.utils.Utility.isLocationPermissionGiven
import com.nitishsharma.aroundme.utils.Utility.toast

open class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var markersOnMap = ArrayList<Pair<Marker, String?>>()
    private var searchText = ""
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var geoCoder: Geocoder
    private var currentLatLng: LatLng? = null
    private val mapsActivityVM: MapsActivityVM by viewModels()
    private val placesToGo = SearchBarHelper.PLACES_TO_GO
    private lateinit var dialog: BottomSheetDialog
    private val dropIcon: BitmapDescriptor by lazy {
        val color = ContextCompat.getColor(this, R.color.light_red)
        BitmapHelper.vectorToBitmap(this, R.drawable.ic_drop, color)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialog = BottomSheetDialog(this)

        initGoogleMap()
        initComposeView()
        initClickListeners()
        initSearchBar()
        initObservers()
    }

    private fun initComposeView() {
        binding.composeView.setContent {
            SetupPlacesLazyRow(placesToGo = placesToGo) { placeToSearch ->
                currentLatLng?.let { currentLocation ->
                    binding.progressBar.visibility = View.VISIBLE
                    mapsActivityVM.fetchNearbyPlaces(
                        "${currentLocation.latitude},${currentLocation.longitude}",
                        placeToSearch
                    )
                }
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
                addNewMarkers(it.second.second, it.first)
            }
            binding.progressBar.visibility = View.GONE
        })
        mapsActivityVM.searchedLocation.observe(this, Observer { location ->
            removePreviousMarkers()
            location?.let {
                addNewMarkers(it)
                zoomToMarker(it)
            }
            binding.progressBar.visibility = View.GONE
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
                currentLatLng = LatLng(location.latitude, location.longitude)
                removePreviousMarkers()
                mMap.isMyLocationEnabled = true
                mMap.uiSettings.isMyLocationButtonEnabled = false
                currentLatLng?.let {
                    zoomToMarker(it)
                }
            } else {
                toast("Location not available")
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setCurrentLocationPointer()
        initMarkerClickListener()
    }

    private fun initMarkerClickListener() {
        mMap.setOnMarkerClickListener { marker ->
            marker.tag?.let {
                showBottomSheetFragment(placeId = it, markerLocation = marker.position)
            }
            true
        }
    }

    private fun removePreviousMarkers() {
        if (markersOnMap.isNotEmpty()) {
            markersOnMap.forEach {
                it.first.remove()
            }
            markersOnMap.removeAll(markersOnMap)
        }
    }

    private fun addNewMarkers(location: LatLng, placeId: String? = null) {
        val marker = if (placeId.isNullOrEmpty()) mMap.addMarker(
            MarkerOptions().position(location)
                .title("title")
        ) else mMap.addMarker(
            MarkerOptions().position(location).icon(dropIcon)
                .title("title")
        )
        marker?.tag = placeId
        marker?.let {
            markersOnMap.add(Pair(it, placeId))
        }
    }

    private fun zoomToMarker(location: LatLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
    }

    private fun initSearchBar() {
        binding.searchPlace.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (p0 != null) {
                    searchText = p0.replace(" ", "_")
                    binding.progressBar.visibility = View.VISIBLE
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
        mapsActivityVM.searchPlace(toSearch, geoCoder)
    }

    private fun showBottomSheetFragment(placeId: Any, markerLocation: LatLng) {
        val placeIdString = placeId as String
        currentLatLng?.let {
            DetailedBottomSheet.newInstance(placeIdString, it, markerLocation)
                .show(supportFragmentManager, "DETAILED_BOTTOM_SHEET")
        }
    }
}

