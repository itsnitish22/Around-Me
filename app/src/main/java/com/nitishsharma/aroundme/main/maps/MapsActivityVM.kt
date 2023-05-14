package com.nitishsharma.aroundme.main.maps

import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.nitishsharma.aroundme.api.RetrofitInstance
import com.nitishsharma.aroundme.utils.Constants
import kotlinx.coroutines.launch
import java.io.IOException

class MapsActivityVM : ViewModel() {

    private val _placesOnMap = MutableLiveData<MutableList<Pair<String, Pair<String, LatLng>>>>()
    val placesOnMap: LiveData<MutableList<Pair<String, Pair<String, LatLng>>>>
        get() = _placesOnMap

    private val _searchedLocation = MutableLiveData<LatLng?>()
    val searchedLocation: LiveData<LatLng?>
        get() = _searchedLocation

    //getting nearby places according to the clicked category
    fun fetchNearbyPlaces(currentLocation: String, placeToSearch: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getNearbyPlaces( //hitting api
                    location = currentLocation,
                    type = placeToSearch.toLowerCase()
                )
                if (response.isSuccessful) { //if status is successful
                    //manipulate the data, and send it to live data
                    val pointsOnMap: MutableList<Pair<String, Pair<String, LatLng>>> =
                        mutableListOf()
                    response.body()?.results?.forEach {
                        pointsOnMap.add(
                            Pair(
                                it.place_id,
                                Pair(
                                    it.name,
                                    LatLng(it.geometry.location.lat, it.geometry.location.lng)
                                )
                            )
                        )
                    }
                    _placesOnMap.postValue(pointsOnMap)
                }
            } catch (e: Exception) {
                Log.e(Constants.ActivityNameForLogging.MAP_ACTIVITY_VM, e.toString())
            }
        }
    }

    //search a place according to the searched location
    fun searchPlace(toSearch: String, geocoder: Geocoder) {
        viewModelScope.launch {
            try {
                //getting searched location from geocoder
                val addresses = geocoder.getFromLocationName(toSearch, 1)
                val location =
                    addresses?.get(0)?.let { LatLng(it.latitude, addresses[0].longitude) }
                _searchedLocation.postValue(location)
            } catch (e: IOException) {
                Log.e(Constants.ActivityNameForLogging.MAP_ACTIVITY_VM, e.toString())
            }
        }
    }
}