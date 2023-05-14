package com.nitishsharma.aroundme.main.maps

import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
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
import com.nitishsharma.aroundme.utils.Constants
import com.nitishsharma.aroundme.utils.SearchBarHelper
import com.nitishsharma.aroundme.utils.Utility
import com.nitishsharma.aroundme.utils.Utility.isLocationPermissionNotGiven
import com.nitishsharma.aroundme.utils.Utility.toast

open class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap //google map
    private lateinit var binding: ActivityMapsBinding //binding
    private var markersOnMap = ArrayList<Pair<Marker, String?>>() //storing marked markers on map
    private var searchText = "" //search text default
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient //location provider [fused]
    private lateinit var mapFragment: SupportMapFragment //map frag
    private lateinit var geoCoder: Geocoder //geocode
    private var currentLatLng: LatLng? = null //user's current location
    private val mapsActivityVM: MapsActivityVM by viewModels() //viewmodel
    private val placesToGo = SearchBarHelper.PLACES_TO_GO //top scroll nearby places [CATEGORY WISE]
    private lateinit var dialog: BottomSheetDialog //bottom sheet frag
    private val dropIcon: BitmapDescriptor by lazy { //drop point icon if searched nearby places
        val color = ContextCompat.getColor(this, R.color.light_red)
        BitmapHelper.vectorToBitmap(this, R.drawable.ic_drop, color)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialog = BottomSheetDialog(this)

        //initializing things
        initGoogleMap()
        initComposeView()
        initClickListeners()
        initSearchBar()
        initObservers()
    }

    //compose view for showing top slider to search nearby places [category wise]
    private fun initComposeView() {
        binding.composeView.setContent {
            SetupPlacesLazyRow(placesToGo = placesToGo) { placeToSearch -> //callback on clicked category
                currentLatLng?.let { currentLocation ->
                    binding.progressBar.visibility = View.VISIBLE
                    mapsActivityVM.fetchNearbyPlaces( //fetch nearby places
                        "${currentLocation.latitude},${currentLocation.longitude}",
                        placeToSearch
                    )
                }
            }
        }
    }

    //google maps init stuff
    private fun initGoogleMap() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLocation()
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapsFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        geoCoder = Geocoder(this)
    }

    //init observers
    private fun initObservers() {
        mapsActivityVM.placesOnMap.observe(
            this,
            Observer { pointsOnMap -> //getting places on map according to the searched category
                removePreviousMarkers() //removing previous markers if any
                pointsOnMap.forEach {//iterating on pointsOnMap and pin them on map
                    addNewMarkers(it.second.second, it.first)
                }
                binding.progressBar.visibility = View.GONE
            })
        mapsActivityVM.searchedLocation.observe(
            this,
            Observer { location -> //observing the searched location
                removePreviousMarkers() //removing previous markers
                location?.let {
                    addNewMarkers(it) //adding new marker
                    zoomToMarker(it) //as it's only one point on map, zoom to it
                }
                binding.progressBar.visibility = View.GONE
            })
    }

    //init click listeners
    private fun initClickListeners() {
        binding.apply {
            locateMe.setOnClickListener { //click on locate me button
                setCurrentLocationPointer() //set the current location pointer
            }
        }
    }

    //setting current location pointer based on a few checks
    @SuppressLint("MissingPermission")
    private fun setCurrentLocationPointer() {
        if (isLocationPermissionNotGiven())  //check if permission of location is not given
            Utility.askLocationPermission(this@MapsActivity) //if true, ask for location permission
        else
            showCurrentLocation() //else, show current location of user on map
    }

    //show current location
    @SuppressLint("MissingPermission")
    private fun showCurrentLocation() {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location -> //getting location
            if (location != null) {
                currentLatLng = LatLng(location.latitude, location.longitude)
                removePreviousMarkers() //removing previous markers
                mMap.isMyLocationEnabled = true //blue pointer on current location
                mMap.uiSettings.isMyLocationButtonEnabled = false //relocate button disable
                currentLatLng?.let {
                    zoomToMarker(it) //zoom to the current location
                }
            } else {
                toast("Location not available")
            }
        }.addOnFailureListener {
            Log.e(Constants.ActivityNameForLogging.MAP_ACTIVITY, it.toString())
            toast("Some error occoured")
        }
    }

    //on map ready overriden fxn
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setCurrentLocationPointer() //set current location
        initMarkerClickListener() //init maker click listeners
    }

    private fun initMarkerClickListener() {
        mMap.setOnMarkerClickListener { marker -> //on click of marker
            marker.tag?.let {
                showBottomSheetFragment(
                    placeId = it,
                    markerLocation = marker.position
                ) //show bottom sheet fragment, pass the placeId, markerLocation
            }
            true
        }
    }

    //remove previous markers function
    private fun removePreviousMarkers() {
        if (markersOnMap.isNotEmpty()) {
            markersOnMap.forEach {
                it.first.remove() //remove pointers on map
            }
            markersOnMap.removeAll(markersOnMap) //empty the array list
        }
    }

    //add new markers
    private fun addNewMarkers(location: LatLng, placeId: String? = null) {
        //if the marker is getting pointed because the user searched a location, show the default pointer
        val marker = if (placeId.isNullOrEmpty()) mMap.addMarker(
            MarkerOptions().position(location)
                .title("title")
        ) else mMap.addMarker( //if the marker is getting pointed because the user searched nearby places, show a custom red pointer
            MarkerOptions().position(location).icon(dropIcon)
                .title("title")
        )
        marker?.tag = placeId //adding tag to that marker
        marker?.let {
            markersOnMap.add(Pair(it, placeId)) //adding the marker in array list
        }
    }

    //zoom to marker
    private fun zoomToMarker(location: LatLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
    }

    //search bar
    private fun initSearchBar() {
        binding.searchPlace.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {//when user hits search
                if (p0 != null) {
                    searchText = p0.replace(" ", "_")
                    binding.progressBar.visibility = View.VISIBLE
                    searchLocation(searchText) //search that location
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }
        })
    }

    //call viewmodel searchPlace function
    private fun searchLocation(toSearch: String) {
        mapsActivityVM.searchPlace(toSearch, geoCoder)
    }

    //show bottom sheet, and get 3 args set -> placeId, currentLatLng and marker location
    private fun showBottomSheetFragment(placeId: Any, markerLocation: LatLng) {
        val placeIdString = placeId as String
        currentLatLng?.let {
            DetailedBottomSheet.newInstance(placeIdString, it, markerLocation)
                .show(supportFragmentManager, "DETAILED_BOTTOM_SHEET")
        }
    }
}

